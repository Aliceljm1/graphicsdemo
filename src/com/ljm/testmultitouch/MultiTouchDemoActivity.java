/*
 * Copyright (C) 2013 Christopher Boyd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.ljm.testmultitouch;

import java.io.IOException;
import java.io.InputStream;

import com.ljm.utils.Geometry;
import com.ljm.utils.X11Color;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.ljm.graphics.R;;

/**
 * MultiGestureDetector Demo
 * 粉色的圆球，可以缩放，移动
 * 双击移动到中心点，初始化的时候也是移动到中心点, 最大放大倍数5被
 * @author Christopher Boyd
 */
public class MultiTouchDemoActivity extends Activity implements OnTouchListener {
	
	private Matrix mMatrix = new Matrix();
    private float mScaleX 			= 1.0f;
    private float mLastScaleX 		= mScaleX;
    private float mScaleY 			= 1.0f;
    private float mRotDeg 	= 0.f;
    private float mFocusX 			= 0.f;// 显示点，一开始是中心点
    private float mFocusY 			= 0.f;  //显示点
    private int   mAlpha 			= 255;
    private int   mLastAlpha 		= mAlpha;
    private int OvalHeight 	= 300;
    private  int OvalWidth 	= OvalHeight;
    
    private PaintDrawable mDrawable;
    
    private MultiGestureDetector mMultiDetector;
    ImageView mView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mView= (ImageView) findViewById(R.id.imageView);
		mView.setOnTouchListener(this);
		
		// Set the view's initial scale Matrix
		gotoCenter(mMatrix);
		mMatrix.postScale(mScaleX, mScaleY);
		mView.setImageMatrix(mMatrix);

		// Draw an oval with a gradient.
		
		/**
		 * 粉红色球
		 * */
		ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
		    @Override
		    public Shader resize(int width, int height) {
		        LinearGradient lg = new LinearGradient(0, 0, width, height,
		            new int[]{X11Color.Pink, X11Color.HotPink, X11Color.DeepPink, X11Color.DeepPink, X11Color.Crimson},
		            new float[]{0, 0.3f, 0.6f, 0.7f, 1}, Shader.TileMode.REPEAT);
		        return lg;
		    }
		};
		
		
		
		mDrawable = new PaintDrawable();
		mDrawable.setShape(new OvalShape());
		mDrawable.setShaderFactory(sf);
	    mDrawable.setIntrinsicHeight(OvalHeight);
	    mDrawable.setIntrinsicWidth(OvalWidth);
	    //view.setImageDrawable(mDrawable);
	    
        Bitmap image;
        try {
            InputStream in = getAssets().open("android_big.jpg");
            image = BitmapFactory.decodeStream(in);
            OvalHeight=image.getHeight();
            OvalWidth=image.getWidth();
            // 需要中部居中，同时宽高适配
            in.close();
        } catch (IOException e) {
            image = null;
        }
        mView.setImageBitmap(image);
        
	   
	    
		mMultiDetector = new MultiGestureDetector(getApplicationContext(), new MultiListener());
	}
	
	public void gotoCenter(Matrix m)
	{
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		float displayCenterX = (displaymetrics.widthPixels-OvalWidth)/2;
		float displayCenterY = (displaymetrics.heightPixels-OvalHeight)/2;
        mFocusX = displayCenterX;
        mFocusY = displayCenterY;
		// Reset deformation:
		mScaleX = mScaleY;
		mView.scrollTo((int)-mFocusX, (int)-mFocusY);
//		m.postTranslate(mFocusX, mFocusY);
	}
	
	@SuppressLint("NewApi")
	public boolean onTouch(View v, MotionEvent event) {
        mMultiDetector.onTouchEvent(event);// 处理好坐标，准备移动和缩放

        float scaledImageCenterX = (OvalWidth*mScaleX)/2;
        float scaledImageCenterY = (OvalHeight*mScaleY)/2;
        
        mMatrix.reset();
        mMatrix.postScale(mScaleX, mScaleY);
        mMatrix.postRotate(mRotDeg,  scaledImageCenterX, scaledImageCenterY);
        mMatrix.postTranslate(mFocusX, mFocusY);
        
		ImageView view = (ImageView) v;
		view.setImageMatrix(mMatrix);
		view.setImageAlpha(mAlpha);
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			// End scrolling if the user lifts fingers:
            mMultiDetector.resetScrollMode();
            // Store values in case we need them:
            mLastAlpha  = mAlpha;
            mLastScaleX = mScaleX;
        }
		
		return true; // indicate event was handled
	}
	
	private class MultiListener extends MultiGestureDetector.SimpleMultiGestureListener {
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// Center the ball on the display:
			gotoCenter(mMatrix);
			return true;
		}
		
		/**
		 * e1 为起点，e2是终点，distanceX 为负值表示想x轴正方向也就是右边移动，同理distanceY
		 * */
		@Override
		public boolean onMove(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY, int numPointers) {
			switch (numPointers) {
			case 1:
				mFocusX -= distanceX;
				mFocusY -= distanceY;
				return true;
			case 2:
				// Do different things, depending on whether the fingers are moving in X or Y.
				if (mMultiDetector.isTravelY()) {
					// Reset mScaleX to last value:
					mScaleX = mLastScaleX;
					// Increase the oval's transparency if the user moves two fingers upwards:
					mAlpha -= distanceY;
					if (mAlpha > 255)
						mAlpha = 255;
					else if (mAlpha < 0)
						mAlpha = 0;
				} else {
					// Reset mAlpha to last value:
					mAlpha = mLastAlpha;
					// Stretch or squeeze the oval's width depending on X motion:
					mScaleX -= distanceX / 100.0f;
					// Prevent the oval from being too deformed:
					mScaleX = Math.max(mScaleY / 3.0f, Math.min(mScaleX, Math.min(3.0f * mScaleY, 5.0f))); 
				}
				return true;
			default: return false;
			}
		}
		@Override
		public boolean onScale(MotionEvent e1, MotionEvent e2, double scaleFactor, double angle) {
			mScaleX *= scaleFactor;
			mScaleY *= scaleFactor;
			// Prevent the oval from being too big or too small:
			mScaleX = Math.max(0.1f, Math.min(mScaleX, 5.0f)); 
			mScaleY = Math.max(0.1f, Math.min(mScaleY, 5.0f)); 
			
			mRotDeg += Geometry.rad2deg(angle);
			return true;
		}
	}
}