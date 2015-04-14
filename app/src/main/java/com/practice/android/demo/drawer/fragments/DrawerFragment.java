package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.services.UserService;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.SharedPreferencesHelper;

import java.util.HashMap;
import java.util.Map;

public class DrawerFragment extends Fragment{
	private final Map<RegisteredFragment, TextView> drawerTexts = new HashMap<>();
	;
	private LinearLayout            viewParent;
	private Fragment                fragmentSelected;
	private MainActivity            activity;
	private DrawerLayout            drawer;
	private SharedPreferencesHelper helper;
	private RegisteredFragment      enumSelectedFragment;

	public void setDrawerLayout(DrawerLayout layout){ this.drawer = layout; }

	public RegisteredFragment getEnumSelectedFragment(){ return enumSelectedFragment; }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		viewParent 	= (LinearLayout)inflater.inflate(R.layout.fragment_drawer, container, false);
		activity 	= (MainActivity)getActivity();
		helper 		= SharedPreferencesHelper.instance(activity);

		for(RegisteredFragment type : RegisteredFragment.values()){
			TextView viewText = new TextView(activity);
			viewText.setId(type.titleId);
			viewText.setText(type.titleId);
			setDrawerTextStyle(viewText);

			drawerTexts.put(type, viewText);
			viewParent.addView(viewText);
		}

