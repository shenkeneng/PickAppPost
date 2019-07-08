package com.frxs.PickApp;

import android.app.Dialog;
import android.content.Intent;
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
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.model.PickingOrderList;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.frxs.PickApp.widget.EmptyView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * 正在拣货订单页面
 * Created by Endoon on 2016/3/31.
 */
public class OrderPickingActivity extends FrxsActivity {
    /* 控件 */
    private EditText etSearchOrder;// 订单搜索框

    private ListView lvOrder;// 订单列表ListView

    private TextView tvTitle;// 页面标题

    private TextView tvRefresh;// 标题栏右侧控件

    private EmptyView emptyView;

    /* 对象 */
    private QuickAdapter<PickingOrderList.PickingOrder> mOrderAdapter;// 订单列表适配器

    List<PickingOrderList.PickingOrder> orderList = new ArrayList<>();

    private String orderId = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_picking;
    }

    @Override
    protected void initViews() {
        /* 实例化控件属性 */
        etSearchOrder = (EditText) findViewById(R.id.et_search_order);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRefresh = (TextView) findViewById(R.id.tv_title_right);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
    }

    @Override
    protected void initData() {
        /* 初始化布局 */
        tvTitle.setText("正在拣货订单列表");

        orderId = getOrderID();

        // 适配器适配订单列表数据
        mOrderAdapter = new QuickAdapter<PickingOrderList.PickingOrder>(OrderPickingActivity.this, R.layout.item_order_picking) {
            @Override
            protected void convert(BaseAdapterHelper helper, PickingOrderList.PickingOrder item) {
                // 数据赋值
                helper.setText(R.id.tv_order_id, item.getOrderId());
                helper.setText(R.id.tv_shop_name, item.getShopName());
                helper.setText(R.id.tv_picker_name, item.getPickUserName());
                helper.setText(R.id.tv_station_number, item.getStationNumber() == null ? "" : String.valueOf(item.getStationNumber()));

                // 当前正在拣货的订单标红
                if (!TextUtils.isEmpty(orderId) && orderId.equals(item.getOrderId())) {
                    helper.setTextColor(R.id.tv_order_id, getResources().getColor(R.color.frxs_red));
                    helper.setTextColor(R.id.tv_shop_name, getResources().getColor(R.color.frxs_red));
                    helper.setTextColor(R.id.tv_picker_name, getResources().getColor(R.color.frxs_red));
                    helper.setTextColor(R.id.tv_station_number, getResources().getColor(R.color.frxs_red));
                } else {
                    helper.setTextColor(R.id.tv_order_id, getResources().getColor(R.color.frxs_black_dark));
                    helper.setTextColor(R.id.tv_shop_name, getResources().getColor(R.color.frxs_black_dark));
                    helper.setTextColor(R.id.tv_picker_name, getResources().getColor(R.color.frxs_black_dark));
                    helper.setTextColor(R.id.tv_station_number, getResources().getColor(R.color.frxs_black_dark));
                }
            }
        };
        lvOrder.setAdapter(mOrderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemUtils.isNetworkAvailable(OrderPickingActivity.this)) {
            reqPickingOrderList();
        } else {
            ToastUtils.show(OrderPickingActivity.this, "网络异常，请检查网络是否连接");
        }
    }

    @Override
    protected void initEvent() {
        //刷新按钮
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqPickingOrderList();
            }
        });
        // 点击Item
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
                if (userInfo != null && 1 == userInfo.getIsMaster()) { //组长挑单，进行二次确认
                    confirmDialog(mOrderAdapter.getItem(position));
                } else { //非组长不能进行挑单
                    promptDialog("非组长权限不能挑选订单拣货");
                }
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
     * 挑单二次确认对话框
     *
     * @param pickingOrder
     */
    private void confirmDialog(PickingOrderList.PickingOrder pickingOrder) {
        final Dialog dialog = new Dialog(OrderPickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_continue_pick);// 自定义对话框
        dialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        Button btnCommit = (Button) dialog.findViewById(R.id.btn_commit);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_dialog_content);
        // 如果当前选择的订单和数据库的订单相同，则显示第一种提示语，否则第二种
        final String itemOrderId = pickingOrder.getOrderId();
        if (!TextUtils.isEmpty(itemOrderId) && itemOrderId.equals(orderId)) {
            tvContent.setText("是否选择该订单继续拣货？");
        } else {
            String content = String.format("该订单%1$s拣货员正在拣货，是否继续进行拣货？", pickingOrder.getPickUserName());
            tvContent.setText(content);
        }
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(itemOrderId)) {
                    Intent intent = new Intent(OrderPickingActivity.this, FrxsApplication.versionSelectorClass);
                    intent.putExtra("ORDERID", itemOrderId);
                    intent.putExtra("SOURCETYPE", 0);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 查询正在拣货订单列表
     */
    private void reqPickingOrderList() {
        showProgressDialog();

        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetAtPickingOrderList"));
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("WID", ui.getWID());
        getService().PostPickingOrderList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<PickingOrderList>>() {
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
                            mOrderAdapter.replaceAll(pickingOrders);
                            orderList.addAll(pickingOrders);
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
                    ToastUtils.show(OrderPickingActivity.this, result.getInfo());
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
                        reqPickingOrderList();
                    }
                });
            }
        });
    }
}
