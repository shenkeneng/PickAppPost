package com.frxs.PickApp.fragment;

import android.view.View;

import com.ewu.core.base.BaseFragment;
import com.frxs.PickApp.comms.Config;
import com.frxs.PickApp.rest.service.ApiService;
import com.frxs.PickApp.application.FrxsApplication;

public abstract class FrxsFragment extends BaseFragment
{

	public ApiService getService()
	{
		return FrxsApplication.getRestClient(Config.TYPE_BASE).getApiService();
	}

	protected abstract int getLayoutId();
	protected abstract void initViews(View view);
	protected abstract void initEvent();
	protected abstract void initData();

}
