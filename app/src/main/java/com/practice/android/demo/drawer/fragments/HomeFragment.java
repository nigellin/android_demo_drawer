package com.practice.android.demo.drawer.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;

import com.practice.android.demo.drawer.activities.R;

public class HomeFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState){
		View viewParent= inflater.inflate(R.layout.fragment_home, container, false);
		((TextClock) viewParent.findViewById(R.id.clock_text)).setFormat12Hour("hh:mm:ss a");

		return viewParent;
	}
}
