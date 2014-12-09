package com.example.robot;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import java.io.IOException;

import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;

import android.app.AlertDialog;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity{

	private RelativeLayout layout;
	private FaceDetectView faceDetectView;
	private CameraPreview mPreview;
	private FaceActivity robotFaceView;

	private int height,width;
	private ImageButton btnTalk;

	/**  
	 * 按下这个按钮进行的颜色过滤  
	 */  
	public final static float[] BT_SELECTED = new float[] {    
		2, 0, 0, 0, 2,    
		0, 2, 0, 0, 2,    
		0, 0, 2, 0, 2,    
		0, 0, 0, 1, 0 };   

	/**  
	 * 按钮恢复原状的颜色过滤  
	 */  
	public final static float[] BT_NOT_SELECTED = new float[] {    
		1, 0, 0, 0, 0,    
		0, 1, 0, 0, 0,    
		0, 0, 1, 0, 0,    
		0, 0, 0, 1, 0 };

	/**  
	 * 按钮触碰按下效果  
	 */ 
	private OnTouchListener buttonOnTouchListener = null;

	public void onIatCompleted(){
		btnTalk.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
	}

	public void onTtsCompleted(){
		btnTalk.setEnabled(true);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		buttonOnTouchListener = new OnTouchListener() {
			private ChatActivity chatAct = new ChatActivity(MainActivity.this);
			@Override  
			public boolean onTouch(View v, MotionEvent event) {   
				if(event.getAction() == MotionEvent.ACTION_UP){ 
					v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
					v.setEnabled(false);
					chatAct.startIatListening();
				}   
				return false;   
			}   
		};

		Display display = getWindowManager().getDefaultDisplay(); 
		height = display.getHeight();
		width = display.getWidth();

		layout = new RelativeLayout(this);
//			faceDetectView = new FaceDetectView(this);
//		mPreview = new CameraPreview(this, faceDetectView);
		robotFaceView = new FaceActivity(this);
//			btnTalk = new ImageButton(this);
//			btnTalk.setBackgroundResource(R.drawable.talk);
//			btnTalk.setOnTouchListener(buttonOnTouchListener);

//		RelativeLayout.LayoutParams params_camera = new RelativeLayout.LayoutParams(width/4, height/3);
//		RelativeLayout.LayoutParams params_btntalk = new RelativeLayout.LayoutParams(width/8, height/6);
//		params_camera.setMargins(0,0,width/4,height/3);
//		params_btntalk.setMargins(width*4/9, height*2/3, width*4/9+width/8, height*2/3+height/6);

//		layout.addView(mPreview,params_camera);
		layout.addView(robotFaceView);
//		layout.addView(faceDetectView,params_camera);
//		layout.addView(btnTalk, params_btntalk);

		//layout.addView(btnTalk);
		setContentView(layout);

		//by guo 设置全屏+横屏
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN );
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		robotFaceView.OnTouch(event);
		robotFaceView.postInvalidate();
		return super.onTouchEvent(event);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//menu.add(1,Menu.FIRST,Menu.FIRST,"菜单项1");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		//响应每个菜单项(通过菜单项的ID)
		case R.string.camera_visible:
			boolean isChecked = item.isChecked();
			if(isChecked == true){
				faceDetectView.setVisibility(View.INVISIBLE);
				mPreview.setVisibility(View.INVISIBLE);
			}else{
				faceDetectView.setVisibility(View.VISIBLE);
				mPreview.setVisibility(View.VISIBLE);
			}
			item.setChecked(!isChecked);
			break;
		case R.string.about_us:
			Toast.makeText(this, "SCUT SMART ROBOT", Toast.LENGTH_LONG).show(); 
			break;
		default:
			//对没有处理的事件，交给父类来处理
			return super.onOptionsItemSelected(item);
		}
		//返回true表示处理完菜单项的事件，不需要将该事件继续传播下去了
		return true;
	}

}
