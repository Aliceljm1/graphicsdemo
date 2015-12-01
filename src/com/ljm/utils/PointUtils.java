package com.ljm.utils; 

import android.graphics.Point;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;

/** 
 * @author 刘泾铭
 * @version 创建时间：2015-11-26 下午4:16:37 
 * 类说明 
 */
public class PointUtils {

	public static Point create(int x,int y)
	{
		Point p=new Point();
		p.set(x, y);
		return p;
	}
	
	public static Point create(float x,float y)
	{
		return create((int)x,(int)y);
	}
	
	/**
	 * 计算两指头间距
	 * */
	public static float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 计算两指 中点坐标
	 * */
	public void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	
}
