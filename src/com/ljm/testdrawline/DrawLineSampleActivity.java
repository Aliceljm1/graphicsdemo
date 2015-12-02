package com.ljm.testdrawline;

import android.app.Activity;
import android.os.Bundle;

/***
 * 说明简单的画线处理 和save restore方法
 * */
public class DrawLineSampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(new DrawLineView(this));
        setContentView(new SaveStoreTestView(this));
    }
}