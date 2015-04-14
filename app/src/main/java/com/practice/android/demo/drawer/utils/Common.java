package com.practice.android.demo.drawer.utils;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.Toast;
import com.practice.android.demo.drawer.fragments.DrawerFragment.RegisteredFragment;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Common{
	private Context context;
	private static Common instance;

	private Common(){}

	public static Common instance(){
		if(instance== null)
			instance 		= new Common();

		return instance;
	}

	public static <T>T requireNonNull(T object){ return requireNonNull(object, "Null reference"); }
	public static <T>T requireNonNull(T object, String error){
		if(object== null)
			throw new NullPointerException(error);

		return object;
	}

	public static void requireSameArrayLength(Object[] objects, Object[] objects_){
		if(objects.length!= objects_.length)
			throw new IllegalArgumentException("Length of two arrays are not matched");
	}

	public Common setContext(Context context){
		this.context= context;
		return this;
	}

	public void toastShort(Object text){ toastShort(context, text); }
	public void toastLong(Object text){ toastLong(context, text); }

	public static void toastShort(Context context, Object text){ toast(context, text, Toast.LENGTH_SHORT); }
	public static void toastLong(Context context, Object text){ toast(context, text, Toast.LENGTH_LONG); }
	public static void toast(Context context, Object text, int delay){
		if(text== null)
			return;

		String message= text.toString();

		if(!message.isEmpty())
			Toast.makeText(requireNonNull(context), message, delay).show();
	}

	public static void fieldsClear(EditText... fields){
		for(EditText field: fields)
			field.getText().clear();
	}

	public static String fieldTextTrimmed(EditText field){ return field.getText().toString().trim(); }

	public static JSONObject httpJsonResponse(HttpClient client, HttpPost post, StringBuilder bufferStream) throws IOException, JSONException{
		HttpResponse response= client.execute(post);
		bufferStream.setLength(0);

		BufferedReader reader= new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line;

		while((line= reader.readLine())!= null)
			bufferStream.append(line);

		return new JSONObject(bufferStream.toString());
	}

	public static void toastException(Context context, Throwable throwable){
		if(throwable== null)
			return;

		throwable.printStackTrace();
		toastLong(context, throwable.getLocalizedMessage());
	}

	public static Bitmap getBitmapSample(Resources resources, int resourceId, int requireWidth, int requireHeight){
		BitmapFactory.Options options= new BitmapFactory.Options();
		options.inJustDecodeBounds= true;

		BitmapFactory.decodeResource(resources, resourceId, options);

		final int 	width	= options.outWidth;
		final int 	height	= options.outHeight;
		int sampleSize		= 1;

		while((width/ sampleSize)> requireWidth || (height/ sampleSize)> requireHeight)
			sampleSize*= 2;

		options.inSampleSize= sampleSize;
		options.inJustDecodeBounds= false;

		return BitmapFactory.decodeResource(resources, resourceId, options);
	}

	public static <T extends Fragment>T newFragmentInstance(Class<T> clazz){
		try{
			return clazz.newInstance();
		}catch(Exception e){ e.printStackTrace(); }

		return null;
	}

	public static RegisteredFragment findFragmentTypeById(int id){
		for(RegisteredFragment type: RegisteredFragment.values())
			if(type.titleId== id)
				return type;

		return null;
	}
}
