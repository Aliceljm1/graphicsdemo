package com.ljm.gesture.easysample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Special case of an ImageView that reacts to touch feedback for things like
 * scale and translation. 最简单的缩放Matrix 缩放，图片左上角不动
 * 可以在onsizeChange 中只显示1/4 图片或者中部居中图片
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

	private Paint mPaint;

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Set the scale type to MATRIX so that the scaling works.
		setScaleType(ScaleType.MATRIX);

		// Add a scale GestureDetector, with this as the listener.
		mScaleDetector = new ScaleGestureDetector(context, this);
		mPaint = new Paint();// add by ljm
		mPaint.setAntiAlias(true);
		mPaint.setDither(false);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		// mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);
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
	 * 普通缩放，x,y缩放比例一直, 需要实现中心点缩放
	 * */
	public void scaleNormal(ScaleGestureDetector detector) {
//		mScaleValue *= detector.getScaleFactor();
		mScaleValue=detector.getScaleFactor();
		// Set the image matrix scale
		Matrix m = new Matrix(getImageMatrix());
		log("scaleing scale="+mScaleValue);
		//m.setScale(mScaleValue, mScaleValue, 0, 0);// 会从左上角开始缩放
//		float[] fa={9};
//		m.getValues(fa);//fa
		
		m.setScale(mScaleValue*minScale, mScaleValue*minScale);// 中心点缩放
		
		//m.postTranslate(100, 100);
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
		gotoCenterWithTranslate();
		//show1_4View();
	}

	
	
	/***
	 * 将image移动到当前图片的一半中，也就是图片的中心点，所以只显示1/4
	 * scrollto 表示偏移到原始坐标的量，都为正值表示左上角偏移。
	 * */
	private void show1_4View() {
		scrollTo(mImageWidth/2, mImageHeight/2);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);// 如果改变顺序会产生覆盖
		testMethod(canvas);
	}
	
	/**
	 * 画出测试正方形
	 * */
	private void testMethod(Canvas canvas) {
		ImageView imageView = (ImageView) TouchImageView.this;
		Matrix s=imageView.getImageMatrix();
		Log.i("testlog", "image matrix="+s.toShortString());// 这里100*100的图片高度适配 矩阵是：[7.52, 0.0, 264.0][0.0, 7.52, 0.0][0.0, 0.0, 1.0] 符合计算
		int len=200;
		int px=0;
		int py=0;
		Rect c=new Rect(px, py, px+len, py+len);
		canvas.drawLine(px, py, px+len,py+ len, mPaint);
		canvas.drawLine(px+len, py, px,py+ len, mPaint);
		canvas.drawRect(c, mPaint);
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}
}
