package com.practice.android.demo.drawer.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;

import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Validation;

public class WebFragment extends Fragment{
	private Activity 		activity;
	private EditText 		fieldUrl;
	private WebView 		viewWeb;
	private ImageButton 	buttonEnter;
	private ImageButton 	buttonClear;
	private ProgressDialog 	dialogProgress;
	private WebViewClient 	webClient;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent= inflater.inflate(R.layout.fragment_web, container, false);

		activity	= getActivity();
		fieldUrl	= (EditText) viewParent.findViewById(R.id.field_url);
		viewWeb		= (WebView) viewParent.findViewById(R.id.view_web);
		buttonEnter	= (ImageButton) viewParent.findViewById(R.id.button_enter);
		buttonClear	= (ImageButton) viewParent.findViewById(R.id.button_clear);
		dialogProgress= new ProgressDialog(activity);
		webClient	= new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
				dialogProgress.show();
				super.onPageStarted(view, url, favicon);
				fieldUrl.setText(url);
			}

			@Override
			public void onPageFinished(WebView view, String url){
				super.onPageFinished(view, url);
				dialogProgress.dismiss();
			}
		};

		buttonEnter.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){ loadUrl(); }
		});

		buttonClear.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){ fieldUrl.setText(""); }
		});

		fieldUrl.addTextChangedListener(new TextWatcher(){
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			public void afterTextChanged(Editable s){
				if(s.toString().trim().isEmpty())
					buttonClear.setVisibility(View.GONE);
				else
					buttonClear.setVisibility(View.VISIBLE);
			}
		});

		fieldUrl.setOnKeyListener(new View.OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event){
				if(event.getKeyCode()== KeyEvent.KEYCODE_ENTER){
					loadUrl();
					return true;
				}

				return false;
			}
		});

		dialogProgress.setOnCancelListener(new DialogInterface.OnCancelListener(){
			public void onCancel(DialogInterface dialog){
				viewWeb.stopLoading();
				Common.toastShort(activity, "Stop Loading");
			}
		});

		dialogProgress.setMessage("Loading...");
		dialogProgress.setCancelable(true);

		viewWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		viewWeb.getSettings().setJavaScriptEnabled(true);
		viewWeb.setWebViewClient(webClient);

		return viewParent;
	}

	private void loadUrl(){
		correctUrl();
		Validation validation= Validation.instance().setContext(activity);

		if(validation.fieldCheck(fieldUrl, Validation.Check.REQUIRE, Validation.Check.PATTERN_URL)){
			viewWeb.loadUrl(fieldUrl.getText().toString());
		}
	}

	private void correctUrl(){
		String url= Common.fieldTextTrimmed(fieldUrl);

		if(!url.startsWith("http://") && !url.startsWith("https://"))
			url= "http://"+ url;

		fieldUrl.setText(url);
	}
}
