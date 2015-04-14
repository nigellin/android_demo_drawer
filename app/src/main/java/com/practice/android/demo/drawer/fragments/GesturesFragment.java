package com.practice.android.demo.drawer.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.*;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.practice.android.demo.drawer.activities.R;

public class GesturesFragment extends Fragment implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
	private TextView 	viewLog;
	private ScrollView 	viewScroll;

	private GestureDetectorCompat 	detector;
	private StringBuffer 			previousGesture;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent	= inflater.inflate(R.layout.fragment_gestures, container, false);
		viewLog			= (TextView) viewParent.findViewById(R.id.text_gestures);
		viewScroll		= (ScrollView) viewParent.findViewById(R.id.view_scroll);
		detector		= new GestureDetectorCompat(getActivity(), this);
		previousGesture = new StringBuffer();

		detector.setOnDoubleTapListener(this);

		viewScroll.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				detector.onTouchEvent(event);
				return false;
			}
		});

		((Button) viewParent.findViewById(R.id.button_clear)).setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				previousGesture.setLength(0);
				viewLog.setText("");
			}
		});

		return viewParent;
	}

	private void appendText(CharSequence text){
		if(!previousGesture.toString().equals(text)){
			viewLog.append(text+ "\n");
			previousGesture.setLength(0);
			previousGesture.append(text);
			viewScroll.fullScroll(ScrollView.FOCUS_DOWN);
		}
	}

	@Override
	public boolean onDown(MotionEvent e){
		appendText("DOWN");
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e){ appendText("PRESS"); }
	@Override
	public boolean onSingleTapUp(MotionEvent e){
		appendText("SINGLE_TAP_UP");
		return false;
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		appendText("SCROLL");
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e){ appendText("LONG_PRESS"); }
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		appendText("FLING");
		return false;
	}
	@Override
	public boolean onSingleTapConfirmed(MotionEvent e){
		appendText("SINGLE_TAP");
		return false;
	}
	@Override
	public boolean onDoubleTap(MotionEvent e){
		appendText("DOUBLE_TAP");
		return false;
	}
	@Override
	public boolean onDoubleTapEvent(MotionEvent e){ return false; }
}
