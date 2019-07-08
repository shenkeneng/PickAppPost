package com.frxs.PickApp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.ewu.core.base.BaseActivity;
import com.ewu.core.utils.EasyPermissionsEx;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.comms.GlobelDefines;
import com.frxs.PickApp.interfaces.OnResponseListener;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.ApiService;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import retrofit2.Call;

/**
 * Created by ewu on 2016/3/24.
 */
public abstract class FrxsActivity extends BaseActivity {

    /** 广播action */
    public static final String SYSTEM_EXIT = "com.frxs.PickApp.system_exit";

    private static final int MY_PERMISSIONS_REQUEST_WES = 4;// 请求文件存储权限的标识码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initFontSize();
        super.onCreate(savedInstanceState);

        if (!(this instanceof SplashActivity) && !(this instanceof BaseDialogActivity)) {
            // 判断当前用户是否允许此权限
            if (EasyPermissionsEx.hasPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE})) {
                // 允许 - 执行更新方法
                if (FrxsApplication.getInstance().isNeedCheckUpgrade()) {
                    FrxsApplication.getInstance().prepare4Update(this, false);
                }
            } else {
                // 不允许 - 弹窗提示用户是否允许放开权限
                EasyPermissionsEx.executePermissionsRequest(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_WES);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.frxs_red);
        }
//        if (!(this instanceof SplashActivity) && !(this instanceof BaseDialogActivity)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                setTranslucentStatus(true);
//            }
//
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.frxs_black_light);
//        }

        if (!this.isTaskRoot())
        {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN))
            {
                finish();
                return;
            }
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initFontSize()
    {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        int fontsize = helper.getInt(GlobelDefines.KEY_FONT_SIZE, GlobelDefines.FontSizeConstants.FONT_MEDIUM);
        switch (fontsize) {
            case GlobelDefines.FontSizeConstants.FONT_BIG:
                this.setTheme(R.style.theme_big);
                break;
            case GlobelDefines.FontSizeConstants.FONT_MEDIUM:
                this.setTheme(R.style.theme_medium);
                break;

            case GlobelDefines.FontSizeConstants.FONT_SMALL:
                this.setTheme(R.style.theme_small);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public ApiService getService()
    {
        FrxsApplication.getInstance().prepare4Update(this,false);

        return FrxsApplication.getRestClient(Config.TYPE_BASE).getApiService();
    }

    protected String getOrderID() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        return helper.getString(Config.KEY_LAST_ORDER, "");
    }


    protected void setOrderID(String orderId) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        helper.putValue(Config.KEY_LAST_ORDER, orderId == null ? "" : orderId);
    }

    /**
     * 判断当前用户是否是该货区的组长
     * @return
     */
    public void isShelfAreaMaster(String account, String passWord, String ShelfAreaCode, String WarehouseWID, final OnResponseListener onResponseListener){
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("UserAccount", account);
        params.put("UserPwd", passWord);
        params.put("ShelfAreaCode", ShelfAreaCode);
        params.put("WID", WarehouseWID);
        getService().IsShelfAreaMaster(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")){
                    String checkFlag = result.getData();
                    if (!TextUtils.isEmpty(checkFlag)){
                        if (checkFlag.equals("true")){
                            onResponseListener.onResponse(new Intent());
                        }else{
                            onResponseListener.onFailure(new Throwable(result.getInfo()));
                        }
                    }else{
                        ToastUtils.show(FrxsActivity.this, "验证失败，请重新验证");
                    }
                }else{
                    ToastUtils.show(FrxsActivity.this, result.getInfo());
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                dismissProgressDialog();
                super.onFailure(call, t);
                ToastUtils.show(FrxsActivity.this, "验证失败：" + "\n" + t.getMessage());
            }
        });
    }
    protected abstract int getLayoutId();
    protected abstract void initViews();
    protected abstract void initData();
    protected abstract void initEvent();

    public void onBack(View view){
        finish();
    }

    public void showProgressDialog()
    {
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog()
    {
        if (!isFinishing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 信息提示框
     * @return
     */
    protected void promptDialog(String message) {
        final Dialog dialog = new Dialog(FrxsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_promt_info);// 自定义对话框
        dialog.setCanceledOnTouchOutside(true);// 对话框外点击有效
        TextView msgTv = (TextView) dialog.findViewById(R.id.message_tv);
        msgTv.setText(message);
        dialog.findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 请求用户是否放开权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 已获取权限 继续运行应用
                    if (FrxsApplication.getInstance().isNeedCheckUpgrade()) {
                        FrxsApplication.getInstance().prepare4Update(this, false);
                    }
                } else {
                    // 不允许放开权限后，提示用户可在去设置中跳转应用设置页面放开权限。
                    if (!EasyPermissionsEx.somePermissionPermanentlyDenied(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE})) {
                        EasyPermissionsEx.goSettings2PermissionsDialog(this, "需要文件存储权限来下载更新的内容,但是该权限被禁止,你可以到设置中更改");
                    }
                }
                break;
            }
        }
    }
}
