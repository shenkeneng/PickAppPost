package com.frxs.PickApp;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.ImeUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.comms.GlobelDefines;
import com.frxs.PickApp.greendao.Goods;
import com.frxs.PickApp.model.Order;
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
import java.util.List;
import retrofit2.Call;

/**
 * 完成拣货订单 - 商品详情页面
 * Created by Chentie on 2017/2/6.
 */

public class OrderGoodsActivity extends FrxsActivity {

    private ListView lvGoods;
    private QuickAdapter<Goods> quickAdapter;
    private EmptyView emptyView;
    private TextView tvTitle;
    private TextView ivPrint;// 打印按钮
    private BluetoothService bluetoothService;// 蓝牙服务
    private PrintDataService printDataService;// 打印服务
    private Order mOrder;// 当前订单
    private Dialog failedDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_goods;
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivPrint = (TextView) findViewById(R.id.iv_title_right);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        lvGoods = (ListView) findViewById(R.id.lv_goods);
    }

    @Override
    protected void initData() {
        tvTitle.setText("商品详情");
        quickAdapter = new QuickAdapter<Goods>(OrderGoodsActivity.this, R.layout.item_order_goods) {
            @Override
            protected void convert(BaseAdapterHelper helper, Goods item) {
                /**
                 * 显示商品状态（不显示为正常商品，显示分为赠品、搭售）
                 */
                String isGiftStr = item.getIsGiftStr();
                if (!TextUtils.isEmpty(isGiftStr)) {
                    helper.setVisible(R.id.tv_gifts_flag, true);
                    helper.setText(R.id.tv_gifts_flag, isGiftStr);
                } else {
                    helper.setVisible(R.id.tv_gifts_flag, false);
                }

                /**
                 * 设置商品数量单位
                 */
                helper.setText(R.id.tv_good_count, item.getPickQty() + item.getSaleUnit());
                // 设置商品名称
                helper.setText(R.id.tv_product_name, (TextUtils.isEmpty(item.getProductName()) ? "" : item.getProductName()));
                // 设置商品条码
                helper.setText(R.id.tv_barcode, "条码：" + (item.getBarCode().split(",")[0]));
                // 设置商品编码
                helper.setText(R.id.tv_goods_code, "编码：" + (item.getSKU()));
            }
        };

        lvGoods.setAdapter(quickAdapter);
        addFooterView();
        //初始化蓝牙打印服务
        initBtPrinter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemUtils.isNetworkAvailable(OrderGoodsActivity.this)) {
            String orderid = getIntent().getStringExtra("ORDERID");
            getOrderGoods(orderid);
        } else {
            ToastUtils.show(OrderGoodsActivity.this, "网络异常，请检查网络是否连接");
        }
    }

    /**
     * 获取当前订单的商品详情
     *
     * @param orderid
     */
    private void getOrderGoods(final String orderid) {
        showProgressDialog();
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("WID", ui.getWID());
        if (!TextUtils.isEmpty(orderid)) {// 可传可不传
            params.put("OrderId", orderid);
        }
        getService().getPickedOrderGoods(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Order>>() {
            @Override
            public void onResponse(ApiResponse<Order> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")){
                    if (result.getData() != null){
                        Order data = result.getData();
                        mOrder = data;
                        if (data.getProductData() != null && data.getProductData().size() > 0){
                            emptyView.setVisibility(View.GONE);
                            List<Goods> productData = data.getProductData();
                            quickAdapter.replaceAll(productData);
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setImageResource(R.mipmap.icon_visit_fail);
                            emptyView.setMode(emptyView.MODE_NODATA);
                        }
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setImageResource(R.mipmap.icon_visit_fail);
                        emptyView.setMode(emptyView.MODE_NODATA);
                    }
                } else {
                    ToastUtils.show(OrderGoodsActivity.this, result.getInfo());
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                    emptyView.setMode(EmptyView.MODE_NODATA);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setImageResource(R.mipmap.icon_visit_fail);
                emptyView.setMode(EmptyView.MODE_ERROR);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getOrderGoods(orderid);
                    }
                });
            }
        });
    }

    @Override
    protected void initEvent() {
        ivPrint.setOnClickListener(this);
    }

    private void addFooterView() {
        TextView footerView = new TextView(OrderGoodsActivity.this);
        footerView.setText("亲，已经到底了~");
        footerView.setPadding(10, 10, 10, 10);
        footerView.setGravity(Gravity.CENTER);
        lvGoods.addFooterView(footerView);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.iv_title_right:{
                doPrintAction();
                break;
            }
        }
    }

    protected void doPrintAction() {
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
                        ivPrint.setEnabled(false);
                        ivPrint.setText("正在连接");
                        printDataService.setPrintData(mOrder);
                        printDataService.print();
                        // 点击了打印按钮则标记为已打印
                        FrxsApplication.getInstance().setPrinted(mOrder.getOrderId());
                    }
                }
            } else {
                ToastHelper.toastShort(this, "无效的蓝牙MAC地址，请重新配置");
                Intent intent = new Intent(this, SearchBluetoothActivity.class);
                startActivity(intent);
            }
        } else {
            ToastHelper.toastShort(this, "蓝牙未打开");
            bluetoothService.openBluetooth(this);
        }
    }

    private void initBtPrinter() {
        bluetoothService = PrintHelper.getBluetoothService(this);
        bluetoothService.registerReceiver();
        bluetoothService.setBluetoothLisenter(new OnBluetoothLisenter() {
            @Override
            public void onBluetoothStateChanged(boolean isOpened) {
                if (isOpened) {
                    ivPrint.setText("开始打印");
                } else {
                    ivPrint.setText("打开蓝牙");
                }
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
                ImeUtils.dismissIme(ivPrint);
                printDataService.setBtDevice(device);
                if (mOrder != null) {
                    printDataService.setPrintData(mOrder);
                    printDataService.print();
                    // 点击了打印按钮则标记为已打印
                    FrxsApplication.getInstance().setPrinted(mOrder.getOrderId());
                }
            }
        });

        if (bluetoothService.isOpen()) {
            ivPrint.setText("开始打印");
        } else {
            ivPrint.setText("打开蓝牙");
        }

        printDataService = PrintHelper.getPrintDataService(this);
        printDataService.setPrintLisenter(new OnPrintLisenter() {
            @Override
            public void onConnectedStateChanged(boolean isConnected) {
                ivPrint.setEnabled(!isConnected);
                if (isConnected) {
                    ivPrint.setText("正在打印");
                } else {
                    ivPrint.setText("开始打印");
                }
            }

            @Override
            public void onConnectFailed(int iReturn) {
                ivPrint.setEnabled(true);
                ivPrint.setText("开始打印");
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
            failedDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
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
                Intent intent = new Intent(OrderGoodsActivity.this, SearchBluetoothActivity.class);
                startActivity(intent);
            }
        });
        failedDialog.show();
    }
}
