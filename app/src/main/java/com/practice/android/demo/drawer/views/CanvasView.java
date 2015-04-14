package com.practice.android.demo.drawer.views;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class CanvasView extends SurfaceView implements GestureDetector.OnGestureListener{
	private float x, y;
	private Path            path;
	private Paint           paint;
	private Canvas          canvas;
	private SurfaceHolder   holder;
	private CanvasRunnable  runnable;
	private int             action;
	private GestureDetector detector;
	private AtomicBoolean   isValidMotion;

	public CanvasView(Context context, AttributeSet set){
		super(context, set);
		runnable=new CanvasRunnable();
		paint	=newPaint();
		holder	=getHolder();
		detector=new GestureDetector(getContext(), this);
		isValidMotion=new AtomicBoolean(false);
		x= y= 0;
		runnable.start();

		holder.addCallback(new SurfaceHolder.Callback(){
			public void surfaceCreated(SurfaceHolder holder){ clear(); }
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}
			public void surfaceDestroyed(SurfaceHolder holder){}
		});
	}

	public Paint newPaint(){
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		return paint;
	}

	public void setStrokeWidth(float width){ paint.setStrokeWidth(width); }

	public void setStrokeColor(int color){ paint.setColor(color); }

	public void stop(){ runnable.stop(); }

	public boolean onTouchEvent(MotionEvent event){
		detector.onTouchEvent(event);
		float x=event.getX(), y=event.getY();
		action=event.getAction();
		switch(action){
			case MotionEvent.ACTION_DOWN:
				path=new Path();
				path.moveTo(x, y);
				this.x=x;
				this.y=y;
				break;
			case MotionEvent.ACTION_MOVE:
				path.quadTo(this.x, this.y, (this.x + x) / 2, (this.y + y) / 2);
				this.x=x;
				this.y=y;
				break;
			case MotionEvent.ACTION_UP:
				path.lineTo(x, y);
				break;
			default:
				return false;
		}
		return true;
	}

	public void clear(){
		if(holder.getSurface().isValid()){
			Canvas canvas;
			if((canvas = holder.lockCanvas()) != null){
				canvas.drawARGB(255, 255, 255, 255);
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public boolean onDown(MotionEvent e){
		isValidMotion.set(false);
		return false;
	}

	public void onShowPress(MotionEvent e){ isValidMotion.set(false); }

	public boolean onSingleTapUp(MotionEvent e){
		isValidMotion.set(false);
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		isValidMotion.set(true);
		return false;
	}

	public void onLongPress(MotionEvent e){ isValidMotion.set(false); }

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
		isValidMotion.set(false);
		return false;
	}

	private class CanvasRunnable implements Runnable{
		private boolean isRunning;
		private Thread  thread;

		private CanvasRunnable(){
			isRunning=false;
			thread=new Thread(this);
		}

		public void start(){
			isRunning=true;
			thread.start();
		}

		public void stop(){
			isRunning=false;
			while(thread.isAlive())
				try{
					thread.join();
				}catch(Exception e){}
		}

		public void run(){
			while(isRunning){
				synchronized(holder){
					if(isValidMotion.get())
						if(holder.getSurface().isValid() && path != null){
							if((canvas = holder.lockCanvas()) != null){
								canvas.drawPath(path, paint);
								holder.unlockCanvasAndPost(canvas);
							}
						}
				}
			}
		}
	}
}
