package com.frxs.PickApp;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ewu.core.utils.MD5;
import com.ewu.core.utils.MathUtils;
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
 * 等待拣货订单页面
 */
public class OrderWaitingActivity extends FrxsActivity {
    /* 控件 */
    private EditText etSearchOrder;// 订单搜索框

    private ListView lvOrder;// 订单列表ListView

    private TextView tvTitle;// 页面标题

    private TextView tvRefresh;// 标题栏右侧控件

    private EmptyView emptyView;

    /* 对象 */
    private QuickAdapter<PickingOrderList.PickingOrder> mOrderAdapter;// 订单列表适配器

    List<PickingOrderList.PickingOrder> orderList = new ArrayList<>();
    private TextView tvStationNumber;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_waiting;
    }

    @Override
    protected void initViews() {
        /* 实例化控件属性 */
        etSearchOrder = (EditText) findViewById(R.id.et_search_order);
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRefresh = (TextView) findViewById(R.id.tv_title_right);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        tvStationNumber = (TextView) findViewById(R.id.tv_station_number);
    }

    @Override
    protected void initData() {
        /* 初始化布局 */
        tvTitle.setText(R.string.status_be_picking);

        // 适配器适配订单列表数据
        mOrderAdapter = new QuickAdapter<PickingOrderList.PickingOrder>(OrderWaitingActivity.this, R.layout.item_order_waiting) {
            @Override
            protected void convert(BaseAdapterHelper helper, final PickingOrderList.PickingOrder item) {
                // 数据赋值
                helper.setText(R.id.tv_order_id, item.getOrderId());//订单ID
                helper.setText(R.id.tv_shop_name, item.getShopName());//门店名称
                helper.setText(R.id.tv_order_line, item.getLineName());//线路名称
                helper.setText(R.id.tv_order_count, String.valueOf(item.getItemCount()));//商品行数
                helper.setText(R.id.tv_order_amount, MathUtils.twolittercountString(item.getTotalAmt()));//商品总金额
                helper.setText(R.id.tv_station_number, String.valueOf(item.getStationNumber()));//待装区编号
                //进入正在拣货UI
                helper.setOnClickListener(R.id.ll_waiting_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myShowDialog(item.getOrderId());
                    }
                });
            }
        };
        lvOrder.setAdapter(mOrderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemUtils.isNetworkAvailable(OrderWaitingActivity.this)) {
            reqWaitingOrderList();
        } else {
            ToastUtils.show(OrderWaitingActivity.this, "网络异常，请检查网络是否连接");
        }
    }

    @Override
    protected void initEvent() {
        //刷新按钮
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqWaitingOrderList();
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
                if (orderList.get(i).getShopCode().contains(searchText) || orderList.get(i).getLineName().contains(searchText)) {
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
     * 对话框
     *
     * @param itemOrderId
     */
    private void myShowDialog(final String itemOrderId) {
        final Dialog dialog = new Dialog(OrderWaitingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_continue_pick);// 自定义对话框
        dialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        Button btnCommit = (Button) dialog.findViewById(R.id.btn_commit);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_dialog_content);
        tvContent.setText("是否选择订单："+itemOrderId+" 进行拣货？");
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(itemOrderId)) {
                    Intent intent = new Intent(OrderWaitingActivity.this, FrxsApplication.versionSelectorClass);
                    intent.putExtra("ORDERID", itemOrderId);
                    intent.putExtra("SOURCETYPE", 1);
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
     * 查询等待拣货订单列表
     */
    private void reqWaitingOrderList() {
        showProgressDialog();
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitPickingOrderList"));
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("WID", ui.getWID());
        getService().PostWaitingOrderList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<PickingOrderList>>() {
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
                    ToastUtils.show(OrderWaitingActivity.this, result.getInfo());
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
                        reqWaitingOrderList();
                    }
                });
            }
        });
    }

}