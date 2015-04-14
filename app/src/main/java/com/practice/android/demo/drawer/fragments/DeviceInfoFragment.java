package com.practice.android.demo.drawer.fragments;


import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practice.android.demo.drawer.activities.R;

public class DeviceInfoFragment extends Fragment{
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState){
		View viewParent= inflater.inflate(R.layout.fragment_device_info, container, false);

		((TextView) viewParent.findViewById(R.id.text_device)).setText(Build.DEVICE);
		((TextView) viewParent.findViewById(R.id.text_device_id)).setText(Build.ID);
		((TextView) viewParent.findViewById(R.id.text_device_model)).setText(Build.MODEL);
		((TextView) viewParent.findViewById(R.id.text_device_manufacture)).setText(Build.MANUFACTURER);
		((TextView) viewParent.findViewById(R.id.text_device_hardware)).setText(Build.HARDWARE);
		((TextView) viewParent.findViewById(R.id.text_device_product)).setText(Build.PRODUCT);
		((TextView) viewParent.findViewById(R.id.text_device_version)).setText(Build.VERSION.INCREMENTAL+ " - "+
			Build.VERSION.CODENAME+ " - "+
			Build.VERSION.RELEASE+ " API "+ Build.VERSION.SDK_INT);
		((TextView) viewParent.findViewById(R.id.text_device_os)).setText(System.getProperty("os.version"));

		return viewParent;
	}

}
