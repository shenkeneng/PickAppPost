package com.frxs.PickApp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.comms.GlobelDefines;

/**
 * 个人中心页面
 * Created by Endoon on 2016/3/26.
 */
public class MineActivity extends FrxsActivity {
    /* 控件 */
    private TextView tvUpdatePsw;

    private TextView tvMyVersion;

    private TextView tvExit;

    private TextView tvUserName;

    private TextView tvTitle;

    private int fontSize;// 字体选择

    private int versionSelector;// 版本选择

    private TextView tvBluetoothSetting;// 蓝牙设置

    private TextView printCheckedBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initViews() {
        tvUpdatePsw = (TextView) findViewById(R.id.tv_update_psw);
        tvMyVersion = (TextView) findViewById(R.id.tv_myversion);
        tvExit = (TextView) findViewById(R.id.tv_exit);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBluetoothSetting = (TextView) findViewById(R.id.tv_bluetooth_setting);
        printCheckedBtn = (TextView) findViewById(R.id.print_checked_rb);

        // 初始化版本选择模块
        initSelectorVersion();

        // 初始化字体大小
        initFontSize();

        //设置是否必须打印
        initPrintSetting();
    }

    @Override
    protected void initData() {
        tvTitle.setText("关于我");
        String userAccount = FrxsApplication.getInstance().getUserAccount();
        if (!TextUtils.isEmpty(userAccount)) {
            tvUserName.setText(userAccount+"");
        }
        tvMyVersion.setText("v"+SystemUtils.getAppVersion(MineActivity.this));
    }

    @Override
    protected void initEvent() {
        tvUpdatePsw.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        tvBluetoothSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.tv_update_psw:// 跳转修改密码页面
            {
                Intent intent = new Intent(MineActivity.this,UpdatePswActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.tv_exit:// 退出账号
            {
                loginOut();
                break;
            }

            case R.id.tv_bluetooth_setting:// 设置蓝牙
            {
                Intent intent = new Intent(MineActivity.this, SearchBluetoothActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void loginOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定退出拣货员？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) { //设置取消按钮
                FrxsApplication.getInstance().logout();
                HomeActivity homeActivity = FrxsApplication.getInstance().getHomeAcivity();
                if (null != homeActivity)
                {
                    homeActivity.finish();
                }

                Intent intent = new Intent(MineActivity.this, LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void showCancelForcePrintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认之后，不打印拣货小票可以完成拣货。是否确认不强制打印？");
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(MineActivity.this, GlobelDefines.PREFS_NAME);
                helper.putValue(GlobelDefines.KEY_PRINT_SETTING, false);
                printCheckedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.button_version_selector_no, 0);
            }
        });
        builder.create().show();
    }

    private void initPrintSetting() {
        final SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        Boolean printForced = helper.getBoolean(GlobelDefines.KEY_PRINT_SETTING, true);
        if (printForced) {
            printCheckedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.button_version_selector, 0);
        } else {
            printCheckedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.button_version_selector_no, 0);
        }
        printCheckedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = helper.getBoolean(GlobelDefines.KEY_PRINT_SETTING, true);
                if (isChecked) {
                    showCancelForcePrintDialog();
                } else {
                    printCheckedBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.button_version_selector, 0);
                    helper.putValue(GlobelDefines.KEY_PRINT_SETTING, !isChecked);
                }
            }
        });
    }

    /**
     * 初始化字体大小并设置选择监听
     */
    private void initFontSize() {
        // 初始化版本选择控件
        RadioGroup rgFontSize = (RadioGroup) findViewById(R.id.rg_font_size);
        final RadioButton rbFontBig = (RadioButton) findViewById(R.id.rb_font_big);
        final RadioButton rbFontMedium = (RadioButton) findViewById(R.id.rb_font_medium);

        // 初始化字体大小（标准字体）
        final SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        fontSize = helper.getInt(GlobelDefines.KEY_FONT_SIZE, GlobelDefines.FontSizeConstants.FONT_MEDIUM);
        if (fontSize == 2) {// 标准字体
            rbFontMedium.setChecked(true);
            rbFontBig.setChecked(false);
        }

        if (fontSize == 1) {// 放大字体
            rbFontMedium.setChecked(false);
            rbFontBig.setChecked(true);
        }

        // 监听修改字体大小的变化
        rgFontSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbFontMedium.getId()){
                    fontSize = 2;
                }

                if (checkedId == rbFontBig.getId()){
                    fontSize = 1;
                }

                helper.putValue(GlobelDefines.KEY_FONT_SIZE, fontSize);
            }
        });
    }

    /**
     * 初始化版本选择模块并设置选择监听
     */
    private void initSelectorVersion() {
        // 初始化版本选择控件
        RadioGroup rgVersionSelector = (RadioGroup) findViewById(R.id.rg_version_selector);
        final RadioButton tvVersionClassic = (RadioButton) findViewById(R.id.rb_version_classic);
        final RadioButton tvVersionSimple = (RadioButton) findViewById(R.id.rb_version_simple);

        // 初始化版本选择（精简版）
        final SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        versionSelector = helper.getInt(GlobelDefines.KEY_VERSION_SELECTOR, GlobelDefines.VersionSelector.VERSION_SIMPLE);
        if (versionSelector == 2) {// 精简版
            tvVersionClassic.setChecked(false);
            tvVersionSimple.setChecked(true);
        }

        if (versionSelector == 1) {// 经典版
            tvVersionClassic.setChecked(true);
            tvVersionSimple.setChecked(false);
        }

        // 监听版本选择的变化
        rgVersionSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == tvVersionClassic.getId()){
                    versionSelector = 1;
                    FrxsApplication.versionSelectorClass = PickingClassicalActivity.class;
                }

                if (checkedId == tvVersionSimple.getId()){
                    versionSelector = 2;
                    FrxsApplication.versionSelectorClass = PickingSimpleActivity.class;
                }
                helper.putValue(GlobelDefines.KEY_VERSION_SELECTOR, versionSelector);
            }
        });
    }
}
