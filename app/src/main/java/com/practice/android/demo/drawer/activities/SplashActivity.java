package com.practice.android.demo.drawer.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import com.practice.android.demo.drawer.utils.Common;

public class SplashActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Point point= new Point();
		getWindowManager().getDefaultDisplay().getSize(point);

		ImageView imageWallpaper= ((ImageView) findViewById(R.id.image_wallpaper));
		imageWallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageWallpaper.setImageBitmap(Common.getBitmapSample(getResources(), R.drawable.wallpaper, point.x, point.y));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.menu_splash, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int id=item.getItemId();
		if(id == R.id.action_settings)
			return true;

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()== MotionEvent.ACTION_DOWN){
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}

		return true;
	}
}
