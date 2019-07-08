package com.frxs.PickApp;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Endoon on 2016/4/2.
 */
public class PhotoLookActivity extends FrxsActivity {
    private PhotoView mPhotoView;

    private String mPhotoUrl;

    private String mBarCode;

    private TextView tvBarCode;

    private String mProductName;

    private TextView tvProductName;

    private String barcode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_look;
    }

    @Override
    protected void initViews() {
        mPhotoView = (PhotoView) findViewById(R.id.iv_photo);
        tvBarCode = (TextView) findViewById(R.id.tv_barcode);
        tvProductName = (TextView) findViewById(R.id.tv_product_name);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mPhotoUrl = intent.getStringExtra("PHOTOURL");
            barcode = intent.getStringExtra("BARCODE");
            mProductName = intent.getStringExtra("PRODUCT_NAME");
        }

        if (!TextUtils.isEmpty(barcode)) {
            tvBarCode.setText("条码：\n"+ barcode);
        }else {
            tvBarCode.setText("条码：暂无");
        }

        if (!TextUtils.isEmpty(mProductName)){
            tvProductName.setText("商品名称：" + mProductName);
        } else {
            tvProductName.setText("商品名称：暂无");
        }

        if (!TextUtils.isEmpty(mPhotoUrl)) {
            Picasso.with(this).load(mPhotoUrl).into(mPhotoView);
        } else {
            Toast toast = Toast.makeText(PhotoLookActivity.this, "该商品没有图片，任意点击界面返回",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    @Override
    protected void initEvent() {
        // 点击返回
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
    }
}
