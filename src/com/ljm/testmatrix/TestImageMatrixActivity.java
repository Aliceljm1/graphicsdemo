package com.ljm.testmatrix; 

import android.app.Activity;
import android.os.Bundle;

/** 
 * @author 刘泾铭
 * @version 创建时间：2015-11-24 下午3:37:48 
 * 图片的放大 缩小 移动 带有四个角和中心旋转的控制按钮
 */
public class TestImageMatrixActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		CommonImgEffectView view = new CommonImgEffectView(TestImageMatrixActivity.this);
		setContentView(view);
	}
	
}
