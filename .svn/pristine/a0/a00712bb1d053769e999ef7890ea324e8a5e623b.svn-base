package com.frxs.PickApp.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frxs.PickApp.BluetoothActivity;
import com.frxs.PickApp.R;
import com.frxs.PickApp.service.bluetooth.PrintDataService;

import java.util.List;

public class BluetoothDeviceListAdapter extends MyBaseAdapter<BluetoothDevice>
{
	private boolean isPrinting = false;
	private PrintDataService printService;
	private BluetoothActivity context;
	public BluetoothDeviceListAdapter(BluetoothActivity context, List<BluetoothDevice> list)
	{
		super(context, list);
		this.context = context;
	}
	
	public BluetoothDeviceListAdapter(BluetoothActivity context, PrintDataService printService, List<BluetoothDevice> list)
	{
		super(context, list);
		this.printService = printService;
		this.context = context;
	}
	
	public void setPrintingState(boolean isPrinting)
	{
		this.isPrinting = isPrinting;
		notifyDataSetChanged();
	}
	
	@Override
	protected View getItemView(final int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewholder;
		
		if (null == convertView) {
			convertView = inflateItemView(R.layout.bluetooth_device_item);
			
			viewholder = new ViewHolder();
			viewholder.tvDeviceName = (TextView) convertView.findViewById(R.id.device_tv);
			viewholder.tvConnectState = (TextView) convertView.findViewById(R.id.connect_state_tv);
			viewholder.btnPrint = (TextView) convertView.findViewById(R.id.print_btn);
			viewholder.btnPrint.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					BluetoothDevice item = (BluetoothDevice) getItem(position);
					if (null != item) {
						printService.setBtDevice(item);
						context.requestOrderPrint();
					}
				}
			});
			
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		BluetoothDevice item = (BluetoothDevice) getItem(position);
		viewholder.tvDeviceName.setText(item.getName());
		int bondState = item.getBondState();
		if (bondState == BluetoothDevice.BOND_BONDED) {
			viewholder.tvConnectState.setVisibility(View.GONE);
			viewholder.btnPrint.setVisibility(View.VISIBLE);
			viewholder.btnPrint.setEnabled(!isPrinting);
		} else {
			viewholder.tvConnectState.setVisibility(View.VISIBLE);
			viewholder.btnPrint.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public class ViewHolder
	{
		
		public TextView tvDeviceName;
		
		public TextView tvConnectState;
		
		public TextView btnPrint;
	}
}
