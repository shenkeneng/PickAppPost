package com.frxs.PickApp;

import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.MathUtils;
import com.ewu.core.utils.SystemUtils;
import com.frxs.PickApp.greendao.Goods;
import com.frxs.PickApp.utils.ToastHelper;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

/**
 * Created by Chentie on 2017/3/1.
 */

public class PickingSimpleActivity extends PickingActivity {

    private QuickAdapter<Goods> mGoodsQuickAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_picking;
    }

    @Override
    protected QuickAdapter<Goods> initItem(final ListView lvPicking) {
        mGoodsQuickAdapter = new QuickAdapter<Goods>(PickingSimpleActivity.this, R.layout.item_pick_simple) {
            @Override
            protected void convert(final BaseAdapterHelper helper, final Goods item) {

                // 设置字体大小
                TextView tvProductName = helper.getView(R.id.tv_product_name);
                TextView tvGiftsFlag = helper.getView(R.id.tv_gifts_flag);
                TextView tvGoodsCount = helper.getView(R.id.tv_goods_count);
                if (SystemUtils.isTabletDevice(PickingSimpleActivity.this)) {
                    initFontSize(tvProductName, tvGiftsFlag, tvGoodsCount);
                }
                // 初始化列表时显示是否缺货
                helper.setText(R.id.tv_serial_number, String.valueOf(helper.getPosition() + 1));
                // 设置为全局的对象
                if (TextUtils.isEmpty(item.getIsGiftStr())) {
                    helper.setVisible(R.id.tv_gifts_flag, false);
                    helper.setText(R.id.tv_product_name, item.getProductName());
                } else {
                    helper.setVisible(R.id.tv_gifts_flag, true);
                    helper.setText(R.id.tv_gifts_flag, item.getIsGiftStr());
                    if (item.getIsGiftStr().length() > 1) {
                        helper.setText(R.id.tv_product_name, "          " + item.getProductName());
                    } else {
                        helper.setText(R.id.tv_product_name, "      " + item.getProductName());
                    }
                }

                // 设置单位字体大小
                String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
                tvGoodsCount.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getPickQty())) + pickUnit);
                setUnitFontSize(item, tvGoodsCount);

                // 商品是否拣货是否修改数量
                boolean isModify = !pickUnit.equals(item.getSaleUnit()) || (item.getSaleQty() != item.getPickQty());
                if (item.getIsPicked() == 1) {
                    tvGoodsCount.setTextColor(isModify ? getResources().getColor(R.color.frxs_yellow_dark) : getResources().getColor(R.color.frxs_black_light));// 已拣的数量致灰
                    tvGoodsCount.setBackgroundResource(isModify ? R.drawable.shape_green_border_rectangle : R.color.frxs_transition);
                    tvProductName.setTextColor(getResources().getColor(R.color.frxs_black_light));
                    helper.setBackgroundRes(R.id.tv_serial_number, (item.getPreQty() > item.getSaleQty() ? R.mipmap.icon_iten_lack_picked : R.mipmap.icon_iten_seriel_picked));
                } else {
                    tvGoodsCount.setTextColor(isModify ? getResources().getColor(R.color.frxs_yellow_dark) : getResources().getColor(R.color.frxs_green));// 未拣的数量变绿
                    tvGoodsCount.setBackgroundResource(isModify ? R.drawable.shape_green_border_rectangle : R.color.frxs_transition);
                    tvProductName.setTextColor(getResources().getColor(R.color.frxs_balck));
                    helper.setBackgroundRes(R.id.tv_serial_number, (item.getPreQty() > item.getSaleQty() ? R.mipmap.icon_iten_lack : R.mipmap.icon_iten_seriel));
                }

                tvGoodsCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lvPicking.setItemChecked(helper.getPosition(), true);
                        if (item.getIsPicked() == 1) {
                            ToastHelper.toastShort(PickingSimpleActivity.this, "请先修改拣货状态");
                        } else {
                            showChangeCountDialog(item);

                        }
                        initGoodInfoBottom(item, helper.getPosition() + 1);
                    }
                });
            }
        };

        return mGoodsQuickAdapter;
    }

    /**
     * 初始化底部商品配置信息栏
     * @param item
     * @param position
     */
    protected void initGoodInfoBottom(final Goods item, final int position) {
        TextView tvBarcode = (TextView) findViewById(R.id.tv_barcode);// 条码
        TextView tvGoodsNumber = (TextView) findViewById(R.id.tv_goods_number);// 货位
        TextView tvGoodsPackingQty = (TextView) findViewById(R.id.tv_goods_packing_qty);
        TextView tvGoodsCount1 = (TextView) findViewById(R.id.tv_goods_count_1);// 商品数量
        TextView tvPrice = (TextView) findViewById(R.id.tv_price);// 单价
        TextView tvSerialNumber1 = (TextView) findViewById(R.id.tv_serial_number_1);// 序号
        final TextView btnPickState = (TextView) findViewById(R.id.tv_pick_state);// 拣货状态
        TextView tvRemark = (TextView) findViewById(R.id.tv_remark);// 备注

        // 是否有备注 有备注就显示，无备注则隐藏
        if (!TextUtils.isEmpty(item.getRemark())){
            tvRemark.setVisibility(View.VISIBLE);
            tvRemark.setText("备注：" + item.getRemark());
        } else {
            tvRemark.setVisibility(View.GONE);
        }

        tvBarcode.setText("" + item.getBarCode().split(",")[0]);
        tvGoodsNumber.setText("" + item.getShelfCode());
        double packingQty = item.getPickPackingQty() == 0 ? item.getSalePackingQty() : item.getPickPackingQty();
        tvGoodsPackingQty.setText(String.valueOf(packingQty));
        tvSerialNumber1.setText(String.valueOf(position));

        //根据单位切换单价
        String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
        if (pickUnit.equals(item.getUnit())) { //这个表示切换到小单位
            tvPrice.setText("" + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getUnitPrice())));
        } else { //表示切换的大单位
            tvPrice.setText("" + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getSalePrice())));
        }
        tvGoodsCount1.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getPickQty())) + pickUnit);
        setUnitFontSize(item, tvGoodsCount1);

        if (item.getIsPicked() == 1) {
            btnPickState.setBackgroundResource(R.color.frxs_black_light);
            btnPickState.setText("已拣");
        } else {
            btnPickState.setBackgroundResource(R.color.frxs_green);
            btnPickState.setText("未拣");
        }
        btnPickState.setTextColor(getResources().getColor(R.color.white));

        btnPickState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick()){
                    return;
                }

                if (item.getIsPicked() == 1)// 表示已拣
                {
                    showCheckOutPermissionsDialog(item, btnPickState, R.color.frxs_green);
                } else { // 表示未拣
                    setPickedStatus(item);
                }
            }
        });

        // 点击展示商品名称与图片
        LinearLayout simpleBottomFl = (LinearLayout) findViewById(R.id.ll_picking);
        simpleBottomFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String photoUrl = item.getProductImageUrl400();
                if (TextUtils.isEmpty(photoUrl)) {
                    ToastHelper.toastShort(PickingSimpleActivity.this, "该商品没有图片");
                } else {
                    String placeholder = (!TextUtils.isEmpty(item.getBigUnitBarCode()) && !TextUtils.isEmpty(item.getBarCode())) ? "," : "";
                    Intent intent = new Intent(PickingSimpleActivity.this, PhotoLookActivity.class);
                    intent.putExtra("PHOTOURL", photoUrl);
                    intent.putExtra("BARCODE", item.getBarCode() + placeholder + item.getBigUnitBarCode());
                    intent.putExtra("PRODUCT_NAME", item.getProductName());
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 设置脚布局
     * @param lvPicking
     */
    @Override
    protected void addFooterView(ListView lvPicking) {
        LinearLayout itemFooterLl = (LinearLayout) getLayoutInflater().inflate(R.layout.item_footer, null);
        lvPicking.addFooterView(itemFooterLl);
        // 拣货完成提交订单
        TextView btnSubmitPickedGoods = (TextView) itemFooterLl.findViewById(R.id.btn_checked);
        TextView btnPrint = (TextView) itemFooterLl.findViewById(R.id.btn_print);

        btnSubmitPickedGoods.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }

        switch (view.getId()){
            case R.id.btn_checked: {// 点击提交
                finishPickProcess();
                break;
            }
            case R.id.btn_print:{// 点击打印
                doPrintAction();
            }
        }
    }

    /**
     * 设置单位字体大小
     * @param item
     * @param tvGoodsCount
     */
    private void setUnitFontSize(Goods item, TextView tvGoodsCount) {
        String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
        CharSequence tvGoodsCountText = tvGoodsCount.getText();
        SpannableString msp = new SpannableString(tvGoodsCountText);
        msp.setSpan(new AbsoluteSizeSpan(20, true), tvGoodsCountText.length() - pickUnit.length(), tvGoodsCountText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tvGoodsCount.setText(msp);
    }
}
