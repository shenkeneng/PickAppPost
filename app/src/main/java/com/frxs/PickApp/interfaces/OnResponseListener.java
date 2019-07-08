package com.frxs.PickApp.interfaces;

import android.content.Intent;

/**
 * Created by Chentie on 2017/2/9.
 */

public interface OnResponseListener {

    public void onResponse(Intent data);

    public void onFailure(Throwable t);
}
