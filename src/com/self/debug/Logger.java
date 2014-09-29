package com.self.debug;

public class Logger {
	private final static boolean isDebug = false;

	public static int v(String tag, String msg) {
		if (isDebug) {
			return android.util.Log.d(tag, "cg.gdz->" + msg);
		} else {
			return 0;
		}
	}

	public static int d(String tag, String msg) {
		if (isDebug) {
			return android.util.Log.d(tag, "cg.gdz->" + msg);
		} else {
			return 0;
		}
	}

	public static int w(String tag, String msg) {
		if (isDebug) {
			return android.util.Log.d(tag, "cg.gdz->" + msg);
		} else {
			return 0;
		}
	}

	public static int e(String tag, String msg) {
		if (isDebug) {
			return android.util.Log.d(tag, "cg.gdz->" + msg);
		} else {
			return 0;
		}
	}
}
