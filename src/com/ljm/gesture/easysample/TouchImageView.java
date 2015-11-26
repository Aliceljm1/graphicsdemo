package com.ljm.gesture.easysample;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Special case of an ImageView that reacts to touch feedback for things like
 * scale and translation. 最简单的缩放Matrix 缩放，图片左上角不动
 */
public class TouchImageView extends ImageView implements
		ScaleGestureDetector.OnScaleGestureListener {

	/** The custom gesture detector we use to track scaling. */
	private ScaleGestureDetector mScaleDetector;

	/** The scale value of our internal image view. */
	private float mScaleValue = 1.0f;

	private int mPivotX, mPivotY;// view中心点
	private int mViewHeight, mViewWidth;
	private int mScrollX = 0;//

	private int mCenterOffsetX;// 没有缩放之前 (view-image)/2 中心点偏移距离，
	private int mCenterOffsetY;

	private int mImageWidth;
	private int mImageHeight;

	private float minScale;// 最小缩放比，也就是高度或者宽度适配比

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Set the scale type to MATRIX so that the scaling works.
		setScaleType(ScaleType.MATRIX);

		// Add a scale GestureDetector, with this as the listener.
		mScaleDetector = new ScaleGestureDetector(context, this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Pass our events to the scale gesture detector first.
		boolean handled = mScaleDetector.onTouchEvent(event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			scrollTo(0, 0);// 恢复坐标,如果image和view的左上角重合 那么没有任何影响
			return true;
		}

		// If the scale gesture detector didn't handle the event,
		// pass it to super.
		if (!handled) {
			handled = super.onTouchEvent(event);
		}

		return handled;
	}

	/*
	 * ScaleGestureDetector callbacks
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		// Get the modified scale value
		scaleNormal(detector);
		return true;
	}

	/**
	 * 普通缩放，x,y缩放比例一直
	 * */
	public void scaleNormal(ScaleGestureDetector detector) {
		mScaleValue *= detector.getScaleFactor();
		// Set the image matrix scale
		Matrix m = new Matrix(getImageMatrix());
		log("scaleing scale="+mScaleValue);
		m.setScale(mScaleValue, mScaleValue, 0, 0);
		setImageMatrix(m);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw || h != oldh) {
			// Shift the image to the center of the view

			mViewHeight = h;
			mViewWidth = w;
			mPivotX = mViewWidth / 2;
			mPivotY = mViewHeight / 2;

			mImageWidth = getDrawable().getIntrinsicWidth();
			mImageHeight = getDrawable().getIntrinsicHeight();
			mCenterOffsetX = (mViewWidth - mImageWidth) / 2;
			mCenterOffsetY = (mViewHeight - mImageHeight) / 2;
			float scale_x = (float) mViewWidth / mImageWidth;
			float scale_y = (float) mViewHeight / mImageHeight;
			minScale = Math.min(scale_x, scale_y);

			log("minScale=" + minScale + ",mImageWidth="
					+ mImageWidth + ",mImageHeight=" + mImageHeight
					+ ",mViewHeight=" + mViewHeight + ",mViewWidth="
					+ mViewWidth);

			changeView();
		}
	}

	
	private void gotoCenterWithTranslate() {
		Matrix mImageMatrix = new Matrix(getImageMatrix());
		mImageMatrix.postTranslate(mCenterOffsetX, mCenterOffsetY);
		// 图片居中显示，图片的中心点和矩形的中心点重合，
		// mImageMatrix.setTranslate(0, 0);// 在左上角0,0点显示图片
		// Get the center point for future scale and rotate transforms
		mImageMatrix.postScale(minScale, minScale, mPivotX, mPivotY);// 高度适配或者宽度适配，
		setImageMatrix(mImageMatrix);
	}
	
	private void log(String info) {
		Log.i("ljminfo", info);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		log("onScrollChanged"+",l="+l+",t="+t);
		super.onScrollChanged(l, t, oldl, oldt);
		
	}
	
	private void changeView() {
		//gotoCenterWithTranslate();
		show1_4View();
	}

	
	
	/***
	 * 将image移动到当前图片的一半中，也就是图片的中心点，所以只显示1/4
	 * */
	private void show1_4View() {
		scrollTo(mImageWidth/2, mImageHeight/2);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// Return true here to tell the ScaleGestureDetector we
		// are in a scale and want to continue tracking.
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// We don't care about end events, but you could handle this if
		// you wanted to write finished values or interact with the user
		// when they are finished.
	}
}
