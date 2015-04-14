package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.services.UserService;
import com.practice.android.demo.drawer.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SignInFragment extends Fragment{
	private EditText            fieldId;
	private EditText            fieldPassword;
	private View                viewParent;
	private HttpPost            httpPost;
	private List<NameValuePair> postValues;
	private StringBuilder       bufferStream;
	private MainActivity        activity;
	private SignInTask          task;

	private static final String SUBMIT_TYPE = "SignIn";
	private static final String URL_SIGN_IN = "http://192.168.0.5:8888/androidnet/web/app_dev.php/submit/" + SUBMIT_TYPE+ "/"+ UserService.DEVICE_TOKEN;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		viewParent 		= inflater.inflate(R.layout.fragment_sign_in, container, false);
		fieldId 		= (EditText)viewParent.findViewById(R.id.field_id);
		fieldPassword 	= (EditText)viewParent.findViewById(R.id.field_password);
		activity        = (MainActivity) getActivity();
		bufferStream 	= new StringBuilder();
		postValues 		= new ArrayList<>();
		httpPost 		= new HttpPost(URL_SIGN_IN);
		progressDialog 	= new ProgressDialog(activity);

		progressDialog.setCancelable(true);
		progressDialog.setMessage("Signing In...");
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				if(task != null)
					task.cancel(true);
			}
		});


		Typeface font= Typeface.createFromAsset(activity.getAssets(), "HelveticaNeue.dfont");

		fieldId.setTypeface(font, Typeface.ITALIC);
		fieldPassword.setTypeface(font);

		return viewParent;
	}

	public void doSignIn(){
		Validation validation= Validation.instance().setContext(activity);

		boolean isValid= validation.fieldCheck(fieldId, Validation.Check.REQUIRE);
		isValid &= validation.fieldCheck(fieldPassword, Validation.Check.REQUIRE);

		if(isValid){
			//			Intent intent = new Intent(getActivity(), UserService.class);
			//			intent.setAction(UserService.ACTION_SIGN_IN);
			//
			//			intent.putExtra(UserService.KEY_ID, Common.fieldTextTrimmed(fieldId));
			//			intent.putExtra(UserService.KEY_PASSWORD, Common.fieldTextTrimmed(fieldPassword));
			//
			//			activity.startService(intent);
			try{
				postValues.clear();
				postValues.add(new BasicNameValuePair(UserService.KEY_ID, Common.fieldTextTrimmed(fieldId)));
				postValues.add(new BasicNameValuePair(UserService.KEY_PASSWORD, Common.fieldTextTrimmed(fieldPassword)));

				httpPost.setEntity(new UrlEncodedFormEntity(postValues));

				task= new SignInTask();
				task.execute();
			}catch(UnsupportedEncodingException e){ Common.toastException(activity, e); }
		}
	}

	public void clearFields(){ Common.fieldsClear(fieldId, fieldPassword); }

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		fieldId 		= null;
		fieldPassword 	= null;
		viewParent		= null;
	}

	public class SignInTask extends AsyncTask<Void, Throwable, JSONObject>{
		protected void onPreExecute(){ progressDialog.show(); }
		protected void onCancelled(){
			super.onCancelled();
			Common.toastShort(activity, "Sign In Cancelled");
		}

		protected JSONObject doInBackground(Void... voidValue){
			try{ return Common.httpJsonResponse(activity.getHttpClient(), httpPost, bufferStream);
			}catch(Exception e){ publishProgress(e);
			}finally{ progressDialog.dismiss(); }

			return null;
		}

		protected void onProgressUpdate(Throwable... values){
			Common.toastException(activity, values[0]);
			cancel(true);
		}

		protected void onPostExecute(JSONObject jsonParent){
			if(jsonParent== null)
				return;

			try{
				boolean hasError= jsonParent.getBoolean("hasError");
				String 	message = jsonParent.optString("message");

				if(hasError){
					JSONObject jsonChild = jsonParent.optJSONObject("errors");

					if(jsonChild!= null){
						String errorId 			= jsonChild.optString(UserService.KEY_ID, null);
						String errorPassword 	= jsonChild.optString(UserService.KEY_PASSWORD, null);

						fieldId.setError(errorId);
						fieldPassword.setError(errorPassword);
					}
				}else{
					JSONObject jsonChild= jsonParent.getJSONObject("user");

					SharedPreferencesHelper helper= SharedPreferencesHelper.instance(activity);
					helper.putStrings(
						new String[]{
							UserService.KEY_NAME,
							UserService.KEY_USERNAME,
							UserService.KEY_EMAIL,
							UserService.KEY_ROLE,
							UserService.KEY_PASSWORD,
							UserService.KEY_DATE_CREATED,
							UserService.KEY_DATE_MODIFIED},
						jsonChild.getString(UserService.KEY_NAME),
						jsonChild.getString(UserService.KEY_USERNAME),
						jsonChild.getString(UserService.KEY_EMAIL),
						jsonChild.getString(UserService.KEY_ROLE),
						jsonChild.getString(UserService.KEY_PASSWORD),
						jsonChild.getString(UserService.KEY_DATE_CREATED),
						jsonChild.optString(UserService.KEY_DATE_MODIFIED, ""));

					activity.selectFragment(DrawerFragment.RegisteredFragment.SIGN_IN);
				}

				Common.toastShort(activity, message);
			}catch(Exception e){ publishProgress(e); }
		}
	}
}
