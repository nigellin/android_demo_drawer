package com.practice.android.demo.drawer.utils;

import android.graphics.*;
import android.os.SystemClock;

public class Sprite{
	private Bitmap bitmap;

	private final Rect src, dst;
	private final int xBound, yBound, cols, rows;
	private final int rectWidth, rectHeight;
	private final int refreshRate= 550, incrementRate= 50;
	private int x, y, frameX, frameY;
	private boolean isReverse;
	private Direction direction;

	public Sprite(Bitmap bitmap, int cols, int rows, int xBound, int yBound){
		this.bitmap	= bitmap;
		this.xBound	= xBound;
		this.yBound	= yBound;
		this.cols	= cols;
		this.rows	= rows;
		rectWidth	= bitmap.getWidth()/ cols;
		rectHeight	= bitmap.getHeight()/ rows;
		frameX		= 0;
		frameY		= 0;
		src			= new Rect();
		dst			= new Rect();
		x= y= 0;

		direction= Direction.RIGHT;
		isReverse= false;
	}

	private void updateDirection(){
		switch(direction){
			case LEFT:
				if(x- incrementRate< 0)
					direction= Direction.UP;
				else
					x-= incrementRate;

				break;

			case RIGHT:
				if(x+ incrementRate+ rectWidth> xBound)
					direction= Direction.DOWN;
				else
					x+= incrementRate;

				break;

			case UP:
				if(y- incrementRate< 0)
					direction= Direction.RIGHT;
				else
					y-= incrementRate;

				break;

			case DOWN:
				if(y+ incrementRate+ rectHeight> yBound)
					direction= Direction.LEFT;
				else
					y+= incrementRate;

				break;
		}
	}

	private void updateSprite(){
		SystemClock.sleep(refreshRate);
		updateDirection();

		frameX= ++frameX% cols;

		int srcX= frameX* rectWidth;
		int srcY= frameY* rectHeight;

		src.set(srcX, srcY, srcX+ rectWidth, srcY+ rectHeight);
		dst.set(x, y, x+ rectWidth, y+ rectHeight);

		if(frameX== cols- 1)
			frameY= ++frameY% rows;
	}

	public void drawSprite(Canvas canvas){
		updateSprite();
		canvas.drawARGB(255, 255, 255, 255);
		canvas.drawBitmap(bitmap, src, dst, null);
	}

	enum Direction{ LEFT, RIGHT, UP, DOWN }
}
