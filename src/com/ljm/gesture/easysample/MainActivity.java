package com.ljm.gesture.easysample;

import com.ljm.graphics.R;
import android.app.Activity;
import android.os.Bundle;

/**
 * Created by rharter on 9/27/14.
 * 最简单的图片缩放 TouchImageView 实现了接口 OnScaleGestureListener
 */
public class MainActivity extends Activity {

    TouchImageView mImageView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (TouchImageView) findViewById(R.id.image_view);
      //  mImageView.setImageResource(R.drawable.android);
        mImageView.setImageResource(R.drawable.android100_100);
        
    }
}
