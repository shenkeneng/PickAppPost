package com.frxs.PickApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.core.utils.CommonUtils;
import com.frxs.PickApp.adapter.BluetoothDeviceListAdapter;
import com.frxs.PickApp.application.FrxsApplication;
import com.frxs.PickApp.model.Order;
import com.frxs.PickApp.service.bluetooth.BluetoothService;
import com.frxs.PickApp.service.bluetooth.OnBluetoothLisenter;
import com.frxs.PickApp.service.bluetooth.OnPrintLisenter;
import com.frxs.PickApp.service.bluetooth.PrintDataService;
import com.frxs.PickApp.service.bluetooth.PrintHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BluetoothActivity extends FrxsActivity implements OnBluetoothLisenter, OnPrintLisenter {

    private BluetoothService bluetoothService;

    private PrintDataService printDataService;

    private TextView bhtOperateBtn;

    private ListView bondDeviceslv;

    private BluetoothDeviceListAdapter bondDevicesAdapter;

    private ListView unbondDeviceslv;

    private ImageView ivOpenBlueTooth;

    private TextView tvPrintInfo;

    private LinearLayout llSearchLayout;// 搜索蓝牙布局块

    private LinearLayout llMyDeviceLayout;

    private LinearLayout llOherDeviceLayout;

    private TextView btnSearch;

    private TextView tvTitle;

    private BluetoothDeviceListAdapter unbondDevicesAdapter;

    private ArrayList<BluetoothDevice> unbondDevices = new ArrayList<BluetoothDevice>(); // 用于存放未配对蓝牙设备

    private ArrayList<BluetoothDevice> bondDevices = new ArrayList<BluetoothDevice>();// 用于存放已配对蓝牙设备

    private Order mOrder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initViews() {
        bhtOperateBtn = (TextView) findViewById(R.id.bht_operate_btn);
        bondDeviceslv = (ListView) findViewById(R.id.bond_devices_lv);
        unbondDeviceslv = (ListView) findViewById(R.id.unbond_devices_lv);
        ivOpenBlueTooth = (ImageView) findViewById(R.id.iv_open_bluetooth);
        tvPrintInfo = (TextView) findViewById(R.id.tv_print_info);
        llSearchLayout = (LinearLayout) findViewById(R.id.ll_search);
        llMyDeviceLayout = (LinearLayout) findViewById(R.id.ll_mydevice);
        llOherDeviceLayout = (LinearLayout) findViewById(R.id.ll_otherdevice);
        btnSearch = (TextView) findViewById(R.id.btn_search);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    /**
     * 以下情况，在重新启动是刷新列表
     * 1、按Home键，在设置——>蓝牙界面，取消已经配对的设备.
     * 再次返回蓝牙打印界面时要进行刷新列表，否则在我的设备里面也有未配对的设备。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (bondDevicesAdapter != null) {
            bondDevicesAdapter.notifyDataSetChanged();
        }
        if (unbondDevicesAdapter != null) {
            unbondDevicesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initData() {
        tvTitle.setText("蓝牙打印");

        Intent intent = getIntent();
        if (null != intent) {
            Order order = (Order) intent.getSerializableExtra("ORDER");
            if (order != null) {
                this.mOrder = order;
            }
        }

        printDataService = PrintHelper.getPrintDataService(this);
        printDataService.setPrintLisenter(this);
        bluetoothService = PrintHelper.getBluetoothService(this);
        bluetoothService.setBluetoothLisenter(this);
        bluetoothService.registerReceiver();

        if (this.bluetoothService.isOpen()) {
            BluetoothAdapter bluetoothAdapter = bluetoothService.geteBluetoothAdapter();
            llSearchLayout.setVisibility(View.GONE);
            llMyDeviceLayout.setVisibility(View.VISIBLE);
            llOherDeviceLayout.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            ivOpenBlueTooth.setImageResource(R.mipmap.icon_bluetooth_on);

            if (null != bluetoothAdapter) {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            // Get a set of currently paired devices
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    bondDevices.add(device);
                }
            }

            bondDevicesAdapter = new BluetoothDeviceListAdapter(this, printDataService, bondDevices);
            bondDeviceslv.setAdapter(bondDevicesAdapter);

            unbondDevicesAdapter = new BluetoothDeviceListAdapter(this, unbondDevices);
            unbondDeviceslv.setAdapter(unbondDevicesAdapter);
        } else {
            // bhtOperateBtn.setText(getResources().getString(R.string.open_bluetooth));
            llSearchLayout.setVisibility(View.VISIBLE);
            llOherDeviceLayout.setVisibility(View.VISIBLE);
            llMyDeviceLayout.setVisibility(View.GONE);
            btnSearch.setVisibility(View.GONE);
            ivOpenBlueTooth.setImageResource(R.mipmap.icon_bluetooth);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.unregisterReceiver();
    }

    @Override
    protected void initEvent() {
        bhtOperateBtn.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        unbondDeviceslv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    createBondMethod.invoke(unbondDevices.get(position));
                } catch (Exception e) {
                    Toast.makeText(BluetoothActivity.this, "配对失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.bht_operate_btn: {
                if (bluetoothService.isOpen()) {
                    bluetoothService.searchDevices();
                } else {
                    bluetoothService.openBluetooth(this);
                }
                break;
            }
            case R.id.btn_search: {
                if (CommonUtils.isFastDoubleClick()) {
                    break;
                }

                if (bluetoothService.isOpen()) {
                    if (bluetoothService.isDiscovering()) {
                        bluetoothService.cancelDevices();
                    } else {
                        bluetoothService.searchDevices();
                    }
                }
                break;
            }
            default:
                break;
        }
    }

//	@Override
//	public void handleRequestResponse(BaseData baseRes)
//	{
//		super.handleRequestResponse(baseRes);
//
//		if (baseRes instanceof OrderPrintRes)
//		{
//			OrderPrintRes res = (OrderPrintRes) baseRes;
//			printDataService.setPrintData(res.getData());
//			printDataService.print();
//		}
//
//	}


    @Override
    public void onBluetoothStateChanged(boolean isOpened) {
        if (isOpened) {// 已打开
            // bhtOperateBtn.setText(getResources().getString(R.string.search_bluetooth));
            llSearchLayout.setVisibility(View.GONE);
            llMyDeviceLayout.setVisibility(View.VISIBLE);
            llOherDeviceLayout.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            ivOpenBlueTooth.setImageResource(R.mipmap.icon_bluetooth_on);
        } else {// 未打开
            // bhtOperateBtn.setText(getResources().getString(R.string.open_bluetooth));
            llSearchLayout.setVisibility(View.VISIBLE);
            llOherDeviceLayout.setVisibility(View.VISIBLE);
            llMyDeviceLayout.setVisibility(View.GONE);
            btnSearch.setVisibility(View.GONE);
            ivOpenBlueTooth.setImageResource(R.mipmap.icon_bluetooth);
        }
    }

    @Override
    public void onBluetoothDiscoveryStarted() {
        btnSearch.setText("停止搜索打印机");
    }

    @Override
    public void onBluetoothDiscoveryFound(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {
        this.unbondDevices.clear();
        this.bondDevices.clear();

        this.bondDevices.addAll(bondDevices);
        if (bondDevicesAdapter != null) {
            bondDevicesAdapter.notifyDataSetChanged();
        }

        this.unbondDevices.addAll(unbondDevices);
        if (unbondDevicesAdapter != null) {
            unbondDevicesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBluetoothDiscoveryFinished(List<BluetoothDevice> bondDevices, List<BluetoothDevice> unbondDevices) {
        this.unbondDevices.clear();
        this.bondDevices.clear();

        this.bondDevices.addAll(bondDevices);
        if (bondDevicesAdapter != null) {
            bondDevicesAdapter.notifyDataSetChanged();
        }

        this.unbondDevices.addAll(unbondDevices);
        if (unbondDevicesAdapter != null) {
            unbondDevicesAdapter.notifyDataSetChanged();
        }

        btnSearch.setText("搜索打印机");
    }

    @Override
    public void onBluetoothBondStateChanged(BluetoothDevice device, boolean isSuccess) {
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            if (!bondDevices.contains(device)) {
                // 将绑定好的设备添加的已绑定list集合
                bondDevices.add(device);
            }

            if (unbondDevices.contains(device)) {
                // 将绑定好的设备从未绑定list集合中移除
                unbondDevices.remove(device);
            }

            // 新增适配器实例判断，避免报NullPointException
            if (bondDevicesAdapter != null) {
                bondDevicesAdapter.notifyDataSetChanged();
            }

            if (unbondDevicesAdapter != null) {
                unbondDevicesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onConnectedStateChanged(boolean isConnected) {
        // ToastHelper.toastLong(this, "Bluetooth Connected state " +
        // isConnected);
        bondDevicesAdapter.setPrintingState(isConnected);

        if (!isConnected) {
            progressDialog.dismiss();
//            finish();
        }
    }

    @Override
    public void onConnectFailed(int iReturn) {
        bondDevicesAdapter.setPrintingState(false);
        progressDialog.dismiss();
    }

    public void requestOrderPrint() {
        if (mOrder != null) {
            printDataService.setPrintData(mOrder);
            printDataService.print();
            // 点击了打印按钮则标记为已打印
            FrxsApplication.getInstance().setPrinted(mOrder.getOrderId());
        }
    }

}
