package com.frxs.PickApp;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.MD5;
import com.ewu.core.utils.MathUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.comms.GlobelDefines;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.model.PickingOrderList;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.frxs.PickApp.service.bluetooth.BluetoothService;
import com.frxs.PickApp.service.bluetooth.OnBluetoothLisenter;
import com.frxs.PickApp.service.bluetooth.OnPrintLisenter;
import com.frxs.PickApp.service.bluetooth.PrintDataService;
import com.frxs.PickApp.service.bluetooth.PrintHelper;
import com.frxs.PickApp.utils.ToastHelper;
import com.frxs.PickApp.widget.EmptyView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * 订单汇总页面
 */
public class OrderAllActivity extends FrxsActivity {

    private EditText etSearchOrder;// 订单搜索框

    private ListView lvOrder;// 订单列表ListView

    private TextView tvTitle;// 页面标题

    private TextView tvRefresh;// 标题栏右侧控件

    private EmptyView emptyView;

    /* 对象 */
    private QuickAdapter<PickingOrderList.PickingOrder> mOrderAdapter;// 订单列表适配器

    List<PickingOrderList.PickingOrder> orderList = new ArrayList<>();

    private BluetoothService bluetoothService;// 蓝牙服务

    private PrintDataService printDataService;// 打印服务

    private Order mOrder;// 当前订单

    private TextView tvPrint;

    private boolean isPrint = true;// 正在打印中标识

    private FloatingActionButton fabSwitchTab;// 悬浮按钮 跳转正在拣货页面
    
