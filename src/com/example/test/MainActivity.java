package com.example.test;

import java.util.Timer;
import java.util.TimerTask;

import com.cg.memtest.R;
import com.jnitest.memutils.MEMCHECK;
import com.jnitest.memutils.memcheck;
import com.self.debug.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private final static String TAG = "com.example.test/MainActivity";
	private final static String VIDEO_PATH = "/storage/sdcard0/test.avi";
	private final static int STOP_PLAYER = 0;
	private final static int START_PLAYER = 1;
	private final static int RESUME_PLAYER = 2;
	private final static int RESTART_PLAYER = 3;
	private final static int FINISH_PLAYER = 4;
	private final static int WAIT_CHARGE = 5;
	private final static int CREATE_RUN = 0;
	private final static int RESUME_RUN = 1;

	private TextView resultTv;
	private EditText etNum;
	private Button startBtn;
	private Button stopBtn;
	private VideoView vplayer;
	private int result = 0;
	private int thread = 0;
	private memcheck curMemcheck;
	private Thread tdMemcheck;
	private String ret = "OK";
	private int newRun = 0;
	// this area will check battery status.
	private BroadcastReceiver batteryLevelRcvr;
	private IntentFilter batteryLevelFilter;
	private int gRawlevel = 0;
	private int gScale = 0;
	private int gStatus = 0;
	private int gHealth = 0;
	private int gLevel = 0;

	public void initview() {
		Logger.d(TAG, "initview.");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
				if (false == isBatteryFull()) {
					Logger.d(TAG, "low battery, need to charge");
					curMemcheck.sendCmd(MEMCHECK.STAT_BREAK_INT);
					doTask(WAIT_CHARGE);
					Logger.d(TAG, "Add a schedule to check battery charging.");
					timer.schedule(task, 10000);
					// finish();
				} else {
					Logger.d(TAG, "battery status is ok, no need to charge");
					curMemcheck.sendCmd(MEMCHECK.STAT_CONTINUE_INT);
					doTask(RESTART_PLAYER);
				}
			}

		});
		curMemcheck = new memcheck();
	}

	private int prepState() {
		int ret = MEMCHECK.RET_OK;
		// set audio status
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				AudioManager.RINGER_MODE_SILENT, AudioManager.FLAG_PLAY_SOUND);
		// set back light
		setBrightness(80);
		return ret;
	}

	private void setBrightness(int brightness) {
		/*
		 * try { Logger.d("Progress", "set brightness"); IHardwareService
		 * hardware = IHardwareService.Stub
		 * .asInterface(ServiceManager.getService("hardware")); if (hardware !=
		 * null) { hardware.setBacklights(brightness); }
		 * Logger.d("set brightness", "OK"); } catch (RemoteException doe) {
		 * Logger.d("set brightness", "ERROR"); }
		 */
	}

	public void monitorBatteryState() {
		Logger.d(TAG, "monitorBatteryState");
		batteryLevelRcvr = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				int status = intent.getIntExtra("status", -1);
				int health = intent.getIntExtra("health", -1);
				int level = -1; // percentage, or -1 for unknown
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				gRawlevel = rawlevel;
				gScale = scale;
				gStatus = status;
				gHealth = health;
				gLevel = level;
				Logger.d(TAG, "monitorBatteryState:\n\tgRawlevel = "
						+ gRawlevel + ", gScale = " + gScale + ", gStatus = "
						+ gStatus + ", gHealth = " + gHealth + ", gLevel = "
						+ gLevel);
				if (false == isBatteryFull()) {
					Logger.d(TAG, "low battery, need to charge");
				}
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
	}

	private boolean isBatteryFull() {
		boolean isFull = false;
		StringBuilder sb = new StringBuilder();
		Logger.d(TAG, "isBatteryFull:\n\tgRawlevel = " + gRawlevel
				+ ", gScale = " + gScale + ", gStatus = " + gStatus
				+ ", gHealth = " + gHealth + ", gLevel = " + gLevel);
		if (BatteryManager.BATTERY_HEALTH_OVERHEAT == gHealth) {
			sb.append("'s battery feels very hot!");
		} else {
			switch (gStatus) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				sb.append("no battery.");
//				isFull = true;
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:
				sb.append("'s battery");
				if (gLevel <= 33) {
					sb.append(" is charging, battery level is low" + "["
							+ gLevel + "]");
				} else if (gLevel <= 84) {
					sb.append(" is charging." + "[" + gLevel + "]");
//					isFull = true;
				} else {
					sb.append(" will be fully charged.");
					isFull = true;
				}
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				if (gLevel == 0) {
					sb.append(" needs charging right away.");
				} else if (gLevel > 0 && gLevel <= 33) {
					sb.append(" is about ready to be recharged, battery level is low"
							+ "[" + gLevel + "]");
				} else {
					sb.append("'s battery level is" + "[" + gLevel + "]");
//					isFull = true;
				}
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				sb.append(" is fully charged.");
				break;
			default:
				sb.append("'s battery is indescribable!");
				break;
			}
		}
		return isFull;
	}

	Timer timer = new Timer();
	@SuppressLint("HandlerLeak")
	Handler hdBatStatus = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "HandleMessage for check charging status...");
			switch(msg.what){
			case MEMCHECK.STAT_BREAK_INT:
				doTask(WAIT_CHARGE);
				break;
			case MEMCHECK.STAT_CONTINUE_INT:
				doTask(RESTART_PLAYER);
				break;
			}
			super.handleMessage(msg);
		}

	};
	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Logger.d(TAG, "Timer begin to check battery status....");
			Message message = new Message();
			if(false == isBatteryFull()){
				message.what = MEMCHECK.STAT_BREAK_INT;
			}else{
				message.what = MEMCHECK.STAT_CONTINUE_INT;
			}
			hdBatStatus.sendMessage(message);
		}

	};

	public void doTask(int sw) {
		Logger.d(TAG, "doTask ---> begin");
		String msg = "";
		switch (sw) {
		case WAIT_CHARGE:
			msg = "Wait for charge";
			stopVideo(STOP_PLAYER);
			break;
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
		Logger.d(TAG, "doTask ---> end");
	}

	private void startVideo() {
		Logger.d(TAG, "startVideo.");
		resumeVideo();
		doMemCheck();
	}

	private void resumeVideo() {
		Logger.d(TAG, "resumeVideo.");
		vplayer.setVideoPath(VIDEO_PATH);
		vplayer.start();
		prepState();
		startBtn.setVisibility(View.GONE);
		stopBtn.setVisibility(View.VISIBLE);
		resultTv.setVisibility(View.INVISIBLE);
		etNum.setEnabled(false);
	}

	private void doMemCheck() {
		Logger.d(TAG, "doCheck.");
		Logger.d(TAG, "thread: " + thread);
		for (; thread > 0; thread--) {
			tdMemcheck = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					int no = Integer.parseInt(Thread.currentThread().getName());
					ret = curMemcheck.doTask(no);
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
		} else if(WAIT_CHARGE == type){
			resultTv.setText(MEMCHECK.RET_STR_WAIT_CHARGE);
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Window win = getWindow();
		// win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate.");
		initview();
		monitorBatteryState();
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
		if (CREATE_RUN == newRun) {
			newRun = RESUME_RUN;
		} else if (RESUME_RUN == newRun) {
			doTask(RESUME_PLAYER);
		}
		if (null != curMemcheck) {
			curMemcheck.sendCmd(MEMCHECK.STAT_CONTINUE_INT);
		} else {
			Logger.d(TAG,
					"curMemcheck is null, create a new thread to run memckech.");
			doMemCheck();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.d(TAG, "onDestory");
		if (null != curMemcheck) {
			Logger.d(TAG, "tdMemcheck is not null, just release the resource.");
			Logger.d(TAG, "send cmd to stop background check memory thread");
			curMemcheck.sendCmd(MEMCHECK.STAT_BREAK_INT);
			doTask(STOP_PLAYER);
			// tdMemcheck.stop();
			// tdMemcheck.destroy();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			Logger.d(TAG, "tdMemcheck is null, no need to release.");
		}
		unregisterReceiver(batteryLevelRcvr);
	}
}
