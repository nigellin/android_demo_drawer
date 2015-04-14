package com.practice.android.demo.drawer.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Sprite;

public class SpriteSheetView extends SurfaceView{
	private final SurfaceHolder  holder;
	private       SpriteRunnable runnable;
	private       Bitmap         bitmap;
	private       Sprite         sprite;

	public SpriteSheetView(Context context, AttributeSet attrs){
		super(context, attrs);
		holder= getHolder();
		bitmap= Common.getBitmapSample(getResources(), R.drawable.numbers, 430, 331);
	}

	@Override
	protected void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		runnable.stop();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		sprite 	= new Sprite(bitmap, 5, 2, w, h);
		runnable= new SpriteRunnable();
		runnable.start();
	}

	private class SpriteRunnable implements Runnable{
		private boolean isRunning= false;
		private Thread	thread;

		public void start(){
			if(thread!= null)
				stop();

			isRunning	= true;
			thread		= new Thread(this, "SPRITE_RUNNABLE_THREAD");
			thread.start();
		}

		public void stop(){
			isRunning= false;
			boolean isStopped= false;

			while(!isStopped){
				try{
					thread.join();
					isStopped= true;
				}catch(Exception e){}
			}

			thread= null;
		}

		public void run(){
			while(isRunning){
				if(!holder.getSurface().isValid())
					continue;

				synchronized(holder){
					Canvas canvas;
					if((canvas = holder.lockCanvas()) != null){
						sprite.drawSprite(canvas);
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}
}
