package com.frxs.PickApp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.ImeUtils;
import com.ewu.core.utils.MathUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.comms.GlobelDefines;
import com.frxs.PickApp.greendao.DBHelper;
import com.frxs.PickApp.greendao.Goods;
import com.frxs.PickApp.interfaces.OnResponseListener;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.model.PickData;
import com.frxs.PickApp.model.Product;
import com.frxs.PickApp.model.ProductData;
import com.frxs.PickApp.model.SubmitDatas;
import com.frxs.PickApp.model.SubmitResult;
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
import com.frxs.PickApp.widget.AutoScollListView;
import com.frxs.PickApp.widget.ClearEditText;
import com.frxs.PickApp.widget.CountEditText;
import com.frxs.PickApp.widget.EmptyView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;

/**
 * Created by Endoon on 2016/6/8.
 */
public abstract class PickingActivity extends FrxsActivity {

    private ListView lvPicking;

    private String orderId;

    private String tagFrom; //标识从哪个页面进来的

    private EmptyView emptyView;

    private QuickAdapter<Goods> mGoodsQuickAdapter;

    private TextView tvOrderId;

    private LinearLayout llOrderRemark;

    private ExpandableTextView tvOrderRemark;

    private TextView tvStationNum;

    private View tvOrderTopFlag; //订单置顶标志

    private TextView tvGoodsTotal;

    private TextView tvLineNumber;

    private TextView tvTotalFund;

    private Goods currentGoods;

    private Dialog mUnitDialog;

    private ClearEditText cetAccount;

    private Dialog checkOutDialog;

    private TextView ivPrint;

    private TextView tvTitle;

    private double currentCount;

    private Order mOrder;// 当前订单

    private List<Goods> goodsList = new ArrayList<Goods>(); //缓存当前订单的商品列表

    private TextView tvStoreName;//门店名称

    private TextView btnSubmitPickedGoods;

    private BluetoothService bluetoothService;

    private PrintDataService printDataService;

    private int sourceType;

    private Dialog failedDialog;

    @Override
    protected void initViews() {
        lvPicking = (ListView) findViewById(R.id.lv_picking_list);
        emptyView = (EmptyView) findViewById(R.id.emptyview);
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvStoreName = (TextView) findViewById(R.id.tv_store_name);
        tvStationNum = (TextView) findViewById(R.id.tv_station_num);
        tvOrderTopFlag = findViewById(R.id.to_top_flag_tv);
        llOrderRemark = (LinearLayout) findViewById(R.id.ll_order_remark);
        tvOrderRemark = (ExpandableTextView) findViewById(R.id.expand_text_view);
        tvGoodsTotal = (TextView) findViewById(R.id.tv_goods_total);
        tvLineNumber = (TextView) findViewById(R.id.tv_line_number);
        tvTotalFund = (TextView) findViewById(R.id.total_fund_tv);
        ivPrint = (TextView) findViewById(R.id.iv_title_right);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        if (FrxsApplication.versionSelectorClass == PickingClassicalActivity.class) {
            btnSubmitPickedGoods = (TextView) findViewById(R.id.btn_checked);
        }
    }

    @Override
    protected void initData() {
        tvTitle.setText(FrxsApplication.getInstance().getUserInfo().getShelfAreaName());
        Intent intent = getIntent();
        if (intent != null) {
            tagFrom = intent.getStringExtra("from");
            orderId = intent.getStringExtra("ORDERID");
            sourceType = intent.getIntExtra("SOURCETYPE", 0);
        } else {
            orderId = getOrderID();
        }

        if (SystemUtils.isNetworkAvailable(PickingActivity.this)) {
            showProgressDialog();
            if (TextUtils.isEmpty(orderId)) {
                reqPrePickingOrderID();
            } else {
                reqOrderGoodsList();
            }
        } else {
            ToastUtils.show(PickingActivity.this, "网络异常，请检查网络是否连接");
        }

        mGoodsQuickAdapter = initItem(lvPicking);
        lvPicking.setAdapter(mGoodsQuickAdapter);
        if (FrxsApplication.versionSelectorClass == PickingSimpleActivity.class) {
            setOnSimpleItemclickListener();// 是精简版时给listview设置条目点击监听
        }
        addFooterView(lvPicking);

        //初始化蓝牙打印服务
        initBtPrinter();
    }

    /**
     * 根据当前选定字体设置商品字体大小
     * @param tvProductName
     * @param tvGiftsFlag
     * @param tvGoodCount
     */
    protected void initFontSize(TextView tvProductName, TextView tvGiftsFlag, TextView tvGoodCount) {
        SharedPreferencesHelper spHelper = SharedPreferencesHelper.getInstance(PickingActivity.this, GlobelDefines.PREFS_NAME);
        int fontsize = spHelper.getInt(GlobelDefines.KEY_FONT_SIZE, GlobelDefines.FontSizeConstants.FONT_MEDIUM);
        switch (fontsize) {
            case GlobelDefines.FontSizeConstants.FONT_BIG:
                tvProductName.setTextSize(getResources().getDimension(R.dimen.twenty_six));
                tvGiftsFlag.setTextSize(getResources().getDimension(R.dimen.twenty_six));
                tvGoodCount.setTextSize(getResources().getDimension(R.dimen.twenty_six));
                break;
            case GlobelDefines.FontSizeConstants.FONT_MEDIUM:
                tvProductName.setTextSize(getResources().getDimension(R.dimen.twenty_two));
                tvGiftsFlag.setTextSize(getResources().getDimension(R.dimen.twenty_two));
                tvGoodCount.setTextSize(getResources().getDimension(R.dimen.twenty_two));
                break;
            case GlobelDefines.FontSizeConstants.FONT_SMALL:
                break;

            default:
                break;
        }
    }

