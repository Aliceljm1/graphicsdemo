package com.ljm.utils; 

import android.util.Log;

/** 
 * @author 刘泾铭
 * @version 创建时间：2015-11-26 下午4:34:09 
 * 类说明 
 */
public class Logger {
	
	private static final String TAG="ljminfo";

	public static void log(String info)
	{
		Log.i(TAG, info);
	}
	
}
