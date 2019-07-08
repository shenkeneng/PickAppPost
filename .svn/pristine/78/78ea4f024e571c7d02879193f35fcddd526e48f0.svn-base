
package com.frxs.PickApp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewu.core.utils.DisplayUtil;
import com.frxs.PickApp.R;

/**
 * @author cate 2015-1-6 下午3:27:46
 */

public class EmptyView extends LinearLayout
{
	
	private ImageView mImage;
	private TextView mText;
	private TextView mBtn;
	
	public static final int MODE_LOADING = 0;
	public static final int MODE_NODATA = 1;
	public static final int MODE_ERROR = 2;
	public static final int MODE_ERROR_DATA = 3;
	
	@SuppressLint("NewApi")
	public EmptyView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initView();
	}
	
	public EmptyView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView();
	}
	
	public EmptyView(Context context)
	{
		super(context);
		initView();
	}
	
	@SuppressLint("NewApi")
	private void initView()
	{
		this.setId(R.id.emptyview);
		this.setGravity(Gravity.CENTER);
		this.setOrientation(LinearLayout.VERTICAL);
		mImage = new ImageView(getContext());
		mImage.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mImage);
		
		mText = new TextView(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(0, DisplayUtil.dip2px(getContext(), 10), 0, DisplayUtil.dip2px(getContext(), 10));
		mText.setLayoutParams(params);
		mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		this.addView(mText);
		
		mBtn = new TextView(getContext());
		mBtn.setLayoutParams(params);
		mBtn.setGravity(Gravity.CENTER);
		
//		if (SystemUtils.getSDKVersion() < 16)
//		{
//			mBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.mine_03));
//		}
//		else
//		{
//			mBtn.setBackground(getResources().getDrawable(R.drawable.mine_03));
//		}
		
		this.addView(mBtn);
		mBtn.setVisibility(View.GONE);
		
//		if (SystemUtils.getSDKVersion() < 16)
//		{
//			// mMin.setBackgroundDrawable(ViewUtils.getStateDrawable(getContext(), normal, active, disable));
//		}
//		else
//		{
//			// mMin.setBackground(ViewUtils.getStateDrawable(getContext(), normal, active, disable));
//		}
		
		this.setMode(MODE_LOADING);
		
		this.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void setBtnTextAndListener(String text, OnClickListener listener)
	{
		mBtn.setVisibility(View.VISIBLE);
		mBtn.setText(text);
		mBtn.setOnClickListener(listener);
	}
	
	public void setImageResource(int rId)
	{
		if (getVisibility() == View.GONE)
		{
			this.setVisibility(View.VISIBLE);
		}
		mImage.setImageResource(rId);
	}
	
	public void setText(String text)
	{
		if (getVisibility() == View.GONE)
		{
			this.setVisibility(View.VISIBLE);
		}
		mText.setText(text);
	}
	
	public void setMode(int mode)
	{
		if (getVisibility() == View.GONE)
		{
			this.setVisibility(View.VISIBLE);
		}
		
		switch (mode)
		{
			case MODE_LOADING:
				this.setText("正在加载...");
				break;
			case MODE_NODATA:
				this.setText("没有数据");
				break;
			case MODE_ERROR:
				this.setText("获取数据失败，请点击重试");
				break;
			case MODE_ERROR_DATA:
				this.setText("获取的数据状态已经变更，请点击重试");
				break;
			default:
				break;
		}
	}
	
	public ImageView getmImage()
	{
		return mImage;
	}
	
	public void setmImage(ImageView mImage)
	{
		this.mImage = mImage;
	}
	
	public TextView getmText()
	{
		return mText;
	}
	
	public void setmText(TextView mText)
	{
		this.mText = mText;
	}
	
	public TextView getmBtn()
	{
		return mBtn;
	}
	
	public void setmBtn(TextView mBtn)
	{
		this.mBtn = mBtn;
	}
	
}
