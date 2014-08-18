package com.example.test;

import com.self.debug.Logger;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends ActionBarActivity {
	private final static String TAG = "MainActivity";
	private final static String VIDEO_PATH = "/storage/sdcard0/test.avi";
	private TextView tv;
	private Button startBtn;
	private Button stopBtn;
	private ImageView img;
	private VideoView vplayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.d(TAG, "onCreate.");
		tv = (TextView) findViewById(R.id.tips);
		tv.setText("FAIL");
		tv.setTextSize(120);
		tv.setTextColor(android.graphics.Color.WHITE);
		tv.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLACK);
		tv.setVisibility(View.VISIBLE);
		startBtn = (Button) findViewById(R.id.start);
		startBtn.setText("START");
		startBtn.setTextColor(android.graphics.Color.GREEN);
		startBtn.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLACK);
		startBtn.setVisibility(View.VISIBLE);
		startBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				vplayer.get
				vplayer.setVideoPath(VIDEO_PATH);
				vplayer.start();
				startBtn.setVisibility(View.INVISIBLE);
				stopBtn.setVisibility(View.VISIBLE);
			}
		});
		stopBtn = (Button)findViewById(R.id.stop);
		stopBtn.setText("STOP");
		stopBtn.setTextColor(android.graphics.Color.GREEN);
		stopBtn.setShadowLayer(1.0f, 2.0f, 2.0f, android.graphics.Color.BLACK);
		stopBtn.setVisibility(View.INVISIBLE);
		stopBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				vplayer.pause();
				startBtn.setVisibility(View.VISIBLE);
				stopBtn.setVisibility(View.INVISIBLE);
			}

		});
		vplayer = (VideoView)findViewById(R.id.player);
		vplayer.setKeepScreenOn(true);
		vplayer.setVisibility(View.VISIBLE);
		vplayer.setMinimumHeight(750);
		vplayer.setMinimumWidth(450);
		vplayer.setOnCompletionListener(new OnCompletionListener(){

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		vplayer.resume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
