package com.ljm.utils; 

import android.graphics.Point;

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
	
}
