package com.practice.android.demo.drawer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.services.UserService;

public class SharedPreferencesHelper{
	private SharedPreferences sharedPreferences;

	private static SharedPreferencesHelper instance;

	protected SharedPreferencesHelper(){}

	public static SharedPreferencesHelper instance(Context context){ return instance(context, MainActivity.PREFERENCES_NAME, Context.MODE_PRIVATE); }
	public static SharedPreferencesHelper instance(Context context, String name, int mode){
		if(instance== null)
			instance= new SharedPreferencesHelper();

		instance.setSharedPreferences(context.getSharedPreferences(name, mode));

		return instance;
	}

	public boolean isAuthenticated(){ return containsKey(UserService.KEY_USERNAME) && containsKey(UserService.KEY_EMAIL) && containsKey(UserService.KEY_NAME); }

	public boolean containsKey(String key){ return Common.requireNonNull(sharedPreferences).contains(key); }
	public boolean clearPreferences(){ return getSharedPreferencesEditor().clear().commit(); }

	public void setSharedPreferences(SharedPreferences preferences){ sharedPreferences= preferences; }
	public void setSharedPreferences(Context context, String name, int mode){ setSharedPreferences(context.getSharedPreferences(name, mode)); }

	public SharedPreferences getSharedPreferences(){ return Common.requireNonNull(sharedPreferences); }
	public SharedPreferences.Editor getSharedPreferencesEditor(){ return getSharedPreferences().edit(); }

	public String getString(String key, String defaultValue){ return getSharedPreferences().getString(key, defaultValue); }
	public void putString(String key, String value){ _putObject(ValueType.STRING, key, value); }
	public void putStrings(String[] keys, String... values){ _putObjects(ValueType.STRING, keys, values); }

	public int getInt(String key, int defaultValue){ return getSharedPreferences().getInt(key, defaultValue); }
	public void putInt(String key, int value){ _putObject(ValueType.INT, key, value); }
	public void putInts(String[] keys, int... values){ _putObjects(ValueType.STRING, keys, values); }

	public long getLong(String key, long defaultValue){ return getSharedPreferences().getLong(key, defaultValue); }
	public void putLong(String key, long value){ _putObject(ValueType.LONG, key, value); }
	public void putLongs(String[] keys, long... values){ _putObjects(ValueType.LONG, keys, values); }

	public float getFloat(String key, float defaultValue){ return getSharedPreferences().getFloat(key, defaultValue); }
	public void putFloat(String key, float value){ _putObject(ValueType.FLOAT, key, value); };
	public void putFloats(String[] keys, float... values){ _putObjects(ValueType.FLOAT, keys, values); }

	public boolean getBoolean(String key, boolean defaultValue){ return getSharedPreferences().getBoolean(key, defaultValue); }
	public void putBoolean(String key, boolean value){ _putObject(ValueType.BOOLEAN, key, value); }
	public void putBooleans(String[] keys, Boolean... values){ _putObjects(ValueType.BOOLEAN, keys, values); }

	private void _putObjects(ValueType type, String[] keys, Object... values){
		Common.requireSameArrayLength(keys, values);

		SharedPreferences.Editor editor = getSharedPreferencesEditor();

		for(int i= 0; i< keys.length; i++)
			_putWithoutCommit(editor, type, keys[i], values[i]);

		editor.commit();
	}

	private void _putObject(ValueType type, String key, Object... value){
		SharedPreferences.Editor editor = getSharedPreferencesEditor();
		_putWithoutCommit(editor, type, key, value);
		editor.commit();
	}

	private void _putWithoutCommit(SharedPreferences.Editor editor, ValueType type, String key, Object value){
		switch(type){
			case STRING:
				editor.putString(key, (String) value);
				break;
			case INT:
				editor.putInt(key, (int) value);
				break;
			case LONG:
				editor.putLong(key, (long) value);
				break;
			case FLOAT:
				editor.putFloat(key, (float) value);
				break;
		}
	}

	public enum ValueType{ STRING, BOOLEAN, INT, LONG, FLOAT }
}
