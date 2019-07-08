package com.frxs.PickApp;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
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

public class PickingClassicalActivity extends PickingActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_classical_picking;
    }


    @Override
    protected QuickAdapter<Goods> initItem(ListView lvPicking) {
        QuickAdapter<Goods> mGoodsQuickAdapter = new QuickAdapter<Goods>(PickingClassicalActivity.this, R.layout.item_pick) {

            @Override
            protected void convert(final BaseAdapterHelper helper, final Goods item) {
                // 设置字体大小
                TextView tvProductName = helper.getView(R.id.tv_product_name);
                TextView tvGiftsFlag = helper.getView(R.id.tv_gifts_flag);
                TextView tvGoodsCount = helper.getView(R.id.tv_goods_count);
                if (SystemUtils.isTabletDevice(PickingClassicalActivity.this)) {
                    initFontSize(tvProductName, tvGiftsFlag, tvGoodsCount);
                }
                // 初始化列表时显示是否缺货
                helper.setBackgroundRes(R.id.tv_serial_number, (item.getPreQty() > item.getSaleQty() ? R.mipmap.icon_iten_blue_lack : R.mipmap.icon_iten_blue_seriel));
                helper.setText(R.id.tv_serial_number, String.valueOf(helper.getPosition() + 1));
                // 初始化备注：有则显示 无则不显示
                helper.setText(R.id.tv_remark, "备注: " + item.getRemark());
                helper.setVisible(R.id.tv_remark, (TextUtils.isEmpty(item.getRemark()) ? false : true));
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
                String barcode = "";
                String[] barCodeArray = item.getBarCode().split(",");
                if (null != barCodeArray && barCodeArray.length > 0) {
                    barcode = barCodeArray[0];
                }
                helper.setText(R.id.tv_barcode, "条码: " + barcode);
                helper.setText(R.id.tv_goods_number, "货位: " + item.getShelfCode());
                double packingQty = item.getPickPackingQty() == 0 ? item.getSalePackingQty() : item.getPickPackingQty();
                helper.setText(R.id.tv_goods_packing_qty, "包装数：" + packingQty);

                //根据单位切换单价
                String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
                if (pickUnit.equals(item.getUnit())) {
                    helper.setText(R.id.tv_price, "单价: " + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getUnitPrice())));
                } else {
                    helper.setText(R.id.tv_price, "单价: " + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getSalePrice())));
                }
                tvGoodsCount.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(item.getPickQty())) + pickUnit);
                if (!pickUnit.equals(item.getSaleUnit()) || (item.getSaleQty() != item.getPickQty())) {// 已经修改数量或者单位
                    tvGoodsCount.setBackgroundColor(getResources().getColor(R.color.frxs_blue_light));
                } else {
                    tvGoodsCount.setBackgroundColor(getResources().getColor(R.color.white));
                }


                final TextView btnPickState = helper.getView(R.id.tv_pick_state);

                if (item.getIsPicked() == 1) {
                    btnPickState.setBackgroundResource(R.mipmap.icon_gray_picked);
                    tvGoodsCount.setTextColor(getResources().getColor(R.color.gray));// 已拣的数量致灰
                } else {
                    btnPickState.setBackgroundResource(R.mipmap.icon_nopick);
                    tvGoodsCount.setTextColor(getResources().getColor(R.color.frxs_red));// 未拣的数量变红
                }

                btnPickState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getIsPicked() == 1)// 表示已拣
                        {
                            showCheckOutPermissionsDialog(item, btnPickState, R.mipmap.icon_nopick);
                        } else // 表示未拣
                        {
                            setPickedStatus(item);
                        }
                    }
                });


                helper.setOnClickListener(R.id.tv_goods_count, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getIsPicked() == 1) {
                            ToastHelper.toastShort(PickingClassicalActivity.this, "请先修改拣货状态");
                        } else {
                            showChangeCountDialog(item);
                        }
                    }
                });

                helper.setOnClickListener(R.id.tv_product_name, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String photoUrl = item.getProductImageUrl400();
                        if (TextUtils.isEmpty(photoUrl)) {
                            ToastHelper.toastShort(PickingClassicalActivity.this, "该商品没有图片");
                        } else {
                            String placeholder = (!TextUtils.isEmpty(item.getBigUnitBarCode()) && !TextUtils.isEmpty(item.getBarCode())) ? "," : "";
                            Intent intent = new Intent(PickingClassicalActivity.this, PhotoLookActivity.class);
                            intent.putExtra("PHOTOURL", photoUrl);
                            intent.putExtra("BARCODE", item.getBarCode() + placeholder + item.getBigUnitBarCode());
                            intent.putExtra("PRODUCT_NAME", item.getProductName());
                            startActivity(intent);
                        }
                    }
                });
            }
        };

        return mGoodsQuickAdapter;
    }

    @Override
    protected void addFooterView(ListView lvPicking) {
        TextView footerView = new TextView(PickingClassicalActivity.this);
        footerView.setText("亲，已经到底了~");
        footerView.setPadding(10, 10, 10, 10);
        footerView.setGravity(Gravity.CENTER);
        lvPicking.addFooterView(footerView);
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
        }
    }
}
