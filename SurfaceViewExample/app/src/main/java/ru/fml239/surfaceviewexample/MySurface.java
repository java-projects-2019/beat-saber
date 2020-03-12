package ru.fml239.surfaceviewexample;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurface extends SurfaceView implements
						SurfaceHolder.Callback
{
	private MyThread myThread;
	
	public MySurface(Context context) {
		super(context);
		getHolder().addCallback(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			myThread.touchAction(event.getX(),event.getY());
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		myThread = new MyThread(holder,width,height);
		myThread.setRunning(true);
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		myThread.setRunning(false);
		while (retry)
		{
			try 
			{
				myThread.join();
				retry = false;
			} 
			catch (InterruptedException e) 
			{
			}
		}
	}

}
