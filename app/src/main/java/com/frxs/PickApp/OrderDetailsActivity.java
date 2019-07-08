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
import com.ewu.core.utils.MathUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.comms.GlobelDefines;
import com.frxs.PickApp.greendao.Goods;
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
import java.util.List;

import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/5.
 */

public class OrderDetailsActivity extends FrxsActivity {

    private ListView lvOrder;// 订单列表ListView

    private TextView tvTitle;// 页面标题

    private TextView tvReresh;// 刷新按钮

    private EmptyView emptyView;

    private QuickAdapter<Goods> quickAdapter;

    private String orderid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_details;
    }

    @Override
    protected void initViews() {
        lvOrder = (ListView) findViewById(R.id.lv_order);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvReresh = (TextView) findViewById(R.id.tv_title_right);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
    }

    @Override
    protected void initData() {
        tvTitle.setText(R.string.order_deatils);
        // 适配器适配订单列表数据
        quickAdapter = new QuickAdapter<Goods>(OrderDetailsActivity.this, R.layout.item_good_info) {
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

                // 设置商品名称
                helper.setText(R.id.tv_product_name, (TextUtils.isEmpty(item.getProductName()) ? "" : item.getProductName()));
                // 设置商品条码
                helper.setText(R.id.tv_barcode, String.format(getString(R.string.good_barcode), item.getBarCode().split(",")[0]));
                // 设置商品编码
                helper.setText(R.id.tv_goods_code, String.format(getString(R.string.good_code), item.getSKU()));
                // 设置商品货位
                helper.setText(R.id.tv_shelf_code, String.format(getString(R.string.good_shelfcode), item.getShelfCode()));
                // 设置拣货标识
                if (TextUtils.isEmpty(item.getPickTime())){
                    helper.setText(R.id.tv_good_isPick, "未拣");
                    helper.setTextColor(R.id.tv_good_isPick, getResources().getColor(R.color.frxs_black_light));
                    // 设置商品数量 未拣显示预订商品数量 + 配送单位
                    helper.setText(R.id.tv_goods_count, String.format(getString(R.string.good_count), MathUtils.twolittercountString(item.getPreQty()), item.getSaleUnit()));
                } else {
                    helper.setText(R.id.tv_good_isPick, "已拣");
                    helper.setTextColor(R.id.tv_good_isPick, getResources().getColor(R.color.frxs_red));
                    // 设置商品数量 已拣显示拣货商品数量 + 配送单位
                    helper.setText(R.id.tv_goods_count, String.format(getString(R.string.good_count), MathUtils.twolittercountString(item.getPickQty()), item.getSaleUnit()));
                }

            }
        };

        lvOrder.setAdapter(quickAdapter);
        addFooterView();
    }

    @Override
    protected void initEvent() {
        tvReresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_title_right: {
                reqOrderDetails();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemUtils.isNetworkAvailable(OrderDetailsActivity.this)) {
            orderid = getIntent().getStringExtra("ORDERID");
            reqOrderDetails();
        } else {
            ToastUtils.show(OrderDetailsActivity.this, "网络异常，请检查网络是否连接");
        }
    }

    private void reqOrderDetails() {
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
                        Order data = result.getData();
                        if (data.getProductData() != null && data.getProductData().size() > 0) {
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
                    ToastUtils.show(OrderDetailsActivity.this, result.getInfo());
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
                        reqOrderDetails();
                    }
                });
            }
        });
    }

    private void addFooterView() {
        TextView footerView = new TextView(OrderDetailsActivity.this);
        footerView.setText("亲，已经到底了~");
        footerView.setPadding(10, 10, 10, 10);
        footerView.setGravity(Gravity.CENTER);
        lvOrder.addFooterView(footerView);

    }
}
