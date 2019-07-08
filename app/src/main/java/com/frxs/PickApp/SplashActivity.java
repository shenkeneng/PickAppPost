package com.frxs.PickApp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;

import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.model.UserInfo;

/**
 * Created by ewu on 2016/2/22.
 */
public class SplashActivity extends FrxsActivity {

    @Override
    protected int getLayoutId() {
        return  R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void initData() {
        new CountDownTimer(3000, 1500)
        {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
                if (null != userInfo && !TextUtils.isEmpty(userInfo.getEmpID()))
                {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                SplashActivity.this.finish();
                overridePendingTransition(R.anim.just_fade_in, R.anim.just_fade_out);
            }
        }.start();
    }

    @Override
    protected void initEvent() {

    }
}
