package com.frxs.PickApp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ewu.core.utils.CheckUtils;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.LogUtils;
import com.ewu.core.utils.MathUtils;
import com.ewu.core.utils.SystemUtils;
import com.frxs.PickApp.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author cate 2014-12-18 上午10:31:00
 */

public class CountEditText extends LinearLayout implements OnClickListener {

    private static final int DECIMAL_DIGITS = 2;//小数的位数

    private double mCount = 0.0;

    private EditText mEdit;

    private ImageView mMin;

    private ImageView mAdd;

    private onCountChangeListener mOnCountChangeListener;

    //    private int maxCount;
    private double maxCount;

    @SuppressLint("NewApi")
    public CountEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(attrs);
    }

    public CountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CountEditText(Context context) {
        super(context);
        initView(null);
    }

    private void initView(AttributeSet attrs) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_countedittext, null);
        mMin = (ImageView) view.findViewById(R.id.count_sub);
        mAdd = (ImageView) view.findViewById(R.id.count_add);
        mEdit = (EditText) view.findViewById(R.id.count_edit);
        mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));

        mMin.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        this.addView(view);
        if (SystemUtils.getSDKVersion() < 16) {
            // mMin.setBackgroundDrawable(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        } else {
            // mMin.setBackground(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        }

        mEdit.addTextChangedListener(new TextWatcher() {
                                         @Override
                                         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                         }

                                         @Override
                                         public void onTextChanged(CharSequence s, int start, int before, int count) {
                                             Log.i("WMTest", "onTextChanged  count =2 " + count);
                                             if (s.toString().contains(".")) {
                                                 if (s.length() - 1 - s.toString().indexOf(".") > DECIMAL_DIGITS) {
                                                     s = s.toString().subSequence(0,
                                                             s.toString().indexOf(".") + DECIMAL_DIGITS + 1);
                                                     mEdit.setText(s);
                                                     mEdit.setSelection(s.length());
                                                 }
                                             }
                                             if (s.toString().trim().substring(0).equals(".")) {
                                                 s = "0" + s;
                                                 mEdit.setText(s);
                                                 mEdit.setSelection(2);
                                             }
                                             if (s.toString().startsWith("0")
                                                     && s.toString().trim().length() > 1) {
                                                 if (!s.toString().substring(1, 2).equals(".")) {
                                                     mEdit.setText(s.subSequence(1, s.toString().trim().length()));
                                                     return;
                                                 }
                                             }
                                         }

                                         @Override
                                         public void afterTextChanged(Editable s) {
                                             String value = mEdit.getText().toString().trim();
                                             mEdit.setSelection(value.length());
                                             if (CheckUtils.isValidNumber(value)) {
                                                 mCount = Double.valueOf(value);
                                             } else {
                                                 mCount = 1;
                                             }
                                             LogUtils.e("CountEditText afterTextChanged getText()=" + mEdit.getText() + " s=" + s + " Count=" + mCount);
                                             if (mOnCountChangeListener != null) {
                                                 mOnCountChangeListener.onCountEdit(mCount);
                                             }

                                             // 直接修改数量时判断当前减少数量的标识状态
                                             if (getCount() <= 0) {
                                                 mMin.setEnabled(false);
                                                 mMin.setImageResource(R.mipmap.icon_subtract_disable);
                                             } else {
                                                 mMin.setEnabled(true);
                                                 mMin.setImageResource(R.mipmap.icon_subtract_enable);
                                             }

                                             // 直接修改数量时判断当前增加数量的标识状态
                                             if (getCount() < getMaxCount()) {
                                                 mAdd.setEnabled(true);
                                                 mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
                                             } else {
                                                 mAdd.setEnabled(false);
                                                 mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
                                             }
                                         }
                                     }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.count_sub: {
                if (getCount() > 0) {
                    mCount = MathUtils.sub(mCount, 1);
                    if (mCount <= 0) {
                        mCount = 0.0;
                        mMin.setEnabled(false);
                        mMin.setImageResource(R.mipmap.icon_subtract_disable);
                    }
                    if (mCount < getMaxCount()) {
                        mAdd.setEnabled(true);
                        mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
                    } else {
                        mCount = getMaxCount();
                    }
                    mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));
                    if (mOnCountChangeListener != null) {
                        mOnCountChangeListener.onCountSub(mCount);
                    }
                }
                break;
            }
            case R.id.count_add: {
                if (getCount() < maxCount) {
                    mCount = MathUtils.add(mCount, 1);
                    if (mCount >= maxCount) {
                        mCount = maxCount;
                        mMin.setEnabled(true);
                        mMin.setImageResource(R.mipmap.icon_subtract_enable);
                    }
                    if (mCount == maxCount) {
                        mAdd.setEnabled(false);
                        mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
                    }
                    mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));
                    if (mOnCountChangeListener != null) {
                        mOnCountChangeListener.onCountAdd(mCount);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public onCountChangeListener getOnCountChangeListener() {
        return mOnCountChangeListener;
    }

    public void setOnCountChangeListener(onCountChangeListener mOnCountChangeListener) {
        this.mOnCountChangeListener = mOnCountChangeListener;
    }

    public double getCount() {
        return mCount;
    }

    public void setCount(double mCount) {
        this.mCount = mCount;
        if (mCount <= 0) {
            mCount = 0.0;
            mMin.setEnabled(false);
            mMin.setImageResource(R.mipmap.icon_subtract_disable);

            mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));
        } else if (0 < mCount && mCount < getMaxCount()) {
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);

            mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));
        } else {
            // 当前购物车数量与最大库存相等时显示最大库存，且“+”不可操作“-”号可操作|陈铁
            mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(maxCount)));
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public double getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(double maxCount) {
        this.maxCount = maxCount;
        mCount = 0;
        mEdit.setText(DisplayUtil.subZeroAndDot(MathUtils.twolittercountString(mCount)));
        mMin.setEnabled(false);
        mMin.setImageResource(R.mipmap.icon_subtract_disable);
        if (maxCount > 0) {
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
        } else {
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public interface onCountChangeListener {

        void onCountAdd(double count);

        void onCountSub(double count);

        void onCountEdit(double count);
    }

    //强制显示或者关闭系统键盘
    public void KeyBoard222(final String status)
    {

        Timer timer = new Timer();
        String text = mEdit.getText().toString();
        timer.schedule(new TimerTask(){
            @Override
            public void run()
            {
                InputMethodManager m = (InputMethodManager)
                        mEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(status.equals("open"))
                {
                    m.showSoftInput(mEdit,InputMethodManager.SHOW_FORCED);
                }
                else
                {
                    m.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
                }
            }
        }, 300);
        if (SystemUtils.getSDKVersion() >= 23) {
            mEdit.setText(text);
        }
    }

}
