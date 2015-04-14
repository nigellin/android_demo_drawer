package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.*;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class BluetoothFragment extends Fragment{
	private BluetoothAdapter adapter;
	private Switch           switcher;
	private ImageButton      buttonRefresh;
	private MainActivity     activity;
	private BroadcastReceiver receiver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent = inflater.inflate(R.layout.fragment_bluetooth, container, false);
		activity = (MainActivity)getActivity();
		switcher = (Switch)viewParent.findViewById(R.id.view_switch);
		ListView listDevice = (ListView)viewParent.findViewById(R.id.view_list);
		buttonRefresh = (ImageButton)viewParent.findViewById(R.id.button_refresh);
		adapter = BluetoothAdapter.getDefaultAdapter();

		if(adapter == null){
			switcher.setEnabled(false);
			Common.toastLong(getActivity(), "Bluetooth is not available in this device");
		}else if(adapter.isEnabled())
			switcher.setChecked(true);

		final DeviceListAdapter adapterDevice = new DeviceListAdapter();

		listDevice.setAdapter(adapterDevice);

		switcher.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(switcher.isChecked()){
					if(!adapter.isEnabled()){
						Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(intent, 0);
					}

					Common.toastShort(activity, "Bluetooth Enabled");
					buttonRefresh.setEnabled(true);
				}else{
					if(adapter.isEnabled())
						adapter.disable();

					adapterDevice.list.clear();
					adapterDevice.notifyDataSetChanged();

					Common.toastShort(activity, "Bluetooth Disabled");
					buttonRefresh.setEnabled(false);
				}
			}
		});

		buttonRefresh.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				adapterDevice.list.clear();

				if(adapter.isEnabled()){
					adapter.startDiscovery();
					adapterDevice.list.addAll(adapter.getBondedDevices());
					adapterDevice.notifyDataSetChanged();
				}
			}
		});

		receiver= new BroadcastReceiver(){
			@Override public void onReceive(Context context, Intent intent){
				adapterDevice.list.add((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
				adapterDevice.notifyDataSetChanged();
			}
		};

		return viewParent;
	}

	@Override public void onStart(){
		super.onStart();
		activity.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
	}

	@Override public void onStop(){
		super.onStop();
		activity.unregisterReceiver(receiver);
	}

	private class DeviceListAdapter extends BaseAdapter{
		private final List<BluetoothDevice> list = new ArrayList<>();

		@Override public int getCount(){ return list.size(); }

		@Override
		public Object getItem(int position){ return list.get(position); }

		@Override public long getItemId(int position){ return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			TextView viewText = (TextView)activity.getLayoutInflater().inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
			BluetoothDevice device = list.get(position);

			viewText.append(device.getName());
			viewText.append(Html.fromHtml("<b>"+ device.getAddress()+"</b>"));

			return viewText;
		}
	}
}
