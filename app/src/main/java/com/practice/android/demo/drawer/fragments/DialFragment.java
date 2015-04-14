package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Validation;

public class DialFragment extends Fragment{
	private Button   buttonCall, buttonSend;
	private EditText fieldMessage, fieldPhone;
	private MainActivity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent = inflater.inflate(R.layout.fragment_dial, container, false);
		activity = (MainActivity)getActivity();
		buttonCall = (Button)viewParent.findViewById(R.id.button_call);
		buttonSend= (Button) viewParent.findViewById(R.id.button_send);
		fieldMessage = (EditText)viewParent.findViewById(R.id.field_message);
		fieldPhone = (EditText)viewParent.findViewById(R.id.field_phone);

		buttonCall.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				boolean isValid= Validation.instance().setContext(activity).fieldCheck(fieldPhone, Validation.Check.REQUIRE);

				if(!isValid)
					return;

				Intent intent= new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+Common.fieldTextTrimmed(fieldPhone)));
				startActivity(intent);
			}
		});

		buttonSend.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				boolean isValid= Validation.instance().setContext(activity).fieldCheck(fieldPhone, Validation.Check.REQUIRE);
				isValid|= Validation.instance().fieldCheck(fieldMessage, Validation.Check.REQUIRE);

				if(!isValid)
					return;

				try{
					SmsManager manager = SmsManager.getDefault();
					manager.sendTextMessage(fieldPhone.getText().toString(), null, fieldMessage.getText().toString(), null, null);
					Common.toastShort(activity, "Message is sent");
				}catch(Exception e){
					Common.toastShort(activity, "Failed to send the message");
				}
			}
		});

		return viewParent;
	}
}
