package com.frxs.PickApp.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
	
	private static Toast longToast = null;
	private static Toast shortToast = null;

	public static void toastLong(Context context, String content) {
		if (null == longToast) {
			longToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_LONG);
		} else {
			longToast.setText(content);
		}

		longToast.show();
	}

	public static void toastLong(Context context, String content, int gravity) {
		if (null == longToast) {
			longToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_LONG);
			longToast.setGravity(gravity, 0, 0);
		} else {
			longToast.setText(content);
		}

		longToast.show();
	}

	public static void toastShort(Context context, String content) {
		if (null == shortToast) {
			shortToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
		} else {
			shortToast.setText(content);
		}

		shortToast.show();
	}

	public static void toastShort(Context context, String content, int gravity) {
		if (null == shortToast) {
			shortToast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
			shortToast.setGravity(gravity, 0, 0);
		} else {
			shortToast.setText(content);
		}

		shortToast.show();
	}
}
