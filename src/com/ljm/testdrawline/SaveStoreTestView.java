package com.ljm.testdrawline; 


import android.app.Activity;   
import android.content.Context;   
import android.graphics.Canvas;   
import android.graphics.Color;   
import android.graphics.Paint;   
import android.os.Bundle;   
import android.view.SurfaceHolder;   
import android.view.SurfaceView; 
/** 
 * @author 刘泾铭
 * @version 创建时间：2015-12-2 下午8:40:20 
 * save 和 restore的说明， 先绘制蓝色矩形，再旋转45度绘制红色矩形，两个矩形各自保持自己的角度
 */
public class SaveStoreTestView extends SurfaceView implements SurfaceHolder.Callback{   

        private SurfaceHolder mHolder;   
        private Canvas canvas;   
        public SaveStoreTestView(Context context) {   
            super(context);   
            mHolder = getHolder();   
            mHolder.addCallback(this);   
        }   
            
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width,   
                int height) {   
                
        }   
   
        @Override  
        public void surfaceCreated(SurfaceHolder holder) {   
            canvas = mHolder.lockCanvas();   
            Paint mPaint = new Paint();   
            mPaint.setColor(Color.BLUE);   
            canvas.drawRect(100, 200, 200, 300, mPaint);   
                
            canvas.save();// 之前的绘制效果保存住，   
            canvas.rotate(45);   
            mPaint.setColor(Color.RED);   
            canvas.drawRect(150, 10, 200, 60, mPaint);   
            canvas.restore();// 生效，   
            mHolder.unlockCanvasAndPost(canvas);   
        }   
   
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {   
            // TODO Auto-generated method stub   
                
        }   
            
    }   
