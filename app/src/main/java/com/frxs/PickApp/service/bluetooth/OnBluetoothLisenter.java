package com.frxs.PickApp.service.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;


public interface OnBluetoothLisenter
{
	public void onBluetoothStateChanged(boolean isOpened);
	
	public void onBluetoothDiscoveryFinished(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices);

	public void onBluetoothDiscoveryFound(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices);

	public void onBluetoothDiscoveryStarted();
	
	public void onBluetoothBondStateChanged(BluetoothDevice device, boolean isSuccess);
}
