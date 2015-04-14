package com.practice.android.demo.drawer.utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.practice.android.demo.drawer.activities.R;

public class Validation{
	private Context context;

	private final StringBuilder error;

	private Validation(){
		error= new StringBuilder();
	}

	private static Validation instance;
	public static Validation instance(){
		if(instance== null)
			instance= new Validation();

		return instance;
	}

	public Validation setContext(Context context){
		this.context= context;
		return this;
	}


	public boolean fieldCheck(EditText field, Check... checks){ return fieldCheck(field, null, checks); }
	public boolean fieldCheck(EditText field, Object value, Check... checks){
		Common.requireNonNull(context, "Null context object in Validation class");

		final String text= field.getText().toString().trim();

		for(Check check: checks){
			try{
				switch(check){
					case REQUIRE:
						if(text.isEmpty())
							error.append(context.getString(R.string.error_require));

						break;

					case COMPARE:
						Common.requireNonNull(value, "Require a EditText object");

						EditText 	field_	= (EditText) value;
						String 		text_	= field_.getText().toString().trim();

						if(!text.equals(text_))
							error.append(context.getString(R.string.error_compare, field_.getHint()));

						break;

					case LENGTH_MIN:
						Common.requireNonNull(value, "Require a integer value");

						try{
							int min= Integer.parseInt(value.toString());

							if(text.length()< min)
								error.append(context.getString(R.string.error_length_min, min));
						}catch(NumberFormatException nfe){ error.append(context.getString(R.string.error_internal)); }

						break;

					case LENGTH_MAX:
						Common.requireNonNull(value, "Require a integer value");

						try{
							int max= Integer.parseInt(value.toString());

							if(text.length()> max)
								error.append(context.getString(R.string.error_length_max, max));
						}catch(NumberFormatException nfe){ error.append(context.getString(R.string.error_internal)); }

						break;

					case LENGTH_BETWEEN:
						Common.requireNonNull(value, "Require a String in \"min,max\" format");

						try{
							String[] range= value.toString().split(",");

							int x= Integer.parseInt(range[0]);
							int y= Integer.parseInt(range[1]);

							int max= Math.max(x, y);
							int min= Math.min(x, y);

							if(text.length()< min || text.length()> max)
								error.append(context.getString(R.string.error_length_between, min, max));
						}catch(NumberFormatException nef){ error.append(context.getString(R.string.error_internal)); }

						break;

					case LENGTH_EXACT:
						try{
							int max= Integer.parseInt(value.toString());

							if(text.length()> max)
								error.append(context.getString(R.string.error_length_max, max));
						}catch(NumberFormatException nfe){ error.append(context.getString(R.string.error_internal)); }

						break;

					case PATTERN_EMAIL:
						if(!Patterns.EMAIL_ADDRESS.matcher(text).matches())
							error.append(context.getString(R.string.error_pattern, "email"));

						break;

					case PATTERN_URL:
						if(!Patterns.WEB_URL.matcher(text).matches())
							error.append(context.getString(R.string.error_pattern, "URL"));

						break;
				}

				if(error.length()> 0){
					field.setError(error);
					return false;
				}
			}finally{ error.setLength(0); }
		};

		return true;
	}

	public enum Check{ REQUIRE, COMPARE, PATTERN_EMAIL, PATTERN_URL, LENGTH_MIN, LENGTH_MAX, LENGTH_BETWEEN, LENGTH_EXACT}
}
