package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.views.SpriteSheetView;

public class SpriteFragment extends Fragment{
	SpriteSheetView viewSpriteSheet;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent= inflater.inflate(R.layout.fragment_sprite_sheet, container, false);
		viewSpriteSheet= (SpriteSheetView) viewParent.findViewById(R.id.view_sprite_sheet);

		return viewParent;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		viewSpriteSheet= null;
		System.gc();
	}
}
