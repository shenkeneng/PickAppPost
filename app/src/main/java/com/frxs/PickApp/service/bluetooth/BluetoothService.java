package com.frxs.PickApp.service.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.ewu.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothService
{
	
	private static final String TAG = BluetoothService.class.getSimpleName();

	private static final int REQUEST_OPEN_BT_CODE = 1;
	
	private Context context;
	
	private OnBluetoothLisenter lisenter;
	
	private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
	private ArrayList<BluetoothDevice> unbondDevices = new ArrayList<BluetoothDevice>(); // 用于存放未配对蓝牙设备
	
	private ArrayList<BluetoothDevice> bondDevices = new ArrayList<BluetoothDevice>();// 用于存放已配对蓝牙设备
	
//	private ProgressDialog progressDialog = null;
	
	private Handler handler;

	private boolean mReceiverTag = false;   //广播接受者标识

	public BluetoothService(Context context)
	{
		this.context = context;
	}
	
	public BluetoothService(Context context, OnBluetoothLisenter lisenter)
	{
		this.context = context;
		this.lisenter = lisenter;
	}

	public void setBluetoothLisenter(OnBluetoothLisenter lisenter) {
		this.lisenter = lisenter;
	}
	
	public void registerReceiver() {
		if (!mReceiverTag) {
			// 设置广播信息过滤
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			// 注册广播接收器，接收并处理搜索结果
			context.registerReceiver(receiver, intentFilter);
			mReceiverTag = true;    //标识值 赋值为 true 表示广播已被注册
		} else {
			ToastUtils.show(context, "蓝牙广播已经被注册");
		}
	}
	
	public void unregisterReceiver() {
		if (mReceiverTag) {
			context.unregisterReceiver(receiver);
			mReceiverTag = false;   //Tag值 赋值为false 表示该广播已被注销
		} else {
			ToastUtils.show(context, "蓝牙广播已经被注销");
		}
	}
	
	public BluetoothAdapter geteBluetoothAdapter()
	{
		return bluetoothAdapter;
	}

	public boolean checkBluetoothAddress(String address) {
		return bluetoothAdapter.checkBluetoothAddress(address);
	}

	public Set<BluetoothDevice> getBondedDevices() {
		return bluetoothAdapter.getBondedDevices();
	}

	public BluetoothDevice getRemoteDevice(String address) {
		return bluetoothAdapter.getRemoteDevice(address);
//		bluetoothAdapter.cancelDiscovery();
	}

	/**
	 * 打开蓝牙
	 */
	public void openBluetooth(Activity activity)
	{
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		activity.startActivityForResult(enableBtIntent, REQUEST_OPEN_BT_CODE);
	}
	
	/**
	 * 关闭蓝牙
	 */
	public void closeBluetooth()
	{
		this.bluetoothAdapter.disable();
	}
	
	/**
	 * 判断蓝牙是否打开
	 * 
	 * @return boolean
	 */
	public boolean isOpen()
	{
		return this.bluetoothAdapter.isEnabled();
	}

	public boolean isDiscovering() {
		return bluetoothAdapter.isDiscovering();
	}
	
	/**
	 * 搜索蓝牙设备
	 */
	public void searchDevices()
	{
		this.bondDevices.clear();
		this.unbondDevices.clear();
		
		// 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
		this.bluetoothAdapter.startDiscovery();
	}

	public void cancelDevices() {
		this.bluetoothAdapter.cancelDiscovery();
	}
	
	/**
	 * 添加未绑定蓝牙设备到list集合
	 * 
	 * @param device
	 */
	public void addUnbondDevices(BluetoothDevice device)
	{
		Log.i(TAG, "未绑定设备名称：" + device.getName());
		if (!this.unbondDevices.contains(device))
		{
			this.unbondDevices.add(device);
		}
	}
	
	/**
	 * 添加已绑定蓝牙设备到list集合
	 * 
	 * @param device
	 */
	public void addBandDevices(BluetoothDevice device)
	{
		Log.i(TAG, "已绑定设备名称：" + device.getName());
		if (!this.bondDevices.contains(device))
		{
			this.bondDevices.add(device);
		}
	}
	
	/**
	 * 蓝牙广播接收器
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		
		@Override
		public void onReceive(final Context context, Intent intent)
		{
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_BONDED)
				{
					addBandDevices(device);
				}
				else
				{
					addUnbondDevices(device);
				}

				if (null != lisenter)
				{
					lisenter.onBluetoothDiscoveryFound(bondDevices, unbondDevices);
				}

			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
			{
				Log.i(TAG, "开始搜索设备");
//				progressDialog = ProgressDialog.show(context, "请稍等", "搜索蓝牙设备中...", true);
				handler = new Handler();
				handler.postDelayed(runnable, 25000);

				if (null != lisenter)
				{
					lisenter.onBluetoothDiscoveryStarted();
				}
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
			{
				Log.i(TAG, "设备搜索完毕");
//				progressDialog.dismiss();
				if (null != handler) {
					handler.removeCallbacks(runnable);
				}
				
				if (null != lisenter)
				{
					lisenter.onBluetoothDiscoveryFinished(bondDevices, unbondDevices);
				}
				// bluetoothAdapter.cancelDiscovery();
			}
			else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
			{
				if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
				{
					Log.i(TAG, "--------打开蓝牙-----------");
					if (null != lisenter)
					{
						lisenter.onBluetoothStateChanged(true);
					}
					// searchDevices.setEnabled(true);
					// bondDevicesListView.setEnabled(true);
					// unbondDevicesListView.setEnabled(true);
				}
				else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
				{
					Log.i(TAG, "--------关闭蓝牙-----------");
					if (null != lisenter)
					{
						lisenter.onBluetoothStateChanged(false);
					}
					// searchDevices.setEnabled(false);
					// bondDevicesListView.setEnabled(false);
					// unbondDevicesListView.setEnabled(false);
				}
			}
			else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
			{
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState())
				{
				case BluetoothDevice.BOND_BONDING:
					Log.d("BlueToothTestActivity", "正在配对......");
					ToastUtils.show(context, "正在配对......");
					break;
				case BluetoothDevice.BOND_BONDED:
					Log.d("BlueToothTestActivity", "完成配对");
					ToastUtils.show(context, "完成配对");
					if (null != lisenter)
					{
						lisenter.onBluetoothBondStateChanged(device, true);
					}
					break;
				case BluetoothDevice.BOND_NONE:
					Log.d("BlueToothTestActivity", "取消配对");
					ToastUtils.show(context, "取消配对");
					if (null != lisenter)
					{
						lisenter.onBluetoothBondStateChanged(device, false);
					}
				default:
					break;
				}
			}
		}
	};
	
	Runnable runnable = new Runnable()
	{
		@Override
		public void run()
		{
			if (bluetoothAdapter.isDiscovering())
            {
//				progressDialog.dismiss();
				bluetoothAdapter.cancelDiscovery();
				ToastUtils.show(context, "蓝牙搜索超时，请重试！");
            }
		}
	};
}
