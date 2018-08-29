package com.example.test;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.cg.memtest.R;
import com.jnitest.memutils.CHK_STR;
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
// liujun.modify
import android.media.MediaPlayer.OnPreparedListener;
// liujun.modify
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
// dfsl panlei add
import android.net.Uri;
// dfsl panlei end

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity {
	private final static String TAG = "com.example.test/MainActivity";
    private final static String[] VIDEO_PATH_OPTIONS = new String[]{
        "/data/local/tmp/test.avi",
        "/sdcard/test.avi",
        "/storage/sdcard0/test.avi",
        //"/storage/emulated/legacy/test.avi",
        "/storage/emulated/0/test.avi"};
	// dfsl panlei add
	private Uri video_uri;
	// dfsl panlei end
    private final static int STOP_PLAYER = 0;
	private final static int START_PLAYER = 1;
	private final static int RESUME_PLAYER = 2;
	private final static int RESTART_PLAYER = 3;
	private final static int FINISH_PLAYER = 4;
	private final static int WAIT_CHARGE = 5;
	private final static int CREATE_RUN = 0;
	private final static int RESUME_RUN = 1;
	private final static int FIRST_RUN = 0;
	private final static int UNFIRST_RUN = 1;

	private String VIDEO_PATH = "-1";
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
	private int addTimer = FIRST_RUN;
	// this area will check battery status.
	private BroadcastReceiver batteryLevelRcvr;
	private IntentFilter batteryLevelFilter;
	private int gRawlevel = 0;
	private int gScale = 0;
	private int gStatus = 0;
	private int gHealth = 0;
	private int gLevel = 0;
	private int playStatus = STOP_PLAYER;
// liujun.modify
	private boolean hasSet = false;
	private boolean mPrepared = false;
	private boolean mNeedStart = false;
// liujun.modify
    private Vibrator mVibrator = null;
    private static final long[] V_TIME = {0,1000,0,1000};
    WindowManager.LayoutParams lp;

	private boolean initVideoPath(){
	    boolean ret = false;
	    startBtn.setEnabled(ret);
	    stopBtn.setEnabled(ret);
	    for(String item: VIDEO_PATH_OPTIONS){
	        File file = new File(item);
	        if (file.exists() && file.canRead())
            {
	            VIDEO_PATH = item;
	            ret = true;
	            startBtn.setEnabled(ret);
	            stopBtn.setEnabled(ret);
                break;
            }
	    }
	    return ret;
	}

	public void initview() {
		Logger.d(TAG, "initview.");
		TextView time = (TextView)findViewById(R.id.tips1);
		TextView memory = (TextView)findViewById(R.id.tips2);
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
		startBtn.setVisibility(View.INVISIBLE);
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
				if (true == isNeedPause()) {
					Logger.d(TAG, "low battery, need to charge");
					curMemcheck.sendCmd(CHK_STR.STAT_BREAK_INT);
					doTask(WAIT_CHARGE);
				} else {
					Logger.d(TAG, "battery status is ok, no need to charge");
					curMemcheck.sendCmd(CHK_STR.STAT_CONTINUE_INT);
					doTask(RESTART_PLAYER);
				}
			}

		});
		// liujun.modify
		vplayer.setOnPreparedListener(new OnPreparedListener(){
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				mPrepared = true;
				if(mNeedStart){
					vplayer.start();
					mNeedStart = false;
					Logger.d(TAG, "[DayL]prepared, let's rock");
				} else {
					Logger.d(TAG, "[DayL]prepared, but NOT start");
				}
			}
		});
		// liujun.modify
		curMemcheck = new memcheck();
	}

	private int prepState() {
		int ret = CHK_STR.RET_OK;
		// set audio status
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// dfsl panlei add
		//mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		//mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
		//		AudioManager.RINGER_MODE_SILENT, AudioManager.FLAG_PLAY_SOUND);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
		// dfsl panlei end
		// set back light
		setBrightness(80);
		return ret;
	}

	private void setBrightness(int brightness) {
        /*try { 
            Logger.d("Progress", "set brightness");
            IHardwareService hardware = IHardwareService.Stub.asInterface(ServiceManager.getService("hardware")); 
            if (hardware != null) { 
                hardware.setBacklights(brightness); 
            }
            Logger.d("set brightness", "OK"); 
        } catch (RemoteException doe) {
            Logger.d("set brightness", "ERROR"); 
        }*/
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
				if (true == isNeedPause()) {
					Logger.d(TAG, "low battery, need to charge");
				}
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
	}

	private boolean isNeedPause() {
		boolean needpause = true;
		String str = "BatteryManager.InitSettings";
		Logger.d(TAG, "isBatteryFull:\n\tgRawlevel = " + gRawlevel
				+ ", gScale = " + gScale + ", gStatus = " + gStatus
				+ ", gHealth = " + gHealth + ", gLevel = " + gLevel);
		if (BatteryManager.BATTERY_HEALTH_OVERHEAT == gHealth) {
			// needpause = true;
			ret = "BatteryManager.BATTERY_HEALTH_OVERHEAT";
		} else {
			switch (gStatus) {
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				str = "BatteryManager.BATTERY_STATUS_UNKNOWN";
				break;
			case BatteryManager.BATTERY_STATUS_CHARGING:
				str = "BatteryManager.BATTERY_STATUS_CHARGING";
				if (gLevel <= 33) {
					needpause = true;
				} else if (gLevel <= 84) {
					needpause = false;
				} else {
					needpause = false;
				}
				break;
			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				ret = "BatteryManager.BATTERY_STATUS_DISCHARGING and ";
			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				ret += "BatteryManager.BATTERY_STATUS_NOT_CHARGING";
				if (gLevel == 0) {
					needpause = true;
				} else if (gLevel > 0 && gLevel <= 33) {
					needpause = true;
				} else {
					needpause = false;
				}
				break;
			case BatteryManager.BATTERY_STATUS_FULL:
				ret = "BatteryManager.BATTERY_STATUS_FULL";
				needpause = false;
				break;
			default:
				ret = "BatteryManager.Default";
				break;
			}
		}
		Logger.d(TAG, "Now battery status is: " + str);
		return needpause;
	}

	Timer timer = new Timer();

	Handler hdBatStatus = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "HandleMessage for check charging status...");
			switch (msg.what) {
			case CHK_STR.STAT_BREAK_INT:
				doTask(WAIT_CHARGE);
				break;
			case CHK_STR.STAT_CONTINUE_INT:
				doTask(RESUME_PLAYER);
				break;
			}
			super.handleMessage(msg);
		}

	};
	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Logger.d(TAG, "Timer begin to check battery status....");
			Message message = new Message();
			if (false == isNeedPause()) {
				message.what = CHK_STR.STAT_CONTINUE_INT;
			} else {
				message.what = CHK_STR.STAT_BREAK_INT;
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
			stopVideo(WAIT_CHARGE);
			break;
		case START_PLAYER:
			msg = "startVideo";
			if(STOP_PLAYER == playStatus){
				// liujun.modify
				if(!hasSet){
					hasSet = true;
					// dfsl panlei add
					//vplayer.setVideoPath(VIDEO_PATH);
					vplayer.setVideoURI(video_uri);
					// dfsl panlei end
				} else {
					Logger.d(TAG, "[DayL]video path set");
				}
				// liujun.modify
				startVideo();
			}
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
		//dfsl modify by zhangshaobin begin
		if(mVibrator != null){
		    mVibrator.vibrate(V_TIME,0);
		}
		//dfsl modify end
        Logger.d(TAG, "startvibrate.");
		resumeVideo();
		doMemCheck();
	}

	private void resumeVideo() {
		Logger.d(TAG, "resumeVideo.");

		playStatus = START_PLAYER;
		// liujun.modify
		if(!mPrepared){
			Logger.d(TAG, "[DayL]start on NOT prepared");
			mNeedStart = true;
		} else {
			Logger.d(TAG, "[DayL]start on prepared");
			//dfsl modify by zhangshaobin begin
			if(mVibrator != null){
			    mVibrator.vibrate(V_TIME,0);
			}
			//dfsl modify end
	        Logger.d(TAG, "startvibrate.");
			vplayer.start();
		}
		// liujun.modify
		prepState();
		startBtn.setVisibility(View.INVISIBLE);
		stopBtn.setVisibility(View.INVISIBLE);
		resultTv.setVisibility(View.INVISIBLE);
		etNum.setEnabled(false);
	}

	private void doMemCheck() {
		Logger.d(TAG, "doMemCheck.");
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
					if (Integer.parseInt(ret) != CHK_STR.RET_OK) {
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
		playStatus = STOP_PLAYER;
        if (mVibrator != null) {
            mVibrator.cancel();
        }
		if(vplayer.isPlaying()){
//			vplayer.stopPlayback();
			vplayer.pause();
		}
		startBtn.setVisibility(View.INVISIBLE);
		stopBtn.setVisibility(View.GONE);
		if (FINISH_PLAYER != type) {
			if (WAIT_CHARGE == type) {
				Logger.d(TAG, "Waiting for charging battery.");
				resultTv.setText(CHK_STR.RET_STR_WAIT_CHARGE);
				resultTv.setVisibility(View.VISIBLE);
			} else {
				Logger.d(TAG, "not finish playing.");
				resultTv.setText(CHK_STR.RET_STR_PAUSE_E);
				resultTv.setVisibility(View.VISIBLE);
			}
		} else {
			if (Integer.parseInt(ret) != CHK_STR.RET_OK) {
				tmp = CHK_STR.parseResult(TAG, result);
				resultTv.setText(tmp);
				resultTv.setVisibility(View.VISIBLE);
			}
		}
		etNum.setEnabled(true);
	}

	private void restartVideo() {
		Logger.d(TAG, "restartVideo.");
		// liujun.modify
		//dfsl modify by zhangshaobin begin
		if(mVibrator != null){
		    mVibrator.vibrate(V_TIME,0);
		}
		//dfsl modify end
        Logger.d(TAG, "startvibrate.");
		if(vplayer.isPlaying()){
			vplayer.stopPlayback();
			Logger.d(TAG, "need to stop then start.");
		}
		// dfsl panel add
		//vplayer.setVideoPath(VIDEO_PATH);
		vplayer.setVideoURI(video_uri);
		// dfsl panel end
		vplayer.start();
		// liujun.modify
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
		lp = getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		getWindow().setAttributes(lp);
        mVibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
		monitorBatteryState();
		newRun = CREATE_RUN;
		if (FIRST_RUN == addTimer) {
			Logger.d(TAG, "Add a schedule to check battery charging.");
			timer.schedule(task, 5000, 60000*5);
			addTimer = UNFIRST_RUN;
		}
		// dfsl panlei add
		video_uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.test);
		// dfsl panlei end
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(TAG, "onResume.");
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		/*if(!initVideoPath()){
            Toast.makeText(this.getApplicationContext(), "Not video in phone. copy video to phone.", Toast.LENGTH_LONG).show();
            return;
		}*/
		if(null != vplayer){
			vplayer.requestFocus();
		}
		if (CREATE_RUN == newRun) {
			newRun = RESUME_RUN;
			if(!hasSet){
				hasSet = true;
				// dfsl panlei add
				//Logger.d(TAG, "VIDEO_PATH ist " + VIDEO_PATH);
				//vplayer.setVideoPath(VIDEO_PATH);
				vplayer.setVideoURI(video_uri);
				// dfsl panlei end
			}
		} else if (RESUME_RUN == newRun) {
			doTask(RESUME_PLAYER);
		}
		if (null != curMemcheck) {
			if (STOP_PLAYER == playStatus) {
				curMemcheck.sendCmd(CHK_STR.STAT_CONTINUE_INT);
			}
		} else {
			Logger.d(TAG,
					"curMemcheck is null, create a new thread to run memckech.");
			doMemCheck();
		}
	}

	//dfsl add by zhangshaobin begin
	@Override
	protected void onPause(){
	    super.onPause();
	    Logger.d(TAG, "onPause");
	    if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
	}
	
	@Override
    public void onBackPressed() {
        Logger.d(TAG, "onBackPressed");
        
        this.finish();
    }
	//dfsl add end
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Logger.d(TAG, "onDestory");
        //lp.screenBrightness = -1;
		//getWindow().setAttributes(lp);
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
		if (null != curMemcheck) {
			Logger.d(TAG, "tdMemcheck is not null, just release the resource.");
			Logger.d(TAG, "send cmd to stop background check memory thread");
			curMemcheck.sendCmd(CHK_STR.STAT_BREAK_INT);
			doTask(STOP_PLAYER);
			// tdMemcheck.stop();
			// tdMemcheck.destroy();
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			Logger.d(TAG, "tdMemcheck is null, no need to release.");
		}
		if(null != vplayer){
			vplayer.stopPlayback();
		}
		unregisterReceiver(batteryLevelRcvr);
	}
}
