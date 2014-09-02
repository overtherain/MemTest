package com.example.test;

import com.cg.memtest.R;
import com.jnitest.memutils.MEMCHECK;
import com.jnitest.memutils.memcheck;
import com.self.debug.Logger;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
	private final static int RESUME_PLAYER = 2;
	private final static int RESTART_PLAYER = 3;
	private final static int FINISH_PLAYER = 4;
	private final static int CREATE_RUN = 0;
	private final static int RESUME_RUN = 1;

	private TextView resultTv;
	private EditText etNum;
	private Button startBtn;
	private Button stopBtn;
	private VideoView vplayer;
	private int result = 0;
	private int thread = 0;
	private Thread tdMemcheck;
	private String ret = "OK";
	private int newRun = 0;

	public void initview() {
		Logger.d(TAG, "initview.");
		resultTv = (TextView) findViewById(R.id.tips);
		resultTv.setText("WAITING....");
		resultTv.setTextSize(60);
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
				thread = Integer.parseInt(etNum.getText().toString());
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
				Logger.d(TAG, "onCompletion.");
				doTask(RESTART_PLAYER);
			}

		});
	}

	public void doTask(int sw) {
		Logger.d(TAG, "doTask. sw: " + sw);
		String msg = "";
		switch (sw) {
		case START_PLAYER:
			msg = "startVideo";
			startVideo();
			break;
		case RESUME_PLAYER:
			msg = "resumeVideo";
			resumeVideo();
			break;
		case STOP_PLAYER:
			msg = "stopVideo";
			stopVideo(STOP_PLAYER);
			break;
		case RESTART_PLAYER:
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
		Logger.d(TAG, "startVideo.");
		resumeVideo();
		doCheck();
	}

	private void resumeVideo(){
		Logger.d(TAG, "resumeVideo.");
		vplayer.setVideoPath(VIDEO_PATH);
		vplayer.start();
		startBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.VISIBLE);
		resultTv.setVisibility(View.INVISIBLE);
		etNum.setEnabled(false);
	}

	private void doCheck(){
		Logger.d(TAG, "doCheck.");
		Logger.d(TAG, "thread: " + thread);
		for (; thread > 0; thread--) {
			tdMemcheck = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					int no = Integer.parseInt(Thread.currentThread().getName());
					ret = new memcheck().doTask(no);
					result = Integer.parseInt(ret);
					Logger.d(TAG, ret);
					if (Integer.parseInt(ret) != MEMCHECK.RET_OK) {
						stopVideo(FINISH_PLAYER);
					}
				}
			});
			tdMemcheck.setName("" + thread);
			tdMemcheck.start();
		}
	}
	private void stopVideo(int type) {
		Logger.d(TAG, "stopVideo. type: " + type);
		String tmp = "";
		vplayer.pause();
		startBtn.setVisibility(View.VISIBLE);
		stopBtn.setVisibility(View.GONE);
		if (FINISH_PLAYER != type) {
			resultTv.setText(MEMCHECK.RET_STR_PAUSE_E);
			resultTv.setVisibility(View.VISIBLE);
		} else {
			if (Integer.parseInt(ret) != MEMCHECK.RET_OK) {
				tmp = MEMCHECK.parseResult(TAG, result);
				resultTv.setText(tmp);
				resultTv.setVisibility(View.VISIBLE);
			}
		}
		etNum.setEnabled(true);
	}

	private void restartVideo() {
		Logger.d(TAG, "restartVideo.");
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
		newRun = CREATE_RUN;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(TAG, "onResume.");
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		if(CREATE_RUN == newRun){
			newRun = RESUME_RUN;
		}else if(RESUME_RUN == newRun) {
			doTask(RESUME_PLAYER);
		}
	}
}
