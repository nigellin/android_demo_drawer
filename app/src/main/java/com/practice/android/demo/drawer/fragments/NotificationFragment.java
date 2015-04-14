package com.practice.android.demo.drawer.fragments;


import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Validation;

public class NotificationFragment extends Fragment{
	private static final int NOTIFICATION_ID= 8080;
	private static int counter= 0;

	private NotificationManager notificationManager;
	private EditText 			fieldNotification;
	private NumberPicker 		picker;
	private Activity 			activity;
	private PendingIntent		intent;
	private CountdownTask		countdownTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent		= inflater.inflate(R.layout.fragment_notification, container, false);
		activity			= getActivity();
		notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		fieldNotification	= (EditText) viewParent.findViewById(R.id.field_notification);
		picker				= (NumberPicker) viewParent.findViewById(R.id.number_picker);
		intent				= PendingIntent.getActivity(activity, 0, new Intent(activity, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		countdownTask		= new CountdownTask();
		return viewParent;
	}

	@Override
	public void onStart(){
		super.onStart();
		picker.setMaxValue(60);
		picker.setMinValue(0);
	}

	public void doNotificationCancel(){ notificationManager.cancel(NOTIFICATION_ID); }
	public void doNotify(){
		if(Validation.instance().setContext(activity).fieldCheck(fieldNotification, Validation.Check.REQUIRE)){
			Notification.Builder builder = new Notification.Builder(activity);

			String text = Common.fieldTextTrimmed(fieldNotification);

			builder.
				setAutoCancel(true).
				setContentText(text).
				setContentTitle("from " + activity.getString(R.string.app_name)).
				setTicker("New Notification").
				setNumber(++counter).
				setContentIntent(intent).
				setSmallIcon(R.drawable.app_icon).
				setDefaults(Notification.DEFAULT_SOUND);

			notificationManager.notify(NOTIFICATION_ID, builder.build());
			fieldNotification.setText("");
		}
	}

	public void doNotifyCountdown(){
		if(Validation.instance().setContext(activity).fieldCheck(fieldNotification, Validation.Check.REQUIRE))
			switch(countdownTask.getStatus()){
				case FINISHED:
					countdownTask = new CountdownTask();
				case PENDING:
					countdownTask.execute(picker.getValue());
					break;
			}
	}

	public void doNotifyCountdownCancel(){
		if(countdownTask.getStatus()== AsyncTask.Status.RUNNING)
			countdownTask.cancel(true);
	}

	private void toggleNodes(boolean isEnabled){
		picker.setEnabled(isEnabled);
		fieldNotification.setEnabled(isEnabled);
	}

	private class CountdownTask extends AsyncTask<Integer, Integer, Void>{
		@Override
		protected Void doInBackground(Integer... params){
			int value= params[0];

			while(value--> picker.getMinValue()){
				if(isCancelled())
					break;

				SystemClock.sleep(1000);
				publishProgress(value);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values){
			picker.setValue(values[0]);
		}

		@Override
		protected void onPreExecute(){
			toggleNodes(false);
		}

		@Override
		protected void onPostExecute(Void progress){
			doNotify();
			toggleNodes(true);
		}

		@Override
		protected void onCancelled(){
			toggleNodes(true);
		}
	}
}
