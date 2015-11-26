package com.examples.customtouch.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by Dave Smith Double Encore, Inc. Date: 9/24/12 RotateZoomImageView
 * 中部居中显示一个图片，可以放大和旋转，旋转的中线点图片中心 点击图片 向左边移动20像素
 * 两个指头 改变角度，三个指头缩放
 * 
 */
public class RotateZoomImageView extends ImageView {
	private ScaleGestureDetector mScaleDetector;
	private Matrix mImageMatrix;
	/* Last Rotation Angle */
	private int mLastAngle = 0;
	/* Pivot Point for Transforms */
	private int mPivotX, mPivotY;// view中心点 ，屏幕的中心点
	private int mViewHeight, mViewWidth;
	private int mScrollX = 0;//

	private int mCenterOffsetX;// 没有缩放之前 (view-image)/2 中心点偏移距离，平移此距离，中心点就可以重合
	private int mCenterOffsetY;

	private int mImageWidth;
	private int mImageHeight;
	
	private float minScale;//最小缩放比，也就是高度或者宽度适配比
	
	public RotateZoomImageView(Context context) {
		super(context);
		init(context);
	}

	public RotateZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RotateZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mScaleDetector = new ScaleGestureDetector(context, mScaleListener);

		setScaleType(ScaleType.MATRIX);
		mImageMatrix = new Matrix();
	}

	/**
	 * 重点是先偏移保证中心点重合 再缩放
	 * 缩放地点必须是中心点
	 * 连续使用matrix 需要使用postXXX 操作，保证矩阵变化可以累加，
	 * 第一次平移之后图片内部坐标系发送了变化如果第二次使用setScale 那么图片会向右下角移动
	 * */
	private void gotoCenterWithTranslate() {
		mImageMatrix.postTranslate(mCenterOffsetX, mCenterOffsetY);
		// 图片居中显示，图片的中心点和矩形的中心点重合，
		// mImageMatrix.setTranslate(0, 0);// 在左上角0,0点显示图片
		// Get the center point for future scale and rotate transforms
		mImageMatrix.postScale(minScale, minScale, mPivotX, mPivotY);// 高度适配或者宽度适配，
		setImageMatrix(mImageMatrix);
	}
	
	/***
	 * 通过是scroll 移动到中心
	 * 使用scrollTo 的时候需要配合 scale参数 保证坐标系一致，scrollto 的顺序在setScale前后都不影响，
	 * */
	private void gotoCenterWithScroll(float scale)
	{
		scrollTo((int)(-mCenterOffsetX*scale),(int)(-mCenterOffsetY*scale));
		mImageMatrix.setScale(scale, scale,mPivotX,mPivotY);// 高度适配或者宽度适配，
		setImageMatrix(mImageMatrix);
	}
	
	/**
	 * 从00 点缩放，此时中心点偏移 失效，因为view和image都是从00点开始绘图的，两个中心点不重合
	 * 需要计算缩放之后 两个视图的中心点偏移，
	 * 最终目的是中心点重合并且高度或宽度适配
	 * */
	private void gotoCenterWithScrollScale00(float scale)
	{
		mImageMatrix.postScale(scale, scale);// 高度适配或者宽度适配，
		setImageMatrix(mImageMatrix);
		int sx=(int)((mViewWidth-mImageWidth*scale)/2);// （view-缩放后大小）/2
		int sy=(int)((mViewHeight-mImageHeight*scale)/2);
		// sy,sx 有一个值必为零
		scrollTo(-sx,-sy);//-数，右下角移动, 因为00点缩放之后image和view的左上角一致，所以直接scrollTo,
	}
	
	/**
	 * 先平移，中心点重合，
	 * 再缩放，
	 * 再修复误差 左上角重合
	 * 再高度或者宽度适配
	 * */
	private void spe(float scale)
	{
		resetMatrix();// image和view的中心点此时重合
		mImageMatrix.postScale(scale, scale);//0,0点缩放，此时还有误差，图片在右下角
		setImageMatrix(mImageMatrix);
		
		scrollTo((int)(mCenterOffsetX*(scale)), (int)(mCenterOffsetY*(scale)));//左上角重合完成
		
		// 计算 中心点重合的偏移量
		int sx=(int)((mViewWidth-mImageWidth*scale)/2);// （view-缩放后大小）/2
		int sy=(int)((mViewHeight-mImageHeight*scale)/2);
		scrollBy(-sx,-sy);//因为上面有一个scrollTo 所有这个scroll必须是ScrollBy因为上一个不能立即生效 ,可以合并两个计算值，
		
//		int all_x=(int)(mCenterOffsetX*scale-(mViewWidth-mImageWidth*scale)/2);
//		int all_y=(int)(mCenterOffsetY*scale-(mViewHeight-mImageHeight*scale)/2);
//		scrollTo(all_x,all_y);
		
	}
	
	
	/**
	 * 重置矩阵，中心点重合
	 * */
	private void resetMatrix()
	{
		mImageMatrix.reset();
		log("RestMatrix PreTranslate X="+mCenterOffsetX+",canvasYOffset="+mCenterOffsetY);
		mImageMatrix.preTranslate(mCenterOffsetX, mCenterOffsetY);
	}
	
	/**
	 * 仅仅是高度适配 或者宽度适配，适配完成之后，底部或者右边留白
	 * */
	private void justAdapt(float scale)
	{
		mImageMatrix.setScale(scale, scale);// 高度适配或者宽度适配，
		setImageMatrix(mImageMatrix);
	}
	
	
	@Override
	public void scrollTo(int x, int y) {
		// TODO 自动生成的方法存根
		super.scrollTo(x, y);
		Log.i("ljminfo", "scrollTo x="+x+",y="+y);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO 自动生成的方法存根
		super.onScrollChanged(l, t, oldl, oldt);
		Log.i("ljminfo", "onScrollChanged l="+l+",t="+t+",oldl="+oldl+",oldt="+oldt);
	}
	
	/*
	 * Use onSizeChanged() to calculate values based on the view's size. The
	 * view has no size during init(), so we must wait for this callback.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw || h != oldh) {
			// Shift the image to the center of the view

			mViewHeight = h;
			mViewWidth =w ;
			mPivotX =mViewWidth  / 2;
			mPivotY = mViewHeight / 2;
			
			mImageWidth = getDrawable().getIntrinsicWidth();
			mImageHeight = getDrawable().getIntrinsicHeight();
			mCenterOffsetX = (mViewWidth - mImageWidth) / 2;
			mCenterOffsetY = (mViewHeight - mImageHeight) / 2;
			float scale_x = (float) mViewWidth / mImageWidth;
			float scale_y = (float) mViewHeight / mImageHeight;
			 minScale = Math.min(scale_x, scale_y);
			 Log.i("ljminfo", "minScale="+minScale+
					 ",mImageWidth="+mImageWidth+
					 ",mImageHeight="+mImageHeight+
					 ",mViewHeight="+mViewHeight+
					 ",mViewWidth="+mViewWidth);
			 
//			gotoCenterWithTranslate();// 建议使用矩阵变换，思维轻松些，
//			gotoCenterWithScroll(minScale);
//			 justAdapt(minScale);
			 spe(minScale);
//			gotoCenterWithScrollScale00(minScale);
		}
	}

	/**
	 * 默认缩放处理，围绕中心点 postScale(x放大倍数，y放大倍数，中心点坐标) post表示矩阵左乘，可以连续变化
	 * */
	private ScaleGestureDetector.SimpleOnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			// ScaleGestureDetector calculates a scale factor based on whether
			// the fingers are moving apart or together
			float scaleFactor = detector.getScaleFactor();
			// Pass that factor to a scale for the image
			mImageMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
			setImageMatrix(mImageMatrix);

			return true;
		}
	};

	private void log(String info) {
		Log.i("zoominfo", info);
	}

	/**
	 * Operate on two-finger events to rotate the image. This method calculates
	 * the change in angle between the pointers and rotates the image
	 * accordingly. As the user rotates their fingers, the image will follow.
	 * 
	 * postRotate(旋转角度，中心点坐标)
	 **/
	private boolean doRotationEvent(MotionEvent event) {
		log("双手操作");
		// Calculate the angle between the two fingers
		log("x0=" + event.getX(0) + ",y0=" + event.getY(0));
		log("x1=" + event.getX(1) + ",y1=" + event.getY(1));

		float deltaX = event.getX(0) - event.getX(1);
		float deltaY = event.getY(0) - event.getY(1);// 计算出旋转产生的三角形，tan(a)=Y/X,
														// 反tan,
		double radians = Math.atan(deltaY / deltaX);// 求解旋转弧度
		// Convert to degrees
		int degrees = (int) (radians * 180 / Math.PI);// 弧度转角度
		log("degrees=" + degrees);
		/*
		 * Must use getActionMasked() for switching to pick up pointer events.
		 * These events have the pointer index encoded in them so the return
		 * from getAction() won't match the exact action constant.
		 */
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_POINTER_UP:
			// Mark the initial angle
			mLastAngle = degrees;
			break;
		case MotionEvent.ACTION_MOVE:
			// ATAN returns a converted value between -90deg and +90deg
			// which creates a point when two fingers are vertical where the
			// angle flips sign. We handle this case by rotating a small amount
			// (5 degrees) in the direction we were traveling
			if ((degrees - mLastAngle) > 45) {
				// Going CCW across the boundary
				mImageMatrix.postRotate(-5, mPivotX, mPivotY);
			} else if ((degrees - mLastAngle) < -45) {
				// Going CW across the boundary
				mImageMatrix.postRotate(5, mPivotX, mPivotY);
			} else {
				log("旋转");
				// Normal rotation, rotate the difference
				mImageMatrix.postRotate(degrees - mLastAngle, mPivotX, mPivotY);
			}
			// Post the rotation to the image
			setImageMatrix(mImageMatrix);
			// Save the current angle
			mLastAngle = degrees;
			break;
		}

		return true;
	}

	/**
	 * 每次向左移动20像素，
	 * */
	private void scrollLeft() {
		mScrollX += 20;
		if (mScrollX >= mViewWidth)
			mScrollX = 0;
		Log.i("ljminfo", "mScrollX=" + mScrollX);
		scrollTo(mScrollX, 0);// 移动到某个坐标下，mScrollX 为正值则向左移动
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// We don't care about this event directly, but we declare
			// interest so we can get later multi-touch events.
			//scrollLeft();
			return true;
		}

		switch (event.getPointerCount()) {
		case 3:
			// With three fingers down, zoom the image
			// using the ScaleGestureDetector
			return mScaleDetector.onTouchEvent(event);
		case 2:
			// With two fingers down, rotate the image
			// following the fingers
			return doRotationEvent(event);
		default:
			// Ignore this event
			return super.onTouchEvent(event);
		}
	}
}
