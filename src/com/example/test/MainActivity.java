package com.example.test;

import com.cg.memtest.R;
import com.jnitest.memutils.memcheck;
import com.self.debug.Logger;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private final static String VIDEO_PATH = "/storage/sdcard0/test.avi";
	private final static int STOP_PLAYER = 0;
	private final static int START_PLAYER = 1;
	private final static int RESTART_PLAYER = 2;
	private TextView resultTv;
	private EditText etNum;
	private Button startBtn;
	private Button stopBtn;
	private VideoView vplayer;
	private int result = 0;
	private int thread = 0;
	private Thread tdMemcheck;
	private String ret = "OK";

	public void initview() {
		resultTv = (TextView) findViewById(R.id.tips);
		resultTv.setText("FAIL");
		resultTv.setTextSize(120);
		resultTv.setTextColor(android.graphics.Color.RED);
		resultTv.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLUE);
		resultTv.setVisibility(View.VISIBLE);
		etNum = (EditText) findViewById(R.id.num);
		startBtn = (Button) findViewById(R.id.startBtn);
		startBtn.setText("START");
		startBtn.setTextColor(android.graphics.Color.GREEN);
		startBtn.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLACK);
		startBtn.setVisibility(View.VISIBLE);
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "click startBtn");
				doTask(START_PLAYER);
			}
		});
		stopBtn = (Button) findViewById(R.id.stopBtn);
		stopBtn.setText("STOP");
		stopBtn.setTextColor(android.graphics.Color.GREEN);
		stopBtn.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLACK);
		stopBtn.setVisibility(View.INVISIBLE);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "click stopBtn");
				doTask(STOP_PLAYER);
			}

		});
		vplayer = (VideoView) findViewById(R.id.player);
		vplayer.setKeepScreenOn(true);
		vplayer.setVisibility(View.VISIBLE);
		vplayer.setMinimumHeight(750);
		vplayer.setMinimumWidth(450);
		vplayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				doTask(RESTART_PLAYER);
			}

		});
	}

	public void doTask(int sw) {
		String msg = "";
		switch (sw) {
		case 1:
			msg = "startVideo";
			startVideo();
			break;
		case 0:
			msg = "stopVideo";
			stopVideo();
			break;
		case 2:
			msg = "restartVideo";
			restartVideo();
			break;
		default:
			msg = "unknow";
			break;
		}
		Logger.d(TAG, msg);
	}

	private void startVideo() {
		vplayer.setVideoPath(VIDEO_PATH);
		vplayer.start();
		startBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.VISIBLE);
		resultTv.setVisibility(View.INVISIBLE);
		thread = Integer.parseInt(etNum.getText().toString());
		etNum.setEnabled(false);
		for (int i = 0; i < thread; i++) {
			tdMemcheck = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					int no = Integer.parseInt(Thread.currentThread().getName());
					ret = new memcheck().doTask(no);
					Logger.d(TAG, ret);
				}
			});
			tdMemcheck.setName("" + i);
			tdMemcheck.start();
		}
	}

	private void stopVideo() {
		vplayer.pause();
		startBtn.setVisibility(View.VISIBLE);
		stopBtn.setVisibility(View.GONE);
		if (!"running ok".contains(ret)) {
			resultTv.setVisibility(View.VISIBLE);
		}
		etNum.setEnabled(true);
	}

	private void restartVideo() {
		vplayer.setVideoPath(VIDEO_PATH);
		vplayer.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Window win = getWindow();
		// win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate.");
		initview();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		doTask(START_PLAYER);
	}
}
