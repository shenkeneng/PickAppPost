package com.frxs.PickApp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.MD5;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.ewu.core.widget.BadgeView;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.interfaces.OnResponseListener;
import com.frxs.PickApp.model.OrderCount;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.frxs.PickApp.widget.ClearEditText;

import retrofit2.Call;

public class HomeActivity extends FrxsActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();
    /**
     * 控件实例
     */
    private TextView tvTitle;

    private TextView tvStartPick;

    private TextView tvTitleLeft;

    private TextView tvPickingOrderCount;

    private TextView tvPickWaitCount;//等待拣货单

    private TextView tvPickHistoryCount;//历史拣货单

    private TextView tvAllOrderCount;//订单汇总数量

    private TextView btnRefresh;

    /**
     * 数据属性
     */
    private Handler mHandler = new Handler();

    private String orderId = "";

    private BadgeView badgeView;

    private Dialog checkOutDialog;

    private ClearEditText cetAccount;

    private View allView;

    private View pickingView;

    private View bePickingView;

    private View pickedView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvStartPick = (TextView) findViewById(R.id.tv_start_pick);
        tvTitleLeft = (TextView) findViewById(R.id.tv_title_left);
        tvPickingOrderCount = (TextView) findViewById(R.id.picking_order_count_tv);
        btnRefresh = (TextView) findViewById(R.id.tv_title_right);
        tvPickWaitCount = (TextView) findViewById(R.id.be_picking_order_count_tv);
        tvPickHistoryCount = (TextView) findViewById(R.id.picked_order_count_tv);
        pickingView = findViewById(R.id.picking_layout);
        bePickingView = findViewById(R.id.be_picking_order_layout);
        pickedView = findViewById(R.id.picked_order_layout);
        allView = findViewById(R.id.all_order_layout);
        tvAllOrderCount = (TextView) findViewById(R.id.all_order_count_tv);

        // 判断用户是否是第一工位 第一工位才显示订单汇总入口
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo != null){
            if (userInfo.getFirstStation() == 1){
                allView.setVisibility(View.VISIBLE);
            }
        }

        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) tvStartPick.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.width = (int) (DisplayUtil.getScreenWidth(this) * 0.7);// 控件的宽强制设成30
        tvStartPick.setLayoutParams(linearParams);

        badgeView = new BadgeView(this, tvStartPick);
        badgeView.setTextSize(32);
        badgeView.setBadgeMargin(20);
        badgeView.setPadding(30, 15, 30, 15);
        badgeView.setBadgeCornerRadius(16);
        badgeView.setTextColor(getResources().getColor(R.color.frxs_red));
        badgeView.setBadgeBackgroundColor(Color.WHITE);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
    }

    @Override
    protected void initData() {
        tvTitle.setVisibility(View.GONE);
        FrxsApplication.getInstance().prepare4Update(this, false);
        FrxsApplication.getInstance().setHomeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderId = getOrderID();
        if (!TextUtils.isEmpty(orderId)) {
            tvStartPick.setText("正在拣货...");
            tvStartPick.setBackgroundResource(R.drawable.shape_blue_circle);
            badgeView.hide();
        } else {
            tvStartPick.setText("开始拣货");
            tvStartPick.setBackgroundResource(R.drawable.shape_red_circle);
            badgeView.show();
        }
        mHandler.post(pollRunnable);
    }

    private Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {

            if (SystemUtils.isNetworkAvailable(HomeActivity.this)) {
                reqWaitPickNum(false);
            } else {
                ToastUtils.show(HomeActivity.this, "网络异常，请检查网络是否连接");
            }
            mHandler.postDelayed(pollRunnable, 10000);
        }
    };

    private void showBadgeView(int pickingCount, int waitingCount, int fishNum, int sumNun) {
        //正在拣货数量业务处理
        if (pickingCount < 0) {
            pickingCount = 0;
        }
        tvPickingOrderCount.setText(String.valueOf(pickingCount));

        //等待拣货数量业务处理
        if (waitingCount > 0) {
            badgeView.setText(String.valueOf(waitingCount));
            if (!TextUtils.isEmpty(orderId)) {
                badgeView.hide();
            } else {
                badgeView.show();
            }
        } else {
            badgeView.hide();
            waitingCount = 0;
        }
        tvPickWaitCount.setText(String.valueOf(waitingCount));
        tvPickHistoryCount.setText(String.valueOf(fishNum));
        tvAllOrderCount.setText(String.valueOf(sumNun));
    }

    /**
     * 获取待拣的订单数量
     */
    private void reqWaitPickNum(final boolean isRefresh) {
        UserInfo ui = FrxsApplication.getInstance().getUserInfo();
        if (ui == null) {
            return;
        }

        if (isRefresh) {
            showProgressDialog();
        }
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitPickingNum"));
        params.put("ShelfAreaCode", ui.getShelfAreaCode());
        params.put("ShelfAreaID", ui.getShelfAreaID());
        params.put("WID", ui.getWID());
        getService().PostWaitPickingNum(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<OrderCount>>() {
            @Override
            public void onResponse(ApiResponse<OrderCount> result, int code, String msg) {
                if (isRefresh) {
                    dismissProgressDialog();
                }
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        OrderCount orderCount = result.getData();
                        showBadgeView(orderCount.getPickingNum(), orderCount.getWaitPickNum(), orderCount.getFishNum(), orderCount.getSumNum());
                    } else {
                        ToastUtils.show(HomeActivity.this, result.getInfo());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderCount>> call, Throwable t) {
                super.onFailure(call, t);

                if (isRefresh) {
                    dismissProgressDialog();
                }
            }
        });
    }

    @Override
    protected void initEvent() {
        tvStartPick.setOnClickListener(this);
        tvTitleLeft.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);

        pickingView.setOnClickListener(this);
        bePickingView.setOnClickListener(this);
        pickedView.setOnClickListener(this);
        allView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_start_pick: {
                if (CommonUtils.isFastDoubleClick()) {
                    break;
                }
                Intent intent = new Intent(this, FrxsApplication.versionSelectorClass);
                intent.putExtra("from", TAG);
                intent.putExtra("SOURCETYPE", 0);
                intent.putExtra("ORDERID", orderId);
                startActivity(intent);
                mHandler.removeCallbacks(pollRunnable);
                break;
            }

            case R.id.tv_title_left: {
                if (CommonUtils.isFastDoubleClick()) {
                    break;
                }
                Intent intent = new Intent(this, MineActivity.class);
                startActivity(intent);
                mHandler.removeCallbacks(pollRunnable);
                break;
            }
            case R.id.tv_title_right: {
                if (SystemUtils.isNetworkAvailable(HomeActivity.this)) {
                    reqWaitPickNum(true);
                } else {
                    ToastUtils.show(HomeActivity.this, "网络异常，请检查网络是否连接");
                }
                break;
            }
            //正在拣货订单
            case R.id.picking_layout: {
                if (CommonUtils.isFastDoubleClick()) {
                    break;
                }

                Intent intent = new Intent(this, OrderPickingActivity.class);
                startActivity(intent);
                mHandler.removeCallbacks(pollRunnable);
                break;
            }
            //等待拣货订单
            case R.id.be_picking_order_layout: {
                if (CommonUtils.isFastDoubleClick()) {
                    break;
                }
                //组长账号不需要验证，暂时不开通 2016/11/05
//                if (isMasterAccount()) {
//                    Intent intent = new Intent(HomeActivity.this, OrderWaitingActivity.class);
//                    startActivity(intent);
//                    mHandler.removeCallbacks(pollRunnable);
//                } else {
//                    showDialog();
//                }
                showDialog(OrderWaitingActivity.class);
                break;
            }
            //历史拣货订单
            case R.id.picked_order_layout: {
                Intent intent = new Intent(this, PickedActivity.class);
                startActivity(intent);
                mHandler.removeCallbacks(pollRunnable);
                break;
            }
            case R.id.all_order_layout: {
                Intent intent = new Intent(this, OrderAllActivity.class);
                startActivity(intent);
                mHandler.removeCallbacks(pollRunnable);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 组长权限验证对话框
     */
    private void showDialog(final Class<?> cls) {
        checkOutDialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
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
                    isShelfAreaMaster(account, passWord, FrxsApplication.getInstance().getUserInfo().getShelfAreaCode(),FrxsApplication.getInstance().getUserInfo().getWarehouseWID(), new OnResponseListener() {
                        @Override
                        public void onResponse(Intent data) {
                            if (data != null) {
                                Intent intent = new Intent(HomeActivity.this, cls);
                                startActivity(intent);
                                mHandler.removeCallbacks(pollRunnable);
                                checkOutDialog.dismiss();
                            }else{
                                ToastUtils.show(HomeActivity.this, "验证失败，请重新验证");
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            ToastUtils.show(HomeActivity.this, "验证失败：" + t.getMessage());
                        }
                    });
                } else {
                    ToastUtils.show(HomeActivity.this, "账号或密码为空");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOutDialog.dismiss();
            }
        });
        checkOutDialog.show();
    }

    /**
     * 界面不可见时删除轮询线程
     */
    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(pollRunnable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myShowDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 对话框
     */
    private void myShowDialog() {
        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_continue_pick);// 自定义对话框
        dialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        TextView tvContent = (TextView) dialog.findViewById(R.id.tv_dialog_content);
        Button btnCommit = (Button) dialog.findViewById(R.id.btn_commit);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        tvContent.setText("确定退出拣货员吗？");
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                FrxsApplication.getInstance().exitApp(0);
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
}
