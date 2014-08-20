package com.jnitest.memutils;

public class memcheck {

	public native String doTask(int no);
	static{
		System.loadLibrary("memcheck");
	}
}
