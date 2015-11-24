package com.examples.customtouch.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by Dave Smith
 * Double Encore, Inc.
 * Date: 9/24/12
 * RotateZoomImageView
 * 中部居中显示一个图片，可以放大和旋转，旋转的中线点图片中心 不能移动图片
 * 
 */
public class RotateZoomImageView extends ImageView {
    private ScaleGestureDetector mScaleDetector;
    private Matrix mImageMatrix;
    /* Last Rotation Angle */
    private int mLastAngle = 0;
    /* Pivot Point for Transforms */
    private int mPivotX, mPivotY;

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

    /*
     * Use onSizeChanged() to calculate values based on the view's size.
     * The view has no size during init(), so we must wait for this
     * callback.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            //Shift the image to the center of the view
        	int imageWidth=getDrawable().getIntrinsicWidth();
        	int imageHeigth=getDrawable().getIntrinsicHeight();
            int translateX = (w - imageWidth) / 2;
            int translateY = (h - imageHeigth) / 2;
            
            mImageMatrix.setTranslate(translateX, translateY);// 图片居中显示，平移数据就是平移空白的地方
//            mImageMatrix.setTranslate(0, 0);// 在左上角0,0点显示图片
            setImageMatrix(mImageMatrix);
            //Get the center point for future scale and rotate transforms
            mPivotX = w / 2;
            mPivotY = h / 2;
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
            //Pass that factor to a scale for the image
            mImageMatrix.postScale(scaleFactor, scaleFactor, mPivotX, mPivotY);
            setImageMatrix(mImageMatrix);

            return true;
        }
    };

    private void log(String info)
    {
    	Log.i("zoominfo", info);
    }
    
    
     /** 
      * Operate on two-finger events to rotate the image.
     * This method calculates the change in angle between the
     * pointers and rotates the image accordingly.  As the user
     * rotates their fingers, the image will follow.
     * 
     * postRotate(旋转角度，中心点坐标)
     **/
    private boolean doRotationEvent(MotionEvent event) {
    	log("双手操作");
        //Calculate the angle between the two fingers
    	log("x0="+event.getX(0)+",y0="+event.getY(0));
    	log("x1="+event.getX(1)+",y1="+event.getY(1));
    	
    	
        float deltaX = event.getX(0) - event.getX(1);
        float deltaY = event.getY(0) - event.getY(1);// 计算出旋转产生的三角形，tan(a)=Y/X, 反tan,
        double radians = Math.atan(deltaY / deltaX);// 求解旋转弧度
        //Convert to degrees
        int degrees = (int)(radians * 180 / Math.PI);// 弧度转角度
        log("degrees="+degrees);
        /*
         * Must use getActionMasked() for switching to pick up pointer events.
         * These events have the pointer index encoded in them so the return
         * from getAction() won't match the exact action constant.
         */
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                //Mark the initial angle
                mLastAngle = degrees;
                break;
            case MotionEvent.ACTION_MOVE:
                // ATAN returns a converted value between -90deg and +90deg
                // which creates a point when two fingers are vertical where the
                // angle flips sign.  We handle this case by rotating a small amount
                // (5 degrees) in the direction we were traveling
                if ((degrees - mLastAngle) > 45) {
                    //Going CCW across the boundary
                    mImageMatrix.postRotate(-5, mPivotX, mPivotY);
                } else if ((degrees - mLastAngle) < -45) {
                    //Going CW across the boundary
                    mImageMatrix.postRotate(5, mPivotX, mPivotY);
                } else {
                	log("旋转");
                    //Normal rotation, rotate the difference
                    mImageMatrix.postRotate(degrees - mLastAngle, mPivotX, mPivotY);
                }
                //Post the rotation to the image
                setImageMatrix(mImageMatrix);
                //Save the current angle
                mLastAngle = degrees;
                break;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // We don't care about this event directly, but we declare
            // interest so we can get later multi-touch events.
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
                //Ignore this event
                return super.onTouchEvent(event);
        }
    }
}
