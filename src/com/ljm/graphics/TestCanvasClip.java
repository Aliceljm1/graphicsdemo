package com.ljm.graphics; 

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

/** 
 * @author 刘泾铭
 * @version 创建时间：2015-12-10 上午9:58:03 
 * 类说明 
 * http://blog.csdn.net/harvic880925/article/details/39080931
 *1.平移 translate 会改变坐标原点的位置
 *2.剪裁会截取指定矩形作为绘图区域，坐标原点也随之改变，
 *save 就是保存当前画布的状态，包括大小形态，save的次数>= restore,
 *restore 会弹出最近的一次堆栈。
 */
public class TestCanvasClip  extends View{

	public TestCanvasClip(Context context) {
		super(context);
		// TODO 自动生成的构造函数存根
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO 自动生成的方法存根
		super.onDraw(canvas);
		   
	    canvas.drawColor(Color.RED);  
	    //保存的画布大小为全屏幕大小  
	    canvas.save();  
	      
	    canvas.clipRect(new Rect(100, 100, 800, 800));  
	    canvas.drawColor(Color.GREEN);  
	    //保存画布大小为Rect(100, 100, 800, 800)  
	    int saveCount=canvas.getSaveCount();
	    canvas.save();  
	      
	    canvas.clipRect(new Rect(200, 200, 700, 700));  
	    canvas.drawColor(Color.BLUE);  
	    //保存画布大小为Rect(200, 200, 700, 700)  
	    canvas.save();  
	      
	    canvas.clipRect(new Rect(300, 300, 600, 600));  
	    canvas.drawColor(Color.BLACK);  
	    //保存画布大小为Rect(300, 300, 600, 600)  
	    canvas.save();  
	      
	    canvas.clipRect(new Rect(400, 400, 500, 500));  
	    canvas.drawColor(Color.WHITE);  
	      
	    //将栈顶的画布状态取出来，作为当前画布，并画成黄色背景  
	    //canvas.restore();
	    
	    
	  //连续出栈三次，将最后一次出栈的Canvas状态作为当前画布，并画成黄色背景  也就是 100,800
	    //canvas.restore();
	    //canvas.restore();
	    //canvas.restore();
	    canvas.restoreToCount(saveCount); //精确还原 效果同上
 
	    canvas.drawColor(Color.YELLOW);  
	}

}
