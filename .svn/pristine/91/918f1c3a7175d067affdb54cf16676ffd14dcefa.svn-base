package com.frxs.PickApp;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ewu.core.utils.DisplayUtil;
import com.frxs.PickApp.greendao.Goods;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.widget.EmptyView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.List;

/**
 * Created by Endoon on 2016/5/25.
 */
public class CommodityListActivity extends FrxsActivity {
    private ListView mGoodsPickedList;

    private TextView tvTitle;

    private LinearLayout llPickedBrock;

    private EmptyView emptyView;

    private TextView tvOrderId;

    private TextView tvStationNum;

    private Order mOrder;

    private QuickAdapter<Goods> mProductAdapter;


    private LinearLayout llCommodityBrock;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picked;
    }

    @Override
    protected void initViews() {
        mGoodsPickedList = (ListView) findViewById(R.id.lv_goods_picked);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        llPickedBrock = (LinearLayout) findViewById(R.id.ll_picked_brock);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvStationNum = (TextView) findViewById(R.id.tv_station_num);
        llCommodityBrock = (LinearLayout) findViewById(R.id.tv_commodity_brock);
    }

    @Override
    protected void initData() {
        tvTitle.setText("商品清单");
        llCommodityBrock.setVisibility(View.GONE);
        final Intent intent = getIntent();
        if (intent != null) {
            mOrder = (Order) intent.getSerializableExtra("ORDER");
        }

        mProductAdapter = new QuickAdapter<Goods>(CommodityListActivity.this, R.layout.item_commodity) {
            @Override
            protected void convert(BaseAdapterHelper helper, Goods item) {
                String name = item.getProductName();
                if (TextUtils.isEmpty(name)) {
                    name = "";
                }

                String preUnit = item.getSaleUnit();
                if (TextUtils.isEmpty(preUnit)) {
                    preUnit = "";
                }

                String unit = item.getUnit();
                if (TextUtils.isEmpty(unit)) {
                    unit = "";
                }

                helper.setText(R.id.tv_goods_name, name);
                helper.setText(R.id.tv_preqty, "订货数：" + DisplayUtil.subZeroAndDot(String.valueOf(item.getPreQty())) + preUnit);
                helper.setText(R.id.tv_salepickqty, "(1*" + item.getSalePackingQty() + unit + ")");
            }
        };
        mGoodsPickedList.setAdapter(mProductAdapter);

        if (mOrder != null) {
            String orderid = mOrder.getOrderId();
            if (!TextUtils.isEmpty(orderid))
            {
                tvOrderId.setText(orderid);
            }

            String station = mOrder.getStationNumber();
            if (!TextUtils.isEmpty(station))
            {
                tvStationNum.setText(station);
            }

            List<Goods> goodses = mOrder.getProductData();
            if (goodses != null && goodses.size() > 0) {
                emptyView.setVisibility(View.GONE);
                addFooterView();
                mProductAdapter.replaceAll(goodses);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setImageResource(R.mipmap.icon_visit_fail);
                emptyView.setMode(EmptyView.MODE_NODATA);
            }
        }
    }


    private void addFooterView()
    {
        TextView footerView = new TextView(CommodityListActivity.this);
        footerView.setText("亲，已经到底了~");
        footerView.setPadding(10,10,10,10);
        footerView.setGravity(Gravity.CENTER);
        mGoodsPickedList.addFooterView(footerView);
    }

    @Override
    protected void initEvent() {

    }
}