		return viewParent;
	}

	private void setDrawerTextStyle(TextView viewText){
		Resources res     = activity.getResources();
		int       padding = (int)res.getDimension(R.dimen.gap_small);

		viewText.setGravity(Gravity.END);
		viewText.setPadding(padding, padding, padding, padding);
		viewText.setTextSize(res.getDimension(R.dimen.text_small));
		viewText.setClickable(true);
		updateDrawerTextStyle(viewText, false);

		viewText.setOnClickListener(onDrawerTextClicked());
	}

	public void selectFragment(RegisteredFragment registeredFragment){
		if(registeredFragment == null || enumSelectedFragment == registeredFragment)
			return;

		enumSelectedFragment = registeredFragment;

		Class clazz= enumSelectedFragment.fragmentClass;

		if(clazz!= null){
			fragmentSelected = Common.newFragmentInstance(clazz);

			if(fragmentSelected != null){
				FragmentTransaction transaction= activity.getFragmentManager().beginTransaction();

				transaction
					.setCustomAnimations(R.animator.enter, R.animator.exit)
					.replace(R.id.layout_dynamic, fragmentSelected)
					.commit();

				activity.setTitle(registeredFragment.titleId);
				drawer.closeDrawers();

				TextView selectedViewText= drawerTexts.get(registeredFragment);
				updateDrawerTextStyle(selectedViewText, true);
				unselectDrawerTextExcluded(selectedViewText);
			}
		}
	}

	private View.OnClickListener onDrawerTextClicked(){
		return new View.OnClickListener(){
			public void onClick(View v){
				selectFragment(Common.findFragmentTypeById(v.getId()));

				if(v.getId()== R.string.text_drawer_profile)
					updateDrawerTextProfile((TextView) v);
			}
		};
	}

	private void unselectDrawerTextExcluded(TextView viewText){
		for(TextView text: drawerTexts.values())
			if(!viewText.equals(text)){
				updateDrawerTextStyle(text, false);
				viewText.setSelected(false);
			}
	}

	private void updateDrawerTextStyle(TextView viewText, boolean isSelected){
		viewText.setSelected(isSelected);

		int colorBG		= Color.BLACK;
		int colorText	= Color.WHITE;
		int typeface	= Typeface.NORMAL;

		if(isSelected){
			colorBG		= Color.WHITE;
			colorText	= Color.BLACK;
			typeface	= Typeface.BOLD_ITALIC;
		}

		viewText.setBackgroundColor(colorBG);
		viewText.setTextColor(colorText);
		viewText.setTypeface(Typeface.DEFAULT, typeface);
	}

	public void updateDrawerTextProfile(TextView viewText){
		if(helper.containsKey(UserService.KEY_USERNAME))
			viewText.setText(Html.fromHtml("Hi, <i><u>" + helper.getString(UserService.KEY_USERNAME, "GUEST") + "</u></i>"));
	}

	public void toggleAuthenticationText(boolean isAuthenticated){
		TextView textSignIn	= drawerTexts.get(RegisteredFragment.SIGN_IN);
		TextView textSignUp	= drawerTexts.get(RegisteredFragment.SIGN_UP);
		TextView textSignOut= drawerTexts.get(RegisteredFragment.SIGN_OUT);
		TextView textProfile= drawerTexts.get(RegisteredFragment.PROFILE);

		textSignIn.setEnabled(!isAuthenticated);
		textSignUp.setEnabled(!isAuthenticated);
		textSignOut.setEnabled(isAuthenticated);
		textProfile.setEnabled(isAuthenticated);

		textSignIn.setVisibility(!isAuthenticated? View.VISIBLE: View.GONE);
		textSignUp.setVisibility(!isAuthenticated? View.VISIBLE: View.GONE);
		textSignOut.setVisibility(isAuthenticated? View.VISIBLE: View.GONE);
		textProfile.setVisibility(isAuthenticated? View.VISIBLE: View.GONE);
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		drawerTexts.clear();
		viewParent = null;
	}

	public void onSignIn(){
		if(isValidFragment(SignInFragment.class)){
			((SignInFragment)fragmentSelected).doSignIn();
			//updateDrawerTextProfile();
		}
	}

	public void onSignUp(){
		if(isValidFragment(SignUpFragment.class))
			((SignUpFragment)fragmentSelected).doSignUp();
	}

	public void onClear(){
		if(isValidFragment(SignInFragment.class))
			((SignInFragment)fragmentSelected).clearFields();

		if(isValidFragment(SignUpFragment.class))
			((SignUpFragment)fragmentSelected).clearFields();
	}

	public void onNotificationStart(){
		if(isValidFragment(NotificationFragment.class))
			((NotificationFragment)fragmentSelected).doNotify();
	}

	public void onNotificationCancel(){
		if(isValidFragment(NotificationFragment.class))
			((NotificationFragment)fragmentSelected).doNotificationCancel();
	}

	public void onNotifyCountdown(){
		if(isValidFragment(NotificationFragment.class))
			((NotificationFragment)fragmentSelected).doNotifyCountdown();
	}

	public void onNotifyCountdownCancel(){
		if(isValidFragment(NotificationFragment.class))
			((NotificationFragment)fragmentSelected).doNotifyCountdownCancel();
	}

	public void onCanvasClear(){
		if(isValidFragment(CanvasFragment.class))
			((CanvasFragment)fragmentSelected).doCanvasClear();
	}

	private boolean isValidFragment(Class clazz){
		return fragmentSelected != null && clazz.isInstance(fragmentSelected);
	}

	public enum RegisteredFragment{
		HOME(HomeFragment.class, R.string.text_drawer_home),
		PROFILE(ProfileFragment.class, R.string.text_drawer_profile),
		NOTIFICATION(NotificationFragment.class, R.string.text_drawer_notification),
		CANVAS(CanvasFragment.class, R.string.text_drawer_canvas),
		GESTURES(GesturesFragment.class, R.string.text_drawer_gestures),
		SPRITE_SHEET(SpriteFragment.class, R.string.text_drawer_sprite_sheet),
		WEB(WebFragment.class, R.string.text_drawer_web),
		DIAL(DialFragment.class, R.string.text_drawer_dial),
		FILE_MANAGER(FileManageFragment.class, R.string.text_drawer_file_manage),
		DEVICE_INFO(DeviceInfoFragment.class, R.string.text_drawer_device_info),
		SENSOR(SensorFragment.class, R.string.text_drawer_sensor),
		BLUETOOTH(BluetoothFragment.class, R.string.text_drawer_bluetooth),
		SIGN_UP(SignUpFragment.class, R.string.text_drawer_sign_up),
		SIGN_IN(SignInFragment.class, R.string.text_drawer_sign_in),
		SIGN_OUT(null, R.string.text_drawer_sign_out);

		public final int 	titleId;
		public final Class 	fragmentClass;
		RegisteredFragment(Class fragmentClass, int titleId){
			this.fragmentClass = fragmentClass;
			this.titleId = titleId;
		}
	}
}
