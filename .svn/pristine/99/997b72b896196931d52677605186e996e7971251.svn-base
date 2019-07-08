package com.frxs.PickApp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.ewu.core.utils.LogUtils;

/**
 * Created by Chentie on 2017/3/22.
 */

public class AutoScollListView extends ListView {

    private DataChangedListener dataChangedListener;

    public interface DataChangedListener{
        public void onDataChanged();
    }

    public AutoScollListView(Context context) {
        super(context);
    }

    public AutoScollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDataChangedListener(DataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    @Override
    protected void handleDataChanged() {
        super.handleDataChanged();

        LogUtils.d(" sssssssssssssssss handleDataChanged");
        if (null != dataChangedListener) {
            dataChangedListener.onDataChanged();
        }
    }

}