    /**
     * 根据库存数量显示是否缺货
     *
     * @param item
     */
    protected void getGoodsStock(final Goods item, final Dialog changeCountDlg) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("WID", FrxsApplication.getInstance().getUserInfo().getWID());
        params.put("OrderID", item.getOrderID());
        params.put("ProductId", item.getProductID());
        getService().getOrderStockQty(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {

            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (null != result) {
                    if (result.getFlag().equals("0")) {
                        if (null != result.getData()) {
                            if (!TextUtils.isEmpty(DisplayUtil.subZeroAndDot(result.getData()))) {
                                String stock = DisplayUtil.subZeroAndDot(result.getData());
                                showStockPop(stock, changeCountDlg);
                            }
                        }
                    } else {
                        ToastUtils.show(PickingActivity.this, " " + result.getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(PickingActivity.this, "获取商品库存失败 \n" + t.getMessage());
            }
        });
    }

    /**
     * 查询库存
     *
     * @param goodStock
     */
    protected void showStockPop(String goodStock, Dialog changeCountDlg) {
        View popView = LayoutInflater.from(this).inflate(R.layout.view_order_stock, null);
        TextView tvStock = (TextView) popView.findViewById(R.id.tv_stock);
        tvStock.setText(Html.fromHtml("库存：<font color=red>" + goodStock + "</font>" + currentGoods.getUnit()));

        PopupWindow window = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 使其聚集
        window.setFocusable(true);
        // 设置允许在外点击消失
        window.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置基于某控件下方弹出
        window.showAsDropDown(changeCountDlg.findViewById(R.id.tv_look_stock), 0, 5);
        // 点击弹出后设置阴影
        setBackgroundAlpha(true);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            setBackgroundAlpha(false);
                                        }
                                    }
        );
    }

    /**
     * 点击数量弹出对话框
     *
     * @param item 单个商品数据
     */
    protected void showChangeCountDialog(final Goods item) {
        currentGoods = item;
        final Dialog changeCountDlg = new Dialog(PickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        changeCountDlg.setContentView(R.layout.dialog_change_count);// 自定义对话框
        changeCountDlg.setCanceledOnTouchOutside(true);// 对话框外点击有效

        Button btnCommit = (Button) changeCountDlg.findViewById(R.id.btn_commit);
        final Button btnCancel = (Button) changeCountDlg.findViewById(R.id.btn_cancel);
        final TextView tvPreQty = (TextView) changeCountDlg.findViewById(R.id.tv_pre_qty);
        final TextView tvPackingQty = (TextView) changeCountDlg.findViewById(R.id.tv_packing_qty);
        TextView tvGoodsSku = (TextView) changeCountDlg.findViewById(R.id.tv_goods_sku);
        TextView tvLookStock = (TextView) changeCountDlg.findViewById(R.id.tv_look_stock);
        final CountEditText cetCount = (CountEditText) changeCountDlg.findViewById(R.id.cet_good_countedit);
        TextView pickedNumTv = (TextView) changeCountDlg.findViewById(R.id.picked_goods_num_tv);
        String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
        pickedNumTv.setText(String.format(getString(R.string.picked_goods_num), pickUnit));
        TextView btnChangeUnit = (TextView) changeCountDlg.findViewById(R.id.tv_change_unit);
        cetCount.setMaxCount(99999999);
        changeCountDlg.show();
        cetCount.KeyBoard222("open");
        // 预定数的包装数在第一次时设置
        if (currentGoods.getPickPackingQty() == 0) {
            currentGoods.setPickPackingQty(currentGoods.getSalePackingQty());
        }

        cetCount.setCount(currentGoods.getPickQty());

        // 预发数
        tvPreQty.setText("数量：" + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(currentGoods.getPreQty())) + currentGoods.getSaleUnit());
        //包装数
        tvPackingQty.setText("(包装数：" + DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(currentGoods.getSalePackingQty())) + ")");
        //编码
        tvGoodsSku.setText("编码：" + currentGoods.getSKU());

        tvLookStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGoodsStock(item, changeCountDlg);
            }
        });

        btnChangeUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }

                if (currentGoods.getSaleUnit().equals(currentGoods.getUnit())) {
                    ToastHelper.toastLong(PickingActivity.this, "只有一个单位，无法切换", Gravity.CENTER);
                } else if (null == mUnitDialog || !mUnitDialog.isShowing()) {
                    showCheckMasterDialog(changeCountDlg);
                }
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int multiple = FrxsApplication.getInstance().getUserInfo().getMultiple();
                if (!isExceedGoodsMultiple(cetCount.getCount(), multiple)) {
                    ImeUtils.dismissIme(v);
                    dealGoodsPickedData(currentGoods, cetCount.getCount());
                    changeCountDlg.dismiss();
                } else {
                    ToastHelper.toastShort(PickingActivity.this, String.format(getString(R.string.picked_multiple), String.valueOf(multiple)), Gravity.CENTER);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGoods.setCurrentUnit("");
                ImeUtils.dismissIme(v);
                changeCountDlg.dismiss();
            }
        });
    }

    /**
     * 切换单位组长验证的对话框
     */
    protected void showCheckMasterDialog(final Dialog changeCountDlg) {
        mUnitDialog = new Dialog(PickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        mUnitDialog.setContentView(R.layout.dialog_checkout_permissions);// 自定义对话框
        mUnitDialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        final ClearEditText cetAccount = (ClearEditText) mUnitDialog.findViewById(R.id.login_account_edit);
        final ClearEditText cetPassWord = (ClearEditText) mUnitDialog.findViewById(R.id.login_password_edit);
        // 有组长帐号将光标下移
        String masterAccout = FrxsApplication.getInstance().getUserInfo().getMasterUserAccount();
        cetAccount.setText(masterAccout == null ? "" : masterAccout);
        if (!TextUtils.isEmpty(masterAccout)) {
            cetPassWord.requestFocus();
        } else {
            cetAccount.requestFocus();
        }
        mUnitDialog.show();
        mUnitDialog.findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = cetAccount.getText().toString().trim();
                String passWord = cetPassWord.getText().toString().trim();
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(passWord)) {
                    isShelfAreaMaster(account, passWord, FrxsApplication.getInstance().getUserInfo().getShelfAreaCode(), FrxsApplication.getInstance().getUserInfo().getWarehouseWID(), new OnResponseListener() {
                        @Override
                        public void onResponse(Intent data) {
                            if (data != null) {
                                showUnitPop(changeCountDlg);
                                collapseSoftInputMethod(cetAccount);
                                mUnitDialog.dismiss();
                            } else {
                                ToastHelper.toastLong(PickingActivity.this, "您不是该货区组长，没有操作权限", Gravity.CENTER);
                                mUnitDialog.dismiss();
                                collapseSoftInputMethod(cetAccount);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.show(PickingActivity.this, "验证失败：" + t.getMessage());
                        }
                    });
                } else {
                    ToastUtils.show(PickingActivity.this, "账号或密码为空");
                }
            }
        });

        mUnitDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnitDialog.dismiss();
            }
        });
    }


    /**
     * 弹出切换单位的POP
     */
    protected void showUnitPop(final Dialog changeCountDlg) {
        final View popView = LayoutInflater.from(this).inflate(R.layout.view_select_unit, null);
        ListView lvUnit = (ListView) popView.findViewById(R.id.lv_unit);
        List<String> unitList = new ArrayList<String>();
        unitList.add(currentGoods.getSaleUnit());
        unitList.add(currentGoods.getUnit());

        final QuickAdapter<String> mUnitAdaptet = new QuickAdapter<String>(PickingActivity.this, R.layout.item_select_unit, unitList) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                helper.setText(R.id.tv_unit_name, item);
                if (currentGoods != null) {
                    if (currentGoods.getCurrentUnit().equals(item)) {
                        helper.setTextColor(R.id.tv_unit_name, getResources().getColor(R.color.frxs_red));
                    } else {
                        helper.setTextColor(R.id.tv_unit_name, getResources().getColor(R.color.black));
                    }
                } else {
                    helper.setTextColor(R.id.tv_unit_name, getResources().getColor(R.color.black));
                }
            }
        };
        lvUnit.setAdapter(mUnitAdaptet);

        final PopupWindow window = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 使其聚集
        window.setFocusable(true);

        // 设置允许在外点击消失
        window.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));

        // 设置基于某控件下方弹出
        window.showAsDropDown(changeCountDlg.findViewById(R.id.tv_change_unit), 0, 5);

        // 点击弹出后设置阴影
        setBackgroundAlpha(true);

        lvUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          @Override
                                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                              // 已选的单位
                                              String selectedUnit = mUnitAdaptet.getItem(position);

                                              // 如果选择的单位等于已经显示的单位，则不做任何操作。
                                              if (currentGoods.getCurrentUnit().equals(selectedUnit)) {// 又是点击当前单位，不做切换
                                                  // 实发数单位
                                                  window.dismiss();
                                                  return;
                                              }

                                              TextView pickedNumTv = (TextView) changeCountDlg.findViewById(R.id.picked_goods_num_tv);
                                              CountEditText cetCount = (CountEditText) changeCountDlg.findViewById(R.id.cet_good_countedit);

                                              // 对话框的实际单位
                                              pickedNumTv.setText(String.format(getString(R.string.picked_goods_num), selectedUnit));
                                              if (selectedUnit.equals(currentGoods.getSaleUnit())) { //这个表示大单位
                                                  currentCount = cetCount.getCount() / currentGoods.getSalePackingQty();
                                              } else { //表示切换的小单位
                                                  currentCount = currentGoods.getSalePackingQty() * cetCount.getCount();
                                              }
                                              currentGoods.setCurrentUnit(selectedUnit);
                                              cetCount.setCount(currentCount);

                                              window.dismiss();
                                          }
                                      }
        );

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            setBackgroundAlpha(false);
                                        }
                                    }
        );
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param isShow
     */
    public void setBackgroundAlpha(boolean isShow) {
        if (isShow) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f; //0.0-1.0
            getWindow().setAttributes(lp);
        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1f;
            getWindow().setAttributes(lp);
        }
    }

    /**
     * 设置成已拣状态，并保存
     * @param item
     */
    protected void setPickedStatus(Goods item) {
        item.setIsPicked(1);// 设成已拣
        initPickingGoodsItemView(goodsList);
        DBHelper.getGoodsService().update(item);
    }

    // 要保证切换单位那里赋值了的话，这里不能重复赋值
    protected void dealGoodsPickedData(Goods item, double pickQty) {
        String pickUnit = TextUtils.isEmpty(item.getPickUnit()) ? item.getSaleUnit() : item.getPickUnit();
        if (!item.getCurrentUnit().equals(pickUnit)) {
            if (item.getCurrentUnit().equals(item.getUnit())) {//大单位切小单位
                item.setPickPackingQty(1.0);
            } else { //反之，小单位切大单位
                item.setPickPackingQty(item.getSalePackingQty());
            }

            item.setPickUnit(item.getCurrentUnit());// 修改的单位
        }

        item.setPickQty(pickQty);// 实际拣货数量
        setPickedStatus(item);
    }

    private void reqPrePickingOrderID() {
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("ParentWID", ui.getWID());
        params.put("ChildWID", ui.getWarehouseWID());
        params.put("UserId", ui.getEmpID());
        params.put("UserName", ui.getEmpName());

        getService().prePickingOrderID(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    orderId = result.getData();
                    if (!TextUtils.isEmpty(orderId)) {
                        //更新本地订单号
                        setOrderID(orderId);
                        reqOrderGoodsList();
                    } else {
                        ToastUtils.show(PickingActivity.this, "返回的订单ID为空");
                    }
                } else if (result.getFlag().equals("1")) {// 没有拣货订单
                    dismissProgressDialog();
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                    emptyView.setMode(EmptyView.MODE_NODATA);
                    boolean isClear = (!TextUtils.isEmpty(tagFrom) && tagFrom.equals(HomeActivity.TAG)) ? true : false;
                    ReturnsTheResultProcessing(result.getInfo() + "无法获取订单详情。", isClear);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setImageResource(R.mipmap.icon_visit_fail);
                emptyView.setMode(EmptyView.MODE_ERROR);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        reqPrePickingOrderID();
                    }
                });
            }
        });
    }

    /**
     * 获得当前待拣货订单商品列表
     * // 1.如果orderId不为空则表示从正在拣货订单列表页面过来，需要根据ID请求后台，查询此订单是否有效（已拣完/未拣完）。
     * //  如果已捡完后台会返回新的订单，前台只要直接获取新订单继续拣货即可。如果未捡完则返回当前请求的订单数据，并根据该订单继续拣货。
     * // 2.如果为空则表示从开始拣货页面进来，则需要读取本地数据库的订单商品列表，并返回订单ID，请求后台进行比对。（逻辑处理同上）
     * //  如果已捡完后台会返回新的订单，前台只要直接获取新订单继续拣货即可。如果未捡完则返回当前请求的订单数据，并根据该订单继续拣货。
     */
    private void reqOrderGoodsList() {
        if (TextUtils.isEmpty(orderId)) {
            dismissProgressDialog();
            ToastUtils.show(PickingActivity.this, "请求参数异常，订单ID不能为空");
            return;
        }
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("ParentWID", ui.getWID());
        params.put("ChildWID", ui.getWarehouseWID());
        params.put("UserId", ui.getEmpID());
        params.put("UserName", ui.getEmpName());
        params.put("sourceType", sourceType);
        params.put("OrderId", orderId);
        getService().PostStartPickingOrderInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<PickData>>() {
            @Override
            public void onResponse(ApiResponse<PickData> result, int code, String msg) {
                dismissProgressDialog();
                // 服务器返回IsPicked:0表示已捡完，1表示未捡完
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        PickData data = result.getData();
                        if (data != null) {
                            if (data.getIsPicked() == 1) { //表示未捡完
                                Order order = data.getWaitPickData();
                                if (order != null) {
                                    mOrder = order;
                                    initViewWithData(order);
                                } else {
                                    emptyView.setVisibility(View.VISIBLE);
                                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                                    emptyView.setMode(EmptyView.MODE_NODATA);
                                }
                            } else if (0 == data.getIsPicked()) {  //表示已经捡完
                                if (!TextUtils.isEmpty(tagFrom) && tagFrom.equals(HomeActivity.TAG)) {
                                    clearLocalOrderData();
                                    emptyView.setVisibility(View.VISIBLE);
                                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                                    emptyView.setMode(EmptyView.MODE_ERROR_DATA);
                                    emptyView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showProgressDialog();
                                            reqPrePickingOrderID();
                                        }
                                    });
                                } else {
                                    emptyView.setVisibility(View.VISIBLE);
                                    emptyView.setImageResource(R.mipmap.icon_visit_fail);
                                    emptyView.setMode(EmptyView.MODE_NODATA);
                                }
                            }
                        }
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setImageResource(R.mipmap.icon_visit_fail);
                        emptyView.setMode(EmptyView.MODE_NODATA);
                        boolean isClear = (!TextUtils.isEmpty(tagFrom) && tagFrom.equals(HomeActivity.TAG)) ? true : false;
                        ReturnsTheResultProcessing(result.getInfo() + "无法获取订单详情。", isClear);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PickData>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setImageResource(R.mipmap.icon_visit_fail);
                emptyView.setMode(EmptyView.MODE_ERROR);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgressDialog();
                        reqOrderGoodsList();
                    }
                });
            }
        });
    }

    private void syncProductList() {
        List<Goods> localProductList = DBHelper.getGoodsService().queryAll();
        HashMap<String, Goods> localProductsHashMap = new HashMap<String, Goods>();
        if (null != localProductList) {
            for (Goods localProduct: localProductList) {
                String giftTag = TextUtils.isEmpty(localProduct.getIsGiftStr()) ? "" : localProduct.getIsGiftStr();
                String key = localProduct.getProductID() +  giftTag;
                localProductsHashMap.put(key, localProduct);
            }
        }

        if (localProductsHashMap.size() > 0) {
            for (Goods item : goodsList) {
                String giftTag = TextUtils.isEmpty(item.getIsGiftStr()) ? "" : item.getIsGiftStr();
                Goods localProduct = localProductsHashMap.get(item.getProductID() + giftTag);
                if (null != localProduct) {
                    item.setIsPicked(localProduct.getIsPicked());
                    item.setPickQty(localProduct.getPickQty());
                    item.setPickPackingQty(localProduct.getPickPackingQty());
                    item.setPickUnit(localProduct.getPickUnit());
                }
            }
        }

        if (goodsList.size() > 0) {
            DBHelper.getGoodsService().deleteAll();
            DBHelper.getGoodsService().saveOrUpdate(goodsList);
        }
    }

    /**
     * 服务器请求订单成功后，在这里进行使用新订单还是旧订单的业务判断
     *
     * @param order
     */
    private void initViewWithData(Order order) {
        if (order.getSendNumber() == 1) {
            tvOrderTopFlag.setVisibility(View.VISIBLE);
        } else {
            tvOrderTopFlag.setVisibility(View.GONE);
        }
        //电话和订单ID
        tvOrderId.setText("电话：" + (TextUtils.isEmpty(order.getRevTelephone()) ? "" : order.getRevTelephone())
                + "     订单号：" + (TextUtils.isEmpty(order.getOrderId()) ? "" : order.getOrderId()));
        //门店名称
        if (!TextUtils.isEmpty(order.getShopName())) {
            String orderName = order.getShopName().replace("(", "").replace("（", "").replace(")", " ").replace("）", " ");
            tvStoreName.setText(orderName);
        }

        if (!TextUtils.isEmpty(order.getRemark())) {
            llOrderRemark.setVisibility(View.VISIBLE);
            tvOrderRemark.setText("整单备注：" + order.getRemark());
        } else {
            llOrderRemark.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(order.getStationNumber())) {
            tvStationNum.setText(order.getStationNumber());
        }

        if (order.getOrderId().equals(getOrderID())) {
            goodsList = order.getProductData();
            syncProductList();
            initPickingGoodsItemView(goodsList);
            // 在这里做未拣货商品的筛选
            boolean isAllPicked = true;
            for (Goods item : goodsList) {
                if (item.getIsPicked() != 1) {
                    isAllPicked = false;
                    break;
                }
            }

            if (isAllPicked) {
                showSubmitPickedDialog(false);
                // 商品拣完跳转到最后一个商品并初始化底部商品信息栏
                lvPicking.setItemChecked(goodsList.size() - 1, true);
                lvPicking.setSelection(goodsList.size() - 1);
                initGoodInfoBottom(goodsList.get(goodsList.size() - 1), goodsList.size());
            }
        } else {
            reloadData(order);
        }

        if (TextUtils.isEmpty(tagFrom) || !tagFrom.equals(HomeActivity.TAG)) {
            setOrderID(orderId);
        }
    }

    /**
     * 拣货完成的对话框
     */
    private void showSubmitPickedDialog(boolean isAllPicked) {
        final Dialog dialog = new Dialog(PickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_continue_pick);// 自定义对话框
        dialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        Button btnCommit = (Button) dialog.findViewById(R.id.btn_commit);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_dialog_content);
        dialog.show();
        if (!isAllPicked) {
            tvContent.setText("所有商品已拣货，是否提交订单？");
        } else {
            SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
            boolean printForced = helper.getBoolean(GlobelDefines.KEY_PRINT_SETTING, true);
            if (printForced) {
                tvContent.setText("请确认是否拣货完成，并已经打印好小票？");
            } else {
                tvContent.setText("请确认是否拣货完成？");
            }
        }
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }

                if (FrxsApplication.getInstance().isPrinted(orderId)) {
                    reqGetPickDetailsCount(orderId);
                } else {
                    final AlertDialog.Builder printDialog = new AlertDialog.Builder(PickingActivity.this);
                    printDialog.setMessage("请先打印小票!");
                    printDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //设置取消
                            doPrintAction();
                            dialog.dismiss();
                        }
                    });
                    printDialog.create().show();
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 该方法表示用新订单数据
     * 1.如果要用新订单数据，不管数据库有没有数据都要做清空操作，不免无效数据。
     * 2.用新订单数据，保存到本地数据库，再进行正常拣货处理。
     *
     * @param order
     */
    private void reloadData(Order order) {
        DBHelper.getGoodsService().deleteAll();

        goodsList = order.getProductData();
        if (goodsList != null && goodsList.size() > 0) {
            // 在这里注释掉的原因：PickQty实际拣货数量，从后台来的数据此参数可能为空（还有其他参数也有可能）。
            // 如果为空再保存再本地数据库，在getProductData()这里loadAllGoods()所有有的商品数据时，会把所有的商品都获取出来（包括这里保存的和处理过的数据dealData()保存的）。
            // 因此，在这里getProductData()获取提交后台的数据data.setPickQty(gs.getPickQty());有可能会为null，所以会报错。

            //对返回的数据进行验证（验证其订单号和订单商品所属的订单号是否一致），如果不通过直接给出提示并退出改页面
            String orderID = order.getOrderId();
            if (!TextUtils.isEmpty(orderID)) {
                for (Goods item : goodsList) {
                    if (TextUtils.isEmpty(item.getOrderID()) || !orderID.equals(item.getOrderID())) {
                        ToastUtils.show(PickingActivity.this, "订单商品和订单不符");
                        finish();
                    }
                }
            } else {
                ToastUtils.show(PickingActivity.this, "数据异常，订单号为空");
                finish();
            }

            DBHelper.getGoodsService().save(goodsList);
            initPickingGoodsItemView(goodsList);
        }
    }

    /**
     * 初始化赋值
     *
     * @param goodsList
     */
    private void initPickingGoodsItemView(List<Goods> goodsList) {
        if (goodsList != null && goodsList.size() > 0) {
            emptyView.setVisibility(View.GONE);
            double productTotal = 0.0;
            double totalMoney = 0.0;
            for (Goods item : goodsList) {
                productTotal += item.getPickQty();
                double packingQty = item.getPickPackingQty() == 0 ? item.getSalePackingQty() : item.getPickPackingQty();
                totalMoney += item.getUnitPrice() * item.getPickQty() * packingQty;
            }
            tvGoodsTotal.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(productTotal)));
            tvLineNumber.setText(String.format(getResources().getString(R.string.total_row), goodsList.size()));
            tvTotalFund.setText(String.format(getResources().getString(R.string.price_2_decimal), totalMoney));

            mGoodsQuickAdapter.replaceAll(goodsList);
            for (int i = 0; i < goodsList.size(); i++) {
                if (goodsList.get(i).getIsPicked() != 1) {
                    if (i == 0) {
                        // 跳转到未拣货的Item并初始化底部商品栏
                        lvPicking.setItemChecked(i, true);
                        lvPicking.setSelection(i);
                        initGoodInfoBottom(goodsList.get(i), i + 1);
                        break;
                    } else {
                        lvPicking.setItemChecked(i, true);
                        lvPicking.setSelection(i == 1 ? (i - 1) : (i - 2));
                        initGoodInfoBottom(goodsList.get(i), i + 1);
                        break;
                    }
                } else if (i == goodsList.size() - 1) {
                    lvPicking.setItemChecked(i, true);
                    lvPicking.setSelection(i == 1 ? (i - 1) : (i - 2));
                    initGoodInfoBottom(goodsList.get(i), i + 1);
                }
            }
        }
    }

    /**
     * 提交拣货完成结果的处理
     *
     * @param resultInfo
     */
    private void ReturnsTheResultProcessing(String resultInfo, final boolean isFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PickingActivity.this);
        builder.setMessage("" + resultInfo);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isFinish) {
                    clearLocalOrderData();
                }

                dialog.dismiss();
                Intent intent = new Intent(PickingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (!isFinishing()) {
            builder.show();
        } else {
            Intent intent = new Intent(PickingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /*
    清空本地DB和SP里边的订单信息
     */
    private void clearLocalOrderData() {
        orderId = null;
        //清空本地SP订单号
        setOrderID(null);
        DBHelper.getGoodsService().deleteAll();
    }

    @Override
    protected void initEvent() {
        ivPrint.setOnClickListener(this);
        if (btnSubmitPickedGoods != null) {
            btnSubmitPickedGoods.setOnClickListener(this);
        }

        /**
         * 监听listview布局高度的变化，发生变化后再给当前未拣的商品定位
         */
        if (this instanceof PickingSimpleActivity ) {
            ((AutoScollListView) lvPicking).setDataChangedListener(new AutoScollListView.DataChangedListener() {
                @Override
                public void onDataChanged() {
                    int checkedPosition = lvPicking.getCheckedItemPosition();
                    if (checkedPosition == 0){
                        lvPicking.setSelection(checkedPosition);
                    } else if (checkedPosition == goodsList.size() -1){
                        lvPicking.setSelection(checkedPosition);
                    } else {
                        lvPicking.setSelection(checkedPosition == 1 ? checkedPosition -1 : checkedPosition -2);
                    }
                }
            });
        }
    }

    /**
     * 反拣货验证输入对话框
     *
     * @param item
     */
    protected void showCheckOutPermissionsDialog(final Goods item, final TextView btnPickState, final int backgroundRes) {
        if (checkOutDialog == null) {
            checkOutDialog = new Dialog(PickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        }
        checkOutDialog.setContentView(R.layout.dialog_checkout_permissions);// 自定义对话框
        checkOutDialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        cetAccount = (ClearEditText) checkOutDialog.findViewById(R.id.login_account_edit);
        final ClearEditText cetPassWord = (ClearEditText) checkOutDialog.findViewById(R.id.login_password_edit);
        // 有组长帐号将光标下移
        String masterAccout = FrxsApplication.getInstance().getUserInfo().getMasterUserAccount();
        cetAccount.setText(masterAccout == null ? "" : masterAccout);
        if (!TextUtils.isEmpty(masterAccout)) {
            cetPassWord.requestFocus();
        } else {
            cetAccount.requestFocus();
        }
        Button btnCommit = (Button) checkOutDialog.findViewById(R.id.btn_commit);
        Button btnCancel = (Button) checkOutDialog.findViewById(R.id.btn_cancel);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = cetAccount.getText().toString().trim();
                final String passWord = cetPassWord.getText().toString().trim();
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(passWord)) {
                    isShelfAreaMaster(account, passWord, FrxsApplication.getInstance().getUserInfo().getShelfAreaCode(), FrxsApplication.getInstance().getUserInfo().getWarehouseWID(), new OnResponseListener() {
                        @Override
                        public void onResponse(Intent data) {
                            if (data != null) {
                                // 精简版修改为未拣的文字 经典版无须修改会刷新列表中的状态
                                if (FrxsApplication.versionSelectorClass == PickingSimpleActivity.class) {
                                    btnPickState.setText("未拣");
                                }
                                // 设置为未拣状态
                                item.setIsPicked(0);
                                btnPickState.setBackgroundResource(backgroundRes);
                                mGoodsQuickAdapter.notifyDataSetChanged();
                                DBHelper.getGoodsService().update(item);
                                collapseSoftInputMethod(cetAccount);
                                checkOutDialog.dismiss();
                            } else {
                                ToastHelper.toastLong(PickingActivity.this, "验证失败，请重新验证", Gravity.CENTER);
                                checkOutDialog.dismiss();
                                collapseSoftInputMethod(cetAccount);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.show(PickingActivity.this, "验证失败：" + t.getMessage());
                        }
                    });
                } else {
                    ToastUtils.show(PickingActivity.this, "账号或密码为空");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImeUtils.dismissIme(v);
                checkOutDialog.dismiss();
            }
        });
        checkOutDialog.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.iv_title_right: {// 点击打印
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

    private void goBluetoothActivity() {
        Intent intent = new Intent(PickingActivity.this, BluetoothActivity.class);
        intent.putExtra("ORDER", mOrder);
        startActivity(intent);
    }

    /**
     * 完成拣货的处理
     */
    protected boolean finishPickProcess() {
        boolean isAllPicked = true;
        int position = 0;
        if (goodsList != null && goodsList.size() > 0) {
            for (int i = 0; i < goodsList.size(); i++) {
                if (goodsList.get(i).getIsPicked() != 1) {
                    isAllPicked = false;
                    position = i;
                    break;
                }
            }
        }

        // 判断是否全部捡完货
        if (isAllPicked) {// 全部捡完
            showSubmitPickedDialog(true);
            return true;
        } else {// 未捡完
            ToastHelper.toastShort(PickingActivity.this, "还有商品未拣货完成");
            if (position == 0) {
                // 跳转到未拣货的Item并初始化商品底部栏
                lvPicking.setItemChecked(position, true);
                lvPicking.setSelection(position);
                initGoodInfoBottom(goodsList.get(position), position + 1);
            } else {
                lvPicking.setItemChecked(position, true);
                lvPicking.setSelection(position == 1 ? (position - 1) : (position - 2));
                initGoodInfoBottom(goodsList.get(position), position + 1);
            }
            return false;
        }
    }

    private void reqGetPickDetailsCount(String orderId) {
        showProgressDialog();
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("OrderID", orderId);
        params.put("WID", ui.getWID());
        params.put("ShelfAreaID", ui.getShelfAreaID());

        getService().GetPickDetailsCount(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(ApiResponse<Integer> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    int orderProductCount = (result.getData() != null ? result.getData().intValue() : 0);
                    reqFinishPick(orderProductCount);
                } else {
                    dismissProgressDialog();
                    ToastUtils.show(PickingActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    /**
     * 完成拣货服务器操作
     */
    private void reqFinishPick(int count) {
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        final Product product = getProductData();
        if (count != product.getProductsData().size()) {
            dismissProgressDialog();
            ReturnsTheResultProcessing("提交商品数量跟服务端订单商品数量不一致", false);
            return;
        }

        SubmitDatas datas = new SubmitDatas();
        datas.setOrderId(product.getOrderId());
        datas.setWID(ui.getWID());
        datas.setUserId(ui.getEmpID());
        datas.setUserName(ui.getEmpName());
        datas.setProductData(product);
        getService().PostFinishPick(datas).enqueue(new SimpleCallback<ApiResponse<SubmitResult>>() {
            @Override
            public void onResponse(final ApiResponse<SubmitResult> result, int code, String msg) {
                dismissProgressDialog();
                // 拣货完成
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        // 0、提交成功
                        if (result.getData().getFlag() == 0) {
                            ToastUtils.show(PickingActivity.this, "商品全部拣货完成");
                            clearLocalOrderData();

                            Intent intent = new Intent(PickingActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    if (result.getData() != null) {
                        // 0、提交成功 1、找不到订单编号对应的数据,帐号状态不可用 2、订单已完成拣货 3、订单的当前获区已完成拣货（只针对APP）4、商品编号或ID异常 5、提交的商品数据为空 6、提交商品数据跟服务端不一致
                        switch (result.getData().getFlag()) {
                            case 1: {
                                ReturnsTheResultProcessing(result.getData().getInfo(), false);
                                break;
                            }
                            case 2: {
                                ReturnsTheResultProcessing(result.getData().getInfo(), true);
                                break;
                            }
                            case 3: {
                                ReturnsTheResultProcessing(result.getData().getInfo(), true);
                                break;
                            }
                            case 4:
                            case 5: {
                                ReturnsTheResultProcessing(result.getData().getInfo(), false);
                                break;
                            }
                            case 6: {
                                ReturnsTheResultProcessing(result.getData().getInfo(), false);
                                break;
                            }
                        }
                    } else {
                        if (result.getFlag().equals("1")){
                            ReturnsTheResultProcessing(result.getInfo() + "，无法完成拣货。", false);
                        } else {
                            ToastUtils.show(PickingActivity.this, "返回信息错误");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SubmitResult>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtils.show(PickingActivity.this, "请求失败 \n" + t.getMessage());
                dismissProgressDialog();
            }
        });
    }

    /**
     * 获取数据库数据提交服务器
     *
     * @return
     */
    private Product getProductData() {
        Product pro = new Product();
        // 订单ID
        pro.setOrderId(mOrder.getOrderId());
        List<ProductData> dataList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            ProductData data = new ProductData();
            Goods gs = goodsList.get(i);
            // 货区编号
            data.setShelfAreaID(gs.getShelfAreaID());
            // 销售单位（实际拣货单位）
            String pickedUnit = TextUtils.isEmpty(gs.getPickUnit()) ? gs.getSaleUnit() : gs.getPickUnit();
            data.setSaleUnit(pickedUnit);
            // 单位包装数
            data.setSalePackingQty(gs.getPickPackingQty() == 0 ? gs.getSalePackingQty() : gs.getPickPackingQty());
            // 实际拣货数量
            data.setPickQty(gs.getPickQty());
            // 商品编号
            data.setProductID(gs.getProductID());
            // 订单商品表主键编号
            data.setID(gs.getID());
            // 预定数量
            double preQty = gs.getPreQty();
            if (!pickedUnit.equals(gs.getSaleUnit())) {
                if (pickedUnit.equals(gs.getUnit())) {
                    preQty = gs.getSalePackingQty() * gs.getPreQty();
                } else {
                    preQty = gs.getPreQty() / gs.getSalePackingQty();
                }
            }
            data.setPreQty(preQty);
            dataList.add(data);
        }
        pro.setProductsData(dataList);
        return pro;
    }

    /**
     * 判断拣货数量是否超过订货数量的规定倍数
     *
     * @param curCount
     * @param multiple
     * @return
     */
    private boolean isExceedGoodsMultiple(double curCount, int multiple) {
        double packingQty;
        if (currentGoods.getCurrentUnit().equals(currentGoods.getSaleUnit())) { // 大单位
            packingQty = 1.0;
        } else { // 小单位]
            packingQty = currentGoods.getSalePackingQty();
        }

        double maxCount = MathUtils.roundUp(currentGoods.getPreQty() * packingQty * multiple, 2);
        if (curCount <= maxCount) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 收起软键盘并设置提示文字
     */
    public void collapseSoftInputMethod(EditText v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * listview设置条目点击监听
     */
    private void setOnSimpleItemclickListener() {
        lvPicking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < goodsList.size()) {
                    initGoodInfoBottom(goodsList.get(position), position + 1);
                }
            }
        });
    }

    /**
     * 添加脚布局
     * @param lvPicking
     */
    protected abstract void addFooterView(ListView lvPicking);

    /**
     * 初始化商品item
     * @param lvPicking
     * @return
     */
    protected abstract QuickAdapter<Goods> initItem(ListView lvPicking);

    /**
     * 初始化底部商品信息
     * @param item
     * @param position
     */
    protected void initGoodInfoBottom(Goods item, int position) {
    }

    private void showFailedDialog() {
        if (failedDialog == null) {
            failedDialog = new Dialog(PickingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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
                Intent intent = new Intent(PickingActivity.this, SearchBluetoothActivity.class);
                startActivity(intent);
            }
        });
        failedDialog.show();
    }
}