    private Dialog failedDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_all;
    }

    @Override
    protected void initViews() {
        /* 实例化控件属性 */
        etSearchOrder = (EditText) findViewById(R.id.et_search_order);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRefresh = (TextView) findViewById(R.id.tv_title_right);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        fabSwitchTab = (FloatingActionButton) findViewById(R.id.fab_switch_tab);
    }

    @Override
    protected void initData() {
        /* 初始化布局 */
        tvTitle.setText(R.string.status_all);

        // 适配器适配订单列表数据
        mOrderAdapter = new QuickAdapter<PickingOrderList.PickingOrder>(OrderAllActivity.this, R.layout.item_order_all) {
            @Override
            protected void convert(BaseAdapterHelper helper, final PickingOrderList.PickingOrder item) {
                if (item.isPrint()) {// 判断订单是否已打印 使用背景色区分显示
                    helper.setBackgroundRes(R.id.ll_bg, R.drawable.shape_circle_rectangle_yellow);
                } else {
                    helper.setBackgroundRes(R.id.ll_bg, R.drawable.shape_circle_rectangle);
                }
                helper.setText(R.id.tv_order_id, item.getOrderId());//订单ID
                helper.setText(R.id.tv_shop_name, item.getShopName());//门店名称
                helper.setText(R.id.tv_order_count, String.valueOf(item.getItemCount()));//商品行数
                helper.setText(R.id.tv_order_amount, MathUtils.twolittercountString(item.getTotalAmt()));//商品总金额
                helper.setText(R.id.station_number_tv, String.valueOf(item.getStationNumber()));//待装区编号
                // helper.setText(R.id.tv_pick_state, item.getPackingStatusStr());//该订单各货区拣货情况
                // 打印小票
                final TextView tvPrint = helper.getView(R.id.print_action_tv);
                helper.setOnClickListener(R.id.print_action_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPrint) {
                            isPrint = false;
                            reqOrderDetails(item.getOrderId(), tvPrint);
                        } else {
                            ToastUtils.show(OrderAllActivity.this, "正在打印中……");
                        }
                    }
                });

                if (bluetoothService.isOpen()) {
                    tvPrint.setText("开始打印");
                } else {
                    tvPrint.setText("打开蓝牙");
                }
                if (isPrint) {
                    tvPrint.setEnabled(isPrint);
                }
            }
        };
        lvOrder.setAdapter(mOrderAdapter);
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PickingOrderList.PickingOrder item = (PickingOrderList.PickingOrder)parent.getAdapter().getItem(position);
                if (null != item) {
                    Intent intent = new Intent(OrderAllActivity.this, OrderDetailsActivity.class);
                    intent.putExtra("ORDERID", item.getOrderId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemUtils.isNetworkAvailable(OrderAllActivity.this)) {
            reqAllOrderList();
        } else {
            ToastUtils.show(OrderAllActivity.this, "网络异常，请检查网络是否连接");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isPrint = true;
    }

    @Override
    protected void initEvent() {
        //注册蓝牙
        registerPrintReceiver();

        //刷新按钮
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqAllOrderList();
            }
        });

        // 搜索监听
        etSearchOrder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchOrder(s);
            }
        });

        fabSwitchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderId = getOrderID();
                Intent intent = new Intent(OrderAllActivity.this, FrxsApplication.versionSelectorClass);
                intent.putExtra("from", HomeActivity.TAG);
                intent.putExtra("SOURCETYPE", 0);
                intent.putExtra("ORDERID", orderId);
                startActivity(intent);
            }
        });
    }

    public void searchOrder(Editable searchText){
        if (!TextUtils.isEmpty(searchText)) {
            List<PickingOrderList.PickingOrder> mOrder = new ArrayList<PickingOrderList.PickingOrder>();
            for (int i = 0; i < orderList.size(); i++) {
                String strStationNum = orderList.get(i).getStationNumber() == null ? "" : String.valueOf(orderList.get(i).getStationNumber());
                if (orderList.get(i).getShopCode().contains(searchText) || strStationNum.contains(searchText)) {
                    mOrder.add(orderList.get(i));
                }
            }

            mOrderAdapter.replaceAll(mOrder);
            mOrderAdapter.notifyDataSetChanged();
        } else {
            mOrderAdapter.replaceAll(orderList);
            mOrderAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取订单详情进行打印小票操作
     * @param orderid
     * @param tvPrint
     */
    private void reqOrderDetails(final String orderid, final TextView tvPrint) {
        showProgressDialog();
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ShelfAreaCode", ui.getShelfAreaCode());
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("WID", ui.getWID());
        params.put("OrderId", orderid);

        getService().getAllOrderDetails(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Order>>() {
            @Override
            public void onResponse(ApiResponse<Order> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        mOrder = result.getData();
                        doPrintAction(tvPrint);
                    } else {
                        ToastUtils.show(OrderAllActivity.this, "获取订单信息失败");
                    }
                } else {
                    ToastUtils.show(OrderAllActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(OrderAllActivity.this, t.getMessage());
            }
        });
    }

    /**
     * 查询所有订单列表
     */
    private void reqAllOrderList() {
        showProgressDialog();
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitPickingOrderList"));
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("ShelfAreaCode", ui.getShelfAreaCode());
        params.put("WID", ui.getWID());
        getService().getAllOrderList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<PickingOrderList>>() {
            @Override
            public void onResponse(ApiResponse<PickingOrderList> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    emptyView.setVisibility(View.GONE);
                    PickingOrderList pol = result.getData();
                    if (null != pol) {
                        List<PickingOrderList.PickingOrder> pickingOrders = pol.getListData();
                        if (pickingOrders != null && pickingOrders.size() > 0) {
                            orderList.clear();
                            orderList.addAll(pickingOrders);
                            synOrderPrintList(orderList);
                            mOrderAdapter.replaceAll(orderList);
                            finishPrintProcess();
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setImageResource(R.mipmap.icon_visit_fail);
                            emptyView.setMode(EmptyView.MODE_NODATA);
                        }
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setImageResource(R.mipmap.icon_visit_fail);
                        emptyView.setMode(EmptyView.MODE_NODATA);
                    }
                    if (etSearchOrder.getText() != null){
                        searchOrder(etSearchOrder.getText());
                    }
                } else {
//                    ToastUtils.show(OrderAllActivity.this, result.getInfo());
                    ToastUtils.show(OrderAllActivity.this, "暂无数据");// 接口存在问题（临发版 待查询） 暂时先在前端提示暂无数据
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                    emptyView.setMode(EmptyView.MODE_NODATA);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PickingOrderList>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setImageResource(R.mipmap.icon_visit_fail);
                emptyView.setMode(EmptyView.MODE_ERROR);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reqAllOrderList();
                    }
                });
            }
        });
    }

    /**
     * 同步集合中订单打印标识
     * @param pickingOrders
     */
    private void synOrderPrintList(List<PickingOrderList.PickingOrder> pickingOrders) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        String orderId = helper.getString(GlobelDefines.KEY_ORDER_PRINT, "");
        String newOrderID = "";
        for (PickingOrderList.PickingOrder order : pickingOrders) {
            if (orderId.contains(order.getOrderId())) {
                order.setPrint(true);
                newOrderID += order.getOrderId() + ",";
            }
        }
        helper.putValue(GlobelDefines.KEY_ORDER_PRINT, newOrderID);
    }

    protected void doPrintAction(TextView print) {
        this.tvPrint = print;
        if (bluetoothService.isOpen()) {
            String macAddress = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME).getString(Config.KEY_BT_MAC, "");
            if (bluetoothService.checkBluetoothAddress(macAddress)) {
                BluetoothDevice btDevice = bluetoothService.getRemoteDevice(macAddress);
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    try {
                        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        createBondMethod.invoke(btDevice);
                    } catch (Exception e) {
                        ToastHelper.toastShort(this, "配对失败");
                    }
                } else if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    printDataService = PrintHelper.getPrintDataService(this);
                    printDataService.setBtDevice(btDevice);
                    if (mOrder != null) {
                        tvPrint.setEnabled(false);
                        tvPrint.setText("正在连接");
                        printDataService.setPrintData(mOrder);
                        printDataService.print();
                        // 点击了打印按钮则标记为已打印
                        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
                        String orderIdList = helper.getString(GlobelDefines.KEY_ORDER_PRINT, "");
                        if (!orderIdList.contains(mOrder.getOrderId())) {
                            helper.putValue(GlobelDefines.KEY_ORDER_PRINT, (TextUtils.isEmpty(orderIdList)) ? mOrder.getOrderId() : orderIdList + "," + mOrder.getOrderId());
                        }
                        for (PickingOrderList.PickingOrder order : orderList) {
                            if (order.getOrderId().equals(mOrder.getOrderId())) {
                                order.setPrint(true);
                                break;
                            }
                        }
                        finishPrintProcess();
                    }
                }
            } else {
                ToastHelper.toastShort(this, "无效的蓝牙MAC地址，请重新配置");
                Intent intent = new Intent(this, SearchBluetoothActivity.class);
                startActivity(intent);
            }
        } else {
            isPrint = true;
            ToastHelper.toastShort(this, "蓝牙未打开");
            bluetoothService.openBluetooth(this);
        }
    }

    private void registerPrintReceiver() {
        bluetoothService = PrintHelper.getBluetoothService(this);
        bluetoothService.registerReceiver();
        bluetoothService.setBluetoothLisenter(new OnBluetoothLisenter() {
            @Override
            public void onBluetoothStateChanged(boolean isOpened) {
                isPrint = true;
                mOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBluetoothDiscoveryFinished(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {

            }

            @Override
            public void onBluetoothDiscoveryFound(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {

            }

            @Override
            public void onBluetoothDiscoveryStarted() {

            }

            @Override
            public void onBluetoothBondStateChanged(BluetoothDevice device, boolean isSuccess) {
                printDataService.setBtDevice(device);
                if (isSuccess) {
                    if (mOrder != null) {
                        printDataService.setPrintData(mOrder);
                        printDataService.print();
                        // 点击了打印按钮则标记为已打印
                        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(OrderAllActivity.this, GlobelDefines.PREFS_NAME);
                        String orderIdList = helper.getString(GlobelDefines.KEY_ORDER_PRINT, "");
                        if (!orderIdList.contains(mOrder.getOrderId())) {
                            helper.putValue(GlobelDefines.KEY_ORDER_PRINT, (TextUtils.isEmpty(orderIdList)) ? mOrder.getOrderId() : orderIdList + "," + mOrder.getOrderId());
                        }
                        for (PickingOrderList.PickingOrder order : orderList) {
                            if (order.getOrderId().equals(mOrder.getOrderId())) {
                                order.setPrint(true);
                                break;
                            }
                        }
                        finishPrintProcess();
                    }
                } else {
                    isPrint = true;
                }
            }
        });
        printDataService = PrintHelper.getPrintDataService(this);
        printDataService.setPrintLisenter(new OnPrintLisenter() {
            @Override
            public void onConnectedStateChanged(boolean isConnected) {
                tvPrint.setEnabled(!isConnected);
                if (isConnected) {
                    tvPrint.setEnabled(false);
                    tvPrint.setText("正在打印");
                } else {
                    dismissProgressDialog();
                    tvPrint.setEnabled(true);
                    tvPrint.setText("开始打印");
                    mOrderAdapter.notifyDataSetChanged();
                    isPrint = true;
                }
            }

            @Override
            public void onConnectFailed(int iReturn) {
                dismissProgressDialog();
                isPrint = true;
                tvPrint.setEnabled(true);
                tvPrint.setText("开始打印");
                mOrderAdapter.notifyDataSetChanged();
                showFailedDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.unregisterReceiver();
    }

    private void showFailedDialog() {
        if (failedDialog == null) {
            failedDialog = new Dialog(OrderAllActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        }
        failedDialog.setContentView(R.layout.dialog_continue_pick);// 自定义对话框
        failedDialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        TextView tvContent = (TextView) failedDialog.findViewById(R.id.tv_dialog_content);
        Button btnCommit = (Button) failedDialog.findViewById(R.id.btn_commit);
        Button btnSettingBt = (Button) failedDialog.findViewById(R.id.btn_cancel);
        btnSettingBt.setText("设置蓝牙");
        tvContent.setText("连接打印机失败，请检查打印机状态或重新设置蓝牙，再打印。");
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedDialog.dismiss();
            }
        });
        btnSettingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failedDialog.dismiss();
                Intent intent = new Intent(OrderAllActivity.this, SearchBluetoothActivity.class);
                startActivity(intent);
            }
        });
        failedDialog.show();
    }

    private void finishPrintProcess() {
        for (int i = 0; i < orderList.size(); i++) {
            if (!orderList.get(i).isPrint()) {
                if (i == 0) {
                    lvOrder.setSelection(i);
                    break;
                } else {
                    lvOrder.setSelection(i - 1);
                    break;
                }
            } else if (i == orderList.size() - 1) {
                lvOrder.setSelection(i - 1);
            }
        }
    }
}