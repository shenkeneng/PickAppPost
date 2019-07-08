package com.frxs.PickApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.InputUtils;
import com.ewu.core.utils.MD5;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.rest.model.AjaxParams;
import com.frxs.PickApp.rest.model.ApiResponse;
import com.frxs.PickApp.rest.service.SimpleCallback;
import com.frxs.PickApp.widget.ClearEditText;

import retrofit2.Call;

/**
 * 修改用户密码界面
 * Created by Endoon on 2016/3/31.
 */
public class UpdatePswActivity extends FrxsActivity{
    /* 控件 */
    private ClearEditText cetOldPsw;// 旧密码

    private ClearEditText cetNewPsw;// 新密码

    private ClearEditText cetConfirmPsw;// 确认新密码

    private TextView tvTitle;// 页面标题

    private Button btnCommitPsw;// 确认修改密码

    /* 对象和属性 */
    private String mOldPsw;

    private String mNewPsw;

    private String mConfirmPsw;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_update_password;
    }

    @Override
    protected void initViews() {
        cetOldPsw = (ClearEditText)findViewById(R.id.cet_old_psw);
        cetNewPsw = (ClearEditText)findViewById(R.id.cet_new_psw);
        cetConfirmPsw = (ClearEditText)findViewById(R.id.cet_confirm_psw);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnCommitPsw = (Button) findViewById(R.id.btn_commit_psw);
    }

    @Override
    protected void initData() {
        tvTitle.setText("修改密码");
    }

    @Override
    protected void initEvent() {
        btnCommitPsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.btn_commit_psw:
            {
                if (TextUtils.isEmpty(cetOldPsw.getText().toString().trim()))
                {
                    ToastUtils.show(this, "旧密码不能为空");// 旧密码不能为空
                    shakeView(cetOldPsw);
                } else if (TextUtils.isEmpty(cetNewPsw.getText().toString().trim()))
                {
                    ToastUtils.show(this, "新密码不能为空");// 新密码不能为空
                    shakeView(cetNewPsw);
                }else if (TextUtils.isEmpty(cetConfirmPsw.getText().toString().trim()))
                {
                    ToastUtils.show(this, "新密码不能为空");// 新密码不能为空
                    shakeView(cetConfirmPsw);
                }
                else {
                    mOldPsw = cetOldPsw.getText().toString().trim();
                    mNewPsw = cetNewPsw.getText().toString().trim();
                    mConfirmPsw = cetConfirmPsw.getText().toString().trim();
                    if (InputUtils.isNumericOrLetter(mNewPsw))
                    {
                        if (mConfirmPsw.equals(mNewPsw))
                        {
                            if (SystemUtils.isNetworkAvailable(UpdatePswActivity.this)) {
                                reqUpdatePwd();
                            } else {
                                ToastUtils.show(UpdatePswActivity.this, "网络异常，请检查网络是否连接");
                            }
                        } else
                        {
                            ToastUtils.show(this, getString(R.string.tips_input_limit));// 密码只能由数字、字母组成
                            shakeView(cetConfirmPsw);
                        }
                    }else if (mNewPsw.equals(mOldPsw))
                    {
                        ToastUtils.show(this, "新密码不能和旧密码相同");// 密码只能由数字、字母组成
                    }
                    else
                    {
                        ToastUtils.show(this, getString(R.string.tips_input_limit));// 密码只能由数字、字母组成
                        shakeView(cetNewPsw);
                    }
                }
                break;
            }
        }
    }

    /**
     * 修改密码网络请求
     */
    private void reqUpdatePwd(){
        showProgressDialog();

        FrxsApplication fa = FrxsApplication.getInstance();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("PickingUpdatePwd"));
        params.put("UserAccount",fa.getUserAccount());
        params.put("OldUserPwd",mOldPsw);
        params.put("NewUserPwd",mNewPsw);
        params.put("UserId",fa.getUserInfo().getEmpID());
        params.put("UserName",fa.getUserInfo().getEmpName());
        params.put("UserType","1");
        getService().PostUpdatePwd(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                // 修改成功
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(UpdatePswActivity.this,"密码修改成功");
                    finish();
                }else {
                    AlertDialog.Builder updateDialog = new AlertDialog.Builder(UpdatePswActivity.this);
                    updateDialog.setMessage(result.getInfo() + "，无法修改密码!");
                    updateDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { //设置取消
                            dialog.dismiss();
                            Intent intent = new Intent(UpdatePswActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    if (!isFinishing()) {
                        updateDialog.create().show();
                    } else {
                        Intent intent = new Intent(UpdatePswActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    /**
     * 窗口抖动
     */
    private void shakeView(EditText edit)
    {
        DisplayUtil.shakeView(this, edit);
        edit.requestFocus();
    }
}
