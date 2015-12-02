package com.ljm.testmatrix;

import android.app.Activity;
import android.os.Bundle;

/**
 * 测试Maxtrix的数学特性
 * http://www.oschina.net/question/157182_40618
 * 
 * */
public class TestMatrixMathActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new RectView(this));
	}

}
