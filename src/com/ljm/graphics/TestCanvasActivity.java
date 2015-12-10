package com.ljm.graphics; 

import android.app.Activity;
import android.os.Bundle;

/** 
 * @author 刘泾铭
 * @version 创建时间：2015-12-10 上午9:57:22 
 * 测试canvas级别函数
 */
public class TestCanvasActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TestCanvasClip(this));
    }
	
}
