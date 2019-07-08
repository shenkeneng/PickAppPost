package com.frxs.PickApp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ewu.core.utils.SharedPreferencesHelper;
import com.frxs.PickApp.comms.GlobelDefines;

/**
 * 修改字体大小
 * Created by Tiepier
 */
public class FontSizeActivity extends FrxsActivity {
    /* 控件 */
    private TextView tvTitle;// 页面标题

    private Button btnSave;// 保存

    private RadioGroup rgFontSize;

    private RadioButton rbFontNormal;//标准大小

    private RadioButton rbFontBig;//放大字体

    private int fontsize = 2;//字体大小标识


    @Override
    protected int getLayoutId() {
        return R.layout.activity_font_size;
    }

    @Override
    protected void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnSave = (Button) findViewById(R.id.btn_save);
        rgFontSize = (RadioGroup) findViewById(R.id.rg_font_size);
        rbFontNormal = (RadioButton) findViewById(R.id.rb_normal);
        rbFontBig = (RadioButton) findViewById(R.id.rb_big);
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        fontsize = helper.getInt(GlobelDefines.KEY_FONT_SIZE, GlobelDefines.FontSizeConstants.FONT_MEDIUM);
        if (fontsize == 2) {
            rbFontNormal.setChecked(true);
            rbFontBig.setChecked(false);
        }
        if (fontsize == 1) {
            rbFontBig.setChecked(true);
            rbFontNormal.setChecked(false);
        }

        rgFontSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == rbFontNormal.getId()) {
                    fontsize = 2;
                }
                if (checkedId == rbFontBig.getId()) {
                    fontsize = 1;
                }
            }
        });

    }

    @Override
    protected void initData() {
        tvTitle.setText("字体大小");
    }

    @Override
    protected void initEvent() {
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_save:
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
                helper.putValue(GlobelDefines.KEY_FONT_SIZE, fontsize);
                Intent intent = new Intent(FontSizeActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
