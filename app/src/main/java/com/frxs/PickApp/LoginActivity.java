package com.frxs.PickApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.InputUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.greendao.DBHelper;
import com.frxs.PickApp.model.UserInfo;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.frxs.PickApp.widget.ClearEditText;
import retrofit2.Call;

/**
 * Created by ewu on 2016/3/23.
 */
public class LoginActivity extends FrxsActivity {
    /* 控件 */
    private Button loginBtn;// 登录按钮

    private ClearEditText edtAccount;// 账号输入框

    private ClearEditText edtPassword;// 密码输入框

    private View envHiddenBtn;// 选择环境的暗门

    private TextView tvTitle;// 标题

    private TextView tvLeft;// 返回按钮

    private String strUserName;// 账号

    private String strPassWord;// 密码

    private long exitTime;// 退出时间

    private String[] environments;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        loginBtn = (Button) findViewById(R.id.login_commit_btn);
        edtAccount = (ClearEditText) findViewById(R.id.login_account_edit);// 用户名编辑框
        edtPassword = (ClearEditText) findViewById(R.id.login_password_edit);// 密码编辑框
        envHiddenBtn = findViewById(R.id.select_environment);// 环境选择按钮
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvLeft = (TextView) findViewById(R.id.tv_title_left);
    }

    @Override
    protected void initData() {
        tvTitle.setText("拣货APP登录");
        tvLeft.setVisibility(View.GONE);
        tvLeft.setEnabled(false);
        if (!TextUtils.isEmpty(FrxsApplication.getInstance().getUserAccount())) {
            edtAccount.setText(FrxsApplication.getInstance().getUserAccount());
        }
        // 设置光标永远在文字长度之后
        edtAccount.setSelection(edtAccount.getText().length());

        initEnvironment();

        FrxsApplication.getInstance().prepare4Update(this, false);
    }

    private void initEnvironment() {
        environments = getResources().getStringArray(R.array.run_environments);
        for (int i = 0; i < environments.length; i++) {
            environments[i] = String.format(environments[i], Config.getBaseUrl(Config.TYPE_BASE, i));
        }
    }

    @Override
    protected void initEvent() {
        loginBtn.setOnClickListener(this);

        envHiddenBtn.setOnClickListener(new View.OnClickListener() {
            int keyDownNum = 0;

            @Override
            public void onClick(View view) {
                keyDownNum++;
                if (keyDownNum == 10) {
                    ToastUtils.show(LoginActivity.this, "已进入环境选择模式");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);

                    final int spEnv = FrxsApplication.getInstance().getEnvironment();
                    String env = spEnv < environments.length ? environments[spEnv] : "";
                    dialog.setTitle(getString(R.string.str_dialog_title_head) + env + getString(R.string.str_dialog_title_foot));
                    dialog.setCancelable(false);
                    dialog.setItems(environments, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            if (spEnv == which) {
                                return;
                            }
                            if (which != 0) {
                                final AlertDialog verifyMasterDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_evironments, null);
                                final EditText pswEt = (EditText) contentView.findViewById(R.id.password_et);
                                contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (TextUtils.isEmpty(pswEt.getText().toString().trim())) {
                                            ToastUtils.show(LoginActivity.this, "密码不能为空！");
                                            return;
                                        }

                                        if (!pswEt.getText().toString().trim().equals(getString(R.string.str_psw))) {
                                            ToastUtils.show(LoginActivity.this, "密码错误！");
                                            return;
                                        }

                                        DBHelper.getGoodsService().deleteAll();
                                        chooseEnvironment(which);
                                        FrxsApplication.getInstance().setEnvironment(which);
                                        verifyMasterDialog.dismiss();
                                    }
                                });

                                contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        verifyMasterDialog.dismiss();
                                    }
                                });
                                verifyMasterDialog.setView(contentView);
                                verifyMasterDialog.show();
                            } else {
                                DBHelper.getGoodsService().deleteAll();
                                chooseEnvironment(which);
                                FrxsApplication.getInstance().setEnvironment(which);
                            }

                        }
                    });
                    dialog.setNegativeButton(getString(R.string.str_dialog_cancle),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                    keyDownNum = 0;
                }
            }
        });
    }

    private void requestLogin() {
        showProgressDialog();

        AjaxParams params = new AjaxParams();
        params.put("UserAccount", strUserName);
        params.put("UserPwd", strPassWord);
        params.put("UserType", "1");
        getService().PostLogin(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(ApiResponse<UserInfo> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(LoginActivity.this, "登录成功");
                    FrxsApplication application = FrxsApplication.getInstance();
                    application.setUserAccount(strUserName);

                    UserInfo userInfo = result.getData();
                    if (null != userInfo) {
                        String oldAreaID = application.getShelfAreaID();
                        String newAreaID = userInfo.getShelfAreaID();
                        if (!TextUtils.isEmpty(oldAreaID) && !TextUtils.isEmpty(newAreaID)) {
                            // 重新登录判断新登录的账号是否与前面登录的账号是一个货区，不是则删除
                            if (!oldAreaID.equals(newAreaID)) {
                                DBHelper.getGoodsService().deleteAll();
                                // 清空上次用户操作的订单
                                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(LoginActivity.this, Config.PREFS_NAME);
                                helper.putValue(Config.KEY_LAST_ORDER, "");
                            }
                        }
                        application.setShelfAreaID(userInfo.getShelfAreaID());
                        userInfo.setMultiple(userInfo.getMultiple() == 0 ? 2 : userInfo.getMultiple());
                        application.setUserInfo(userInfo);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                } else {
                    ToastUtils.show(LoginActivity.this, result.getInfo());
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.login_commit_btn: {
                if (TextUtils.isEmpty(edtAccount.getText().toString().trim())) {
                    ToastUtils.show(this, R.string.tips_null_account);// 账号不能为空
                    shakeView(edtAccount);
                } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    ToastUtils.show(LoginActivity.this, R.string.tips_null_password);// 密码不能为空
                    shakeView(edtPassword);
                } else {
                    strUserName = edtAccount.getText().toString().trim();
                    strPassWord = edtPassword.getText().toString().trim();
                    if (InputUtils.isNumericOrLetter(strPassWord)) {
                        if (SystemUtils.isNetworkAvailable(LoginActivity.this)) {
                            requestLogin();
                        } else {
                            ToastUtils.show(LoginActivity.this, "网络异常，请检查网络是否连接");
                        }
                    } else {
                        ToastUtils.show(LoginActivity.this, getString(R.string.tips_input_limit));// 密码只能由数字、字母组成
                        shakeView(edtPassword);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    protected void chooseEnvironment(int which) {
        switch (which) {
            case 0:// 线上环境
            case 1:// 测试环境
            case 2:// 开发环境
            default:
                Config.networkEnv = 0;
                break;
        }
    }


    /**
     * 窗口抖动
     */
    private void shakeView(EditText edit) {
        DisplayUtil.shakeView(this, edit);
        edit.requestFocus();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出程序
     */
    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            FrxsApplication.getInstance().exitApp(0);
        }
    }
}
