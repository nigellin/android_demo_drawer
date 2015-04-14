package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.services.UserService;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Validation;
import com.practice.android.demo.drawer.utils.Validation.Check;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment extends Fragment{
	private EditText            fieldName;
	private EditText            fieldUsername;
	private EditText            fieldEmail;
	private EditText            fieldPassword;
	private EditText            fieldPassword_;
	private View                viewParent;
	private MainActivity        activity;
	private SignUpTask          task;
	private HttpPost            httpPost;
	private List<NameValuePair> postValues;
	private StringBuilder       bufferStream;

	private ProgressDialog		progressDialog;

	private static final String SUBMIT_TYPE = "SignUp";
	private static final String URL_SIGN_UP = "http://192.168.0.5:8888/androidnet/web/app_dev.php/submit/" + SUBMIT_TYPE+ "/"+ UserService.DEVICE_TOKEN;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		viewParent 		= inflater.inflate(R.layout.fragment_sign_up, container, false);
		fieldName 		= (EditText)viewParent.findViewById(R.id.field_name);
		fieldUsername 	= (EditText)viewParent.findViewById(R.id.field_username);
		fieldEmail 		= (EditText)viewParent.findViewById(R.id.field_email);
		fieldPassword	= (EditText)viewParent.findViewById(R.id.field_password);
		fieldPassword_ 	= (EditText)viewParent.findViewById(R.id.field_password_);
		activity 		= (MainActivity)getActivity();

		postValues 		= new ArrayList<>();
		httpPost 		= new HttpPost(URL_SIGN_UP);
		bufferStream 	= new StringBuilder();
		progressDialog	= new ProgressDialog(activity);

		progressDialog.setCancelable(true);
		progressDialog.setMessage("Signing Up...");
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				if(task!= null)
					task.cancel(true);
			}
		});

		return viewParent;
	}

	public void doSignUp(){
		Validation validation= Validation.instance().setContext(activity);

		boolean isValid= validation.fieldCheck(fieldName, Check.REQUIRE);
		isValid&= validation.fieldCheck(fieldUsername, 5, Check.REQUIRE, Check.LENGTH_MIN);
		isValid&= validation.fieldCheck(fieldPassword, 6, Check.REQUIRE, Check.LENGTH_MIN);
		isValid&= validation.fieldCheck(fieldPassword_, fieldPassword, Check.REQUIRE, Check.COMPARE);
		isValid&= validation.fieldCheck(fieldEmail, Check.REQUIRE, Check.PATTERN_EMAIL);

		if(isValid){
//
			try{
				postValues.clear();
				postValues.add(new BasicNameValuePair(UserService.KEY_USERNAME, Common.fieldTextTrimmed(fieldUsername)));
				postValues.add(new BasicNameValuePair(UserService.KEY_EMAIL, Common.fieldTextTrimmed(fieldEmail)));
				postValues.add(new BasicNameValuePair(UserService.KEY_NAME, Common.fieldTextTrimmed(fieldName)));
				postValues.add(new BasicNameValuePair(UserService.KEY_ROLE, "USER"));
				postValues.add(new BasicNameValuePair(UserService.KEY_PASSWORD, Common.fieldTextTrimmed(fieldPassword)));

				httpPost.setEntity(new UrlEncodedFormEntity(postValues));

				task= new SignUpTask();
				task.execute();
			}catch(Exception e){
				e.printStackTrace();
				Common.toastLong(activity, e.getLocalizedMessage());
			}
		}
	}

	public void clearFields(){
		Common.fieldsClear(fieldName, fieldUsername, fieldEmail, fieldPassword, fieldPassword_);
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();

		fieldName		= null;
		fieldUsername	= null;
		fieldPassword_	= null;
		fieldPassword	= null;
		fieldEmail		= null;
		viewParent 		= null;
	}

	private class SignUpTask extends AsyncTask<Void, Throwable, JSONObject>{
		protected void onPreExecute(){ progressDialog.show(); }

		protected JSONObject doInBackground(Void... params){
			try{ return Common.httpJsonResponse(activity.getHttpClient(), httpPost, bufferStream);
			}catch(Exception e){ publishProgress(e);
			}finally{ progressDialog.dismiss(); }

			return null;
		}

		protected void onPostExecute(JSONObject jsonParent){
			if(jsonParent== null)
				return;

			try{
				boolean hasError= jsonParent.getBoolean("hasError");
				String message	= jsonParent.optString("message");

				if(hasError){
					JSONObject jsonChild= jsonParent.getJSONObject("errors");

					if(jsonChild!= null){
						String errorUsername= jsonChild.optString(UserService.KEY_USERNAME, null);
						String errorEmail	= jsonChild.optString(UserService.KEY_EMAIL, null);

						fieldUsername.setError(errorUsername);
						fieldEmail.setError(errorEmail);
					}
				}else
					activity.selectFragment(DrawerFragment.RegisteredFragment.SIGN_IN);

				Common.toastLong(activity, message);
			}catch(Exception e){ publishProgress(e); }
		}
	}
}
