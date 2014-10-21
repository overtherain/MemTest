package com.jnitest.memutils;

import com.self.debug.Logger;

public class memcheckJNI {

	private final static String TAG = "memcheck-jni";
	public native String doTask(int no);
	public native String sendCmd(int cmd);
	static{
		System.loadLibrary("memcheck");
	}
	public memcheckJNI(){
		Logger.d(TAG, "Create a object.");
	}
}
