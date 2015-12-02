package com.ljm.testdrawline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

class DrawLineView extends View {

	public Canvas canvas;
	public Paint p;
	private Bitmap bitmap;
	float x,y;
	int bgColor;

	public DrawLineView(Context context) {
		super(context);
		bgColor = Color.WHITE;               //设置背景颜色
		canvas=new Canvas();         
		p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true);                //设置抗锯齿，一般设为true
		p.setColor(Color.RED);              //设置线的颜色
		p.setStrokeCap(Paint.Cap.ROUND);     //设置线的类型
		p.setStrokeWidth(8);                //设置线的宽度
		
	}
    
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		super.onSizeChanged(w, h, oldw, oldh);
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);    //设置位图，线就画在位图上面，第一二个参数是位图宽和高         
		canvas.setBitmap(bitmap);       

		Paint rectP=new Paint(p);
		rectP.setColor(Color.GREEN);
		rectP.setStyle(Paint.Style.STROKE);
		canvas.drawRect(0, 0, w, h,rectP);
		canvas.drawLine(0,0,w,h,rectP);
		canvas.drawLine(w,0,0,h,rectP);
	}
	
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.i("mouse", "画线，x="+x+",y="+y);
			canvas.drawLine(x, y, event.getX(), event.getY(), p);
			invalidate();// 必须使用
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {  
			x = event.getX();				
			y = event.getY();
			canvas.drawPoint(x, y, p);//画点
			invalidate();
		}
		x = event.getX();   //记录坐标
		y = event.getY();
		return true;
	}
	
	@Override
	public void onDraw(Canvas c) {			    		
		c.drawBitmap(bitmap, -100, -100, null);// 此时canvas的参考点被平移到-100,100处，而所有的event都是相对于view而言的。也就是屏幕点。	      
		//c.drawBitmap(bitmap, 0, 0, null);
	
	}
 }
