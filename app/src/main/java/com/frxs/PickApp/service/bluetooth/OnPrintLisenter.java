package com.frxs.PickApp.service.bluetooth;



public interface OnPrintLisenter
{
	public void onConnectedStateChanged(boolean isConnected);

	//蓝牙连接失败
	public void onConnectFailed(int iReturn);
}
