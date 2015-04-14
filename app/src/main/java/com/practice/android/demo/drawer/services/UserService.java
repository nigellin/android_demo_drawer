package com.practice.android.demo.drawer.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UserService extends Service{
	public static final String ACTION_RESULT    ="ACTION_RESULT";
	public static final String ACTION_ERROR     ="ACTION_ERROR";
	public static final String KEY_ID           ="id";
	public static final String KEY_NAME         ="name";
	public static final String KEY_USERNAME     ="username";
	public static final String KEY_EMAIL        ="email";
	public static final String KEY_ROLE         ="role";
	public static final String KEY_PASSWORD     ="password";
	public static final String KEY_DATE_CREATED ="dateCreated";
	public static final String KEY_DATE_MODIFIED="dateModified";
	public static final String DEVICE_TOKEN     = "401d142ff0e1075071bb47aaf171240419070baac0281d1a33778897a2e886b5";

	@Override
	public IBinder onBind(Intent intent){ return null; }

	@Override
	public int onStartCommand(Intent intent_, int flags, int startId){
		Intent intent=new Intent();
		sendBroadcast(intent);
		stopSelf();
		return super.onStartCommand(intent_, flags, startId);
	}
}
