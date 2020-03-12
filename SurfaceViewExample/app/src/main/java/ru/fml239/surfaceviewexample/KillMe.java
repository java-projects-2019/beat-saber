package ru.fml239.surfaceviewexample;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class KillMe {
	private float x;
	private int screenWidth;
	private float speed;
	private Paint paint;
	public KillMe(int screenWidth) {
		this.screenWidth = screenWidth;
		speed = screenWidth / 5f;
		paint = new Paint();
		paint.setColor(Color.BLUE);
	}
	public void update(float deltaT)
	{
		float x1 = x + speed * deltaT;
		if (x1 < 0)
		{
			x1 = -x1;
			speed = Math.abs(speed);
		}
		if (x1 > screenWidth)
		{
			x1 = screenWidth - (x1 - screenWidth);
			speed = - Math.abs(speed);
		}
		x = x1;
	}
	public void draw(Canvas canvas)
	{
		canvas.drawCircle((float)x, 100, 100, paint);
	}
	public void setPos(float x2) {
		x = x2;
		speed = - speed;
	}
}
