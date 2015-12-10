package com.ljm.testmultitouch;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.ljm.graphics.R;
import com.ljm.utils.Logger;
import com.ljm.utils.PointUtils;

/**
 * 记录点的个数，同时画出点，可能有偏移，注意代码Matrix，
 * */
public class TestMultiTouchEventActivity extends Activity implements
		OnTouchListener {

	ImageView imgView;
	Bitmap bitmap,mCursor;
	Canvas canvas;
	Paint paint;
	ArrayList<Point> directPoint;
	int mOffectX=-100;
	int mOffectY=-100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_multi_touch_event);
		imgView = (ImageView) findViewById(R.id.imageView);
		Display currentDisplay = getWindowManager().getDefaultDisplay();
		float dw = currentDisplay.getWidth();
		float dh = currentDisplay.getHeight();
		bitmap = Bitmap.createBitmap((int) dw, (int) dh, Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		
		//
		Matrix matrix=new Matrix();
		matrix.preTranslate(mOffectX, mOffectY);// 画布坐标原点平移之后，所有在画布中的作图坐标都是基于和这个原点的，
		canvas.setMatrix(matrix);
		
		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth((float) 10.00);// 设置笔刷大小，自己的屏幕太犀利了
		imgView.setImageBitmap(bitmap);
		imgView.setOnTouchListener(this);
		directPoint = new ArrayList<Point>();
		mCursor=BitmapFactory.decodeResource(getResources(), R.drawable.cursor_mouse);
		
	}

	public boolean onTouch(View v, MotionEvent event) {
		handTouch(event);
		return directPaintPoint(event,true);
	}

	private void handTouch(MotionEvent event) {
		int pointerCount = event.getPointerCount();
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		
		if(pointerCount==2){
			 Logger.log("两指头间距："+PointUtils.spacing(event)+"action="+action);
			}
		Logger.log("触摸点 个数:"+pointerCount);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Logger.log("ACTION_POINTER_DOWN 触发，actionindex= "+event.getActionIndex());
			break;
		}


	}

	/**
	 * 直接写画点
	 * */
	private boolean directPaintPoint(MotionEvent event, boolean isShowPoint) {
		int pointerCount = event.getPointerCount();
		int pointerId = 0;
		int action = (event.getAction() & MotionEvent.ACTION_MASK);// 统一单点和多点
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (pointerCount > 1) {
				pointerId = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>> MotionEvent.ACTION_POINTER_ID_SHIFT;
				show("单个手指写画");
			}
			directPoint.clear();
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX(0);
			float y = event.getY(0);
			directPoint.add(PointUtils.create(x, y));
			if (isShowPoint) {
				canvas.drawPoint((int) x, (int) y, paint);
				
				Rect cursorRect=new Rect();
				cursorRect.left = (int)x-mOffectX; //修补坐标原点带来的偏移
				cursorRect.right = cursorRect.left+mCursor.getWidth();
				cursorRect.top = (int)y-mOffectY;
				cursorRect.bottom = cursorRect.top + mCursor.getHeight();	
				canvas.drawBitmap(mCursor, null, cursorRect, null);
				
				imgView.invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			show("记录点个数：" + directPoint.size());
			break;
		}

		return true;
	}

	private void show(String s) {
		Toast toast = Toast.makeText(getApplicationContext(), s, 1000);
		toast.show();
	}
}
