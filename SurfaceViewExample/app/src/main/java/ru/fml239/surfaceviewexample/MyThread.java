package ru.fml239.surfaceviewexample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

public class MyThread extends Thread {
	private SurfaceHolder surfaceHolder;
	private volatile boolean running = false;
	private int screenWidth;
	private int screenHeight;
	private Paint backgroundPaint;

	private KillMe killMe;
	
	public MyThread(SurfaceHolder surfaceHolder, 
			int screenWidth, int screenHeight) {
		this.surfaceHolder = surfaceHolder;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		Canvas canvas = null;
		double lastTime = System.currentTimeMillis() / 1000.0;
		double currentTime;
		float deltaT;
		
		killMe = new KillMe(screenWidth); 

		while (running)
		{
			try
			{
				canvas = surfaceHolder.lockCanvas();
				if (canvas != null)
					synchronized (surfaceHolder) {
						// �������� �� canvas
						currentTime = System.currentTimeMillis() / 1000.0;
						deltaT = (float)(currentTime - lastTime);
						Log.e("Threat", "dT="+deltaT);
						lastTime = currentTime;
						updateAll(deltaT);
						drawAll(canvas);
					}
			}
			finally
			{
				if (canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	private void updateAll(float deltaT)
	{
		killMe.update(deltaT);
	}
	
	private void drawAll(Canvas canvas) 
	{
		canvas.drawRect(0, 0, screenWidth, screenHeight, 
				backgroundPaint);
		killMe.draw(canvas);
	}

	public void touchAction(float x, float y) {
		//touchX = x;
		//touchY = y;
		killMe.setPos(x);
	}
}
