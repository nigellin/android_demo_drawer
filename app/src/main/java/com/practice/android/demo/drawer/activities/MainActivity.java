package com.practice.android.demo.drawer.activities;

import android.content.*;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.*;
import com.practice.android.demo.drawer.fragments.DrawerFragment;
import com.practice.android.demo.drawer.services.UserService;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.SharedPreferencesHelper;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import com.practice.android.demo.drawer.fragments.DrawerFragment.RegisteredFragment;

public class MainActivity extends ActionBarActivity{
	public static final String PREFERENCES_NAME = "AUTHENTICATION";
	public static final String SWITCH_FRAGMENT  = "SWITCH_FRAGMENT";
	private DrawerLayout          layoutDrawer;
	private ActionBarDrawerToggle drawerToggle;
	private DrawerFragment        fragmentDrawer;
//	private MainReceiver          receiver;
	private Common                common;
	private HttpClient            httpClient;
	private final RegisteredFragment defaultSelectedId = RegisteredFragment.BLUETOOTH;

	private void initVariables(){
		common 			= Common.instance().setContext(this);
//		receiver 		= new MainReceiver();
		fragmentDrawer 	= (DrawerFragment)getFragmentManager().findFragmentById(R.id.fragment_drawer);
		layoutDrawer 	= (DrawerLayout)findViewById(com.practice.android.demo.drawer.activities.R.id.layout_drawer);
		drawerToggle	= new ActionBarDrawerToggle(this, layoutDrawer, R.string.drawer_open, com.practice.android.demo.drawer.activities.R.string.drawer_close);
		httpClient 		= newHttpClient();
	}

	private HttpClient newHttpClient(){
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		return new DefaultHttpClient(params);
	}

	public HttpClient getHttpClient(){ return httpClient; }

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initVariables();

		layoutDrawer.setDrawerListener(drawerToggle);
		layoutDrawer.setScrimColor(Color.TRANSPARENT);
		fragmentDrawer.setDrawerLayout(layoutDrawer);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		IntentFilter filter= new IntentFilter();

		filter.addAction(UserService.ACTION_RESULT);
		filter.addAction(UserService.ACTION_ERROR);

//		registerReceiver(receiver, filter);
		selectFragment(defaultSelectedId);
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
//		unregisterReceiver(receiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		if(drawerToggle.onOptionsItemSelected(item))
			return true;

		switch(item.getItemId()){
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	public void selectFragment(RegisteredFragment type){
		boolean isAuthenticated= SharedPreferencesHelper.instance(this).isAuthenticated();

		switch(type){
			case SIGN_IN:
			case SIGN_UP:
				if(isAuthenticated)
					type= RegisteredFragment.PROFILE;
				break;

			case SIGN_OUT:
				if(!isAuthenticated)
					type= RegisteredFragment.SIGN_IN;
				break;
		}

		fragmentDrawer.selectFragment(type);
		fragmentDrawer.toggleAuthenticationText(isAuthenticated);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putSerializable(SWITCH_FRAGMENT, fragmentDrawer.getEnumSelectedFragment());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		selectFragment((RegisteredFragment)savedInstanceState.get(SWITCH_FRAGMENT));
	}

	public void onSignIn(View view){ fragmentDrawer.onSignIn(); }
	public void onSignUp(View view){ fragmentDrawer.onSignUp(); }
	public void onClear(View view){ fragmentDrawer.onClear(); }

	public void onNotificationStart(View view){ fragmentDrawer.onNotificationStart(); }
	public void onNotificationCancel(View view){ fragmentDrawer.onNotificationCancel(); }
	public void onNotifyCountdown(View view){ fragmentDrawer.onNotifyCountdown(); }
	public void onNotifyCountdownCancel(View view){ fragmentDrawer.onNotifyCountdownCancel(); }

	public void onCanvasClear(View view){ fragmentDrawer.onCanvasClear(); }

	public class MainReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			String action= intent.getAction();

			if(intent.getExtras().containsKey(SWITCH_FRAGMENT))
				selectFragment((RegisteredFragment)intent.getSerializableExtra(SWITCH_FRAGMENT));

			common.toastShort(intent.getStringExtra(action));
		}
	}
}
