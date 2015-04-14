package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
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

public class ProfileFragment extends Fragment{
	private EditText            fieldUsername;
	private EditText            fieldPassword;
	private EditText            fieldPassword_;
	private EditText            fieldName;
	private EditText            fieldEmail;
	private EditText            fieldRole;
	private EditText            fieldDateCreated;
	private EditText            fieldDateModified;
	private TableRow            rowPassword_;
	private Button              buttonUpdate;
	private ToggleButton        buttonToggle;
	private ProgressDialog      dialogProgress;
	private MainActivity        activity;

	private profileUpdateTask   task;
	private HttpPost            httpPost;
	private List<NameValuePair> postValues;
	private StringBuilder       bufferStream;

	private SharedPreferencesHelper helper;

	public static final String SUBMIT_TYPE          ="ProfileUpdate";
	public static final String URL_PROFILE_UPDATE   ="http://192.168.0.5:8888/androidnet/web/app_dev.php/submit/"+ SUBMIT_TYPE+ "/"+ UserService.DEVICE_TOKEN;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent = inflater.inflate(R.layout.fragment_profile, container, false);
		fieldUsername   = (EditText)viewParent.findViewById(R.id.field_username);
		fieldPassword   = (EditText)viewParent.findViewById(R.id.field_password);
		fieldPassword_  = (EditText)viewParent.findViewById(R.id.field_password_);
		fieldName       = (EditText)viewParent.findViewById(R.id.field_name);
		fieldEmail      = (EditText)viewParent.findViewById(R.id.field_email);
		fieldRole       = (EditText)viewParent.findViewById(R.id.field_role);
		fieldDateCreated= (EditText)viewParent.findViewById(R.id.field_date_created);
		fieldDateModified= (EditText) viewParent.findViewById(R.id.field_date_modified);
		rowPassword_    = (TableRow)viewParent.findViewById(R.id.table_row_password_);
		activity        = (MainActivity)getActivity();
		dialogProgress  = new ProgressDialog(activity);
		buttonUpdate    = (Button)viewParent.findViewById(R.id.button_save);
		buttonToggle    = (ToggleButton)viewParent.findViewById(R.id.button_action);
		helper          = SharedPreferencesHelper.instance(activity);

		httpPost    = new HttpPost(URL_PROFILE_UPDATE);
		postValues  = new ArrayList<>();
		bufferStream= new StringBuilder();

		buttonToggle.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){ toggleEdit(buttonToggle.isChecked()); }
		});

		buttonUpdate.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){ onUpdate(); }
		});

		dialogProgress.setCancelable(true);
		dialogProgress.setMessage("Updating Profile...");
		dialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				if(task != null)
					task.cancel(true);
			}
		});

		updateFields();

		return viewParent;
	}

	private void toggleEdit(boolean isEditable){
		buttonUpdate.setVisibility(isEditable? View.VISIBLE: View.INVISIBLE);
		fieldName.setEnabled(isEditable);
		fieldPassword.setEnabled(isEditable);
		fieldPassword_.setEnabled(isEditable);
		rowPassword_.setVisibility(isEditable? View.VISIBLE: View.GONE);
	}

	private void updateFields(){
		fieldUsername.setText(helper.getString(UserService.KEY_USERNAME, "GUEST"));
		fieldPassword.setText(helper.getString(UserService.KEY_PASSWORD, ""));
		fieldPassword_.setText(helper.getString(UserService.KEY_PASSWORD, ""));
		fieldName.setText(helper.getString(UserService.KEY_NAME, ""));
		fieldRole.setText(helper.getString(UserService.KEY_ROLE, "USER"));
		fieldEmail.setText(helper.getString(UserService.KEY_EMAIL, "user@example.com"));
		fieldDateCreated.setText(helper.getString(UserService.KEY_DATE_CREATED, ""));
		fieldDateModified.setText(helper.getString(UserService.KEY_DATE_MODIFIED, ""));
	}

	public void onUpdate(){
		Validation validation = Validation.instance().setContext(getActivity());
		boolean    isValid    = validation.fieldCheck(fieldName, Validation.Check.REQUIRE);

		isValid &= validation.fieldCheck(fieldPassword, 6, Validation.Check.REQUIRE, Validation.Check.LENGTH_MIN);
		isValid &= validation.fieldCheck(fieldPassword_, fieldPassword, Validation.Check.REQUIRE, Validation.Check.COMPARE);

		String name     = Common.fieldTextTrimmed(fieldName);
		String password = Common.fieldTextTrimmed(fieldPassword);

		if(isValid && helper.getString(UserService.KEY_NAME, "").equals(name)
		   && helper.getString(UserService.KEY_PASSWORD, "").equals(password)){
			Common.toastShort(activity, "Nothing Changes");
			isValid= false;
		}

		if(isValid){
			try{
				postValues.clear();
				postValues.add(new BasicNameValuePair(UserService.KEY_USERNAME, fieldUsername.getText().toString()));
				postValues.add(new BasicNameValuePair(UserService.KEY_NAME, name));
				postValues.add(new BasicNameValuePair(UserService.KEY_PASSWORD, password));

				httpPost.setEntity(new UrlEncodedFormEntity(postValues));
				task= new profileUpdateTask();
				task.execute();
			}catch(UnsupportedEncodingException e){ Common.toastException(activity, e); }
		}
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		fieldUsername = null;
		fieldPassword = null;
		fieldName = null;
		fieldEmail = null;
		fieldRole = null;
		fieldDateCreated = null;
		buttonUpdate = null;
		buttonToggle = null;
	}

	private class profileUpdateTask extends AsyncTask<Void, Throwable, JSONObject>{
		protected void onPreExecute(){ dialogProgress.show(); }
		protected JSONObject doInBackground(Void... params){
			try{ return Common.httpJsonResponse(activity.getHttpClient(), httpPost, bufferStream);
			}catch(Exception e){ publishProgress(e);
			}finally{ dialogProgress.dismiss(); }

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
				String message  = jsonParent.optString("message");
				if(!hasError){
					JSONObject jsonChild= jsonParent.getJSONObject("user");

					if(jsonChild.getString(UserService.KEY_USERNAME).equals(helper.getString(UserService.KEY_USERNAME, ""))){
						helper.putStrings(
							new String[]{ UserService.KEY_NAME, UserService.KEY_PASSWORD, UserService.KEY_DATE_MODIFIED },
							jsonChild.getString(UserService.KEY_NAME),
							jsonChild.getString(UserService.KEY_PASSWORD),
							jsonChild.getString(UserService.KEY_DATE_MODIFIED));

						updateFields();
						toggleEdit(false);
						buttonToggle.setChecked(false);
					}else
						message= "Internal Error: retrieved a invalid username";
				}

				Common.toastShort(activity, message);
			}catch(Exception e){ publishProgress(e); }
		}
	}
}
