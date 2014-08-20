package com.example.test;

import com.cg.memtest.R;
import com.jnitest.memutils.memcheck;
import com.self.debug.Logger;

//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

//public class MainActivity extends ActionBarActivity {
public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	private final static String VIDEO_PATH = "/storage/sdcard0/test.avi";
	private TextView resultTv;
	private EditText etNum;
	private Button startBtn;
	private Button stopBtn;
	private VideoView vplayer;
	private int result = 0;
	private int thread = 0;
	private Thread tdMemcheck;

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
				vplayer.setVideoPath(VIDEO_PATH);
				vplayer.start();
				startBtn.setVisibility(View.INVISIBLE);
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
							String tmp = new memcheck().doTask(no);
							android.util.Log.d(TAG, tmp);
						}
					});
					tdMemcheck.setName("" + i);
					tdMemcheck.start();
				}
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
				vplayer.pause();
				startBtn.setVisibility(View.VISIBLE);
				stopBtn.setVisibility(View.INVISIBLE);
				resultTv.setVisibility(View.VISIBLE);
				etNum.setEnabled(true);
				// tdMemcheck.destroy();
				// tdMemcheck.dumpStack();
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
				Logger.d(TAG, "The video this loop play finished.");
				vplayer.setVideoPath(VIDEO_PATH);
				vplayer.start();
			}

		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate.");
		initview();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		vplayer.resume();
	}
}
