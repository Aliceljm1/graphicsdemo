package com.examples.customtouch;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.examples.customtouch.widget.RotateZoomImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dave Smith
 * Double Encore, Inc.
 * Date: 9/24/12
 * MultitouchActivity
 */
public class MultitouchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        RotateZoomImageView iv = new RotateZoomImageView(this);

        Bitmap image;
        try {
            //InputStream in = getAssets().open("android_big.jpg");
        	InputStream in = getAssets().open("android_big2.jpg");
            //InputStream in = getAssets().open("android_high.jpg");
            image = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
            image = null;
        }
        iv.setImageBitmap(image);

        setContentView(iv);
    }
}
