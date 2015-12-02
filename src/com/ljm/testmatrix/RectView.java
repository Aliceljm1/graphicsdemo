package com.ljm.testmatrix;

import com.ljm.utils.Logger;
import com.ljm.utils.PointUtils;
import com.ljm.utils.ToastUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * @author 刘泾铭
 * @version 创建时间：2015-12-1 上午11:08:28 类说明
 */
public class RectView extends SurfaceView implements Runnable, Callback {
	private SurfaceHolder mHolder; // 用于控制SurfaceView

	private Canvas mCanvas; // 声明一张画布
	private int mScreenW, mScreenH; // 用于屏幕的宽高
	private Paint mPaint, qPaint; // 声明两只画笔
	private Thread mThread; // 声明一个线程
	private RectF mRect;
	private Context mContext;

	/**
	 * 构造函数，主要对一些对象初始化
	 */
	public RectView(Context context) {

		super(context);
		mContext = context;
		mHolder = this.getHolder(); // 获得SurfaceHolder对象
		mHolder.addCallback(this); // 添加状态监听

		mPaint = new Paint(); // 创建一个画笔对象
		mPaint.setColor(Color.GREEN); // 设置画笔的颜色为白色
		mRect = new RectF(0, 0, 200, 100);

		qPaint = new Paint(); // 创建一个画笔对象
		qPaint.setAntiAlias(true); // 消除锯齿
		qPaint.setStyle(Style.STROKE); // 设置画笔风格为描边
		qPaint.setStrokeWidth(3); // 设置描边的宽度为3
		qPaint.setColor(Color.GREEN); // 设置画笔的颜色为绿色

		setFocusable(true); // 设置焦点

	}

	@Override
	public void run() {
		mDraw(); // 调用自定义的绘图方法
		try {
			Thread.sleep(50); // 让线程休息50毫秒
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自定义的绘图方法
	 */
	public void mDraw() {

		drawRect();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 获得屏幕的宽和高
		mScreenW = this.getWidth();
		mScreenH = this.getHeight();

		mThread = new Thread(this); // 创建线程对象

		mThread.start(); // 启动线程

	}

	/**
	 * 当屏幕被触摸时调用
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		handTouch(event);
		return super.onTouchEvent(event);
	}

	private void handTouch(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		int action = (event.getAction() & MotionEvent.ACTION_MASK);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (event.getX(0)<100) {
				scale(2);
			} else {
				transport(200, 100);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:

			break;
		}

	}

	private void scale(int i) {
		ToastUtil.show(mContext, "scale", 1500);
		Matrix ma = new Matrix();
		Logger.log("变化前：" + ma.toString());
		ma.postScale(i, i);
		ma.mapRect(mRect);
		Logger.log("变化后：" + ma.toString());
		drawRect();
	}

	private void transport(int x, int y) {
		ToastUtil.show(mContext, "tran", 1500);
		Matrix ma = new Matrix();
		Logger.log("变化前：" + ma.toString());
		ma.postTranslate(x, y);
		ma.mapRect(mRect);
		Logger.log("变化后：" + ma.toString());
		drawRect();

	}

	private void drawRect() {
		mCanvas = mHolder.lockCanvas();
		mCanvas.drawColor(Color.WHITE);
		Rect rect = new Rect();
		mRect.round(rect);
		mCanvas.drawRect(rect, mPaint);
		mHolder.unlockCanvasAndPost(mCanvas); // 把画布显示在屏幕上
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自动生成的方法存根

	}

}
