package com.self.debug;

public class Logger {
	public static int v(String tag, String msg){
		return android.util.Log.d(tag, "cg.gdz->" + msg);
	}
	public static int d(String tag, String msg){
		return android.util.Log.d(tag, "cg.gdz->" + msg);
	}
	public static int w(String tag, String msg){
		return android.util.Log.d(tag, "cg.gdz->" + msg);
	}
	public static int e(String tag, String msg){
		return android.util.Log.d(tag, "cg.gdz->" + msg);
	}
}
