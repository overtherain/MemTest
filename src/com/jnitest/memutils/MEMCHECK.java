package com.jnitest.memutils;

import com.self.debug.Logger;

public class MEMCHECK {
	public final static int RET_OK = 0;
	public final static int RET_MEM_BASE_E = 100;
	public final static int RET_SET_BASE_E = 200;
	public final static int RET_RD_BASE_E = 300;
	public final static int RET_CHK_BASE_E = 400;
	public final static int RET_FILE_BASE_E = 500;

	public final static int RET_MEM_NULL_E = (RET_MEM_BASE_E + 1);
	public final static int RET_SET_55_E = (RET_SET_BASE_E + 1);
	public final static int RET_SET_AA_E = (RET_SET_BASE_E + 2);
	public final static int RET_RD_55_E = (RET_RD_BASE_E + 1);
	public final static int RET_RD_AA_E = (RET_RD_BASE_E + 2);
	public final static int RET_CHK_55_E = (RET_CHK_BASE_E + 1);
	public final static int RET_CHK_AA_E = (RET_CHK_BASE_E + 2);
	public final static int RET_FILE_RD_E = (RET_FILE_BASE_E + 1);
	public final static int RET_FILE_WT_E = (RET_FILE_BASE_E + 2);
	public final static int RET_FILE_OP_E = (RET_FILE_BASE_E + 3);

	public final static String RET_SRT_OK = "[PASS]\nOK.";
	public final static String RET_STR_MEM_NULL_E = "[FAIL]\nMEM is NULL.";
	public final static String RET_STR_SET_55_E = "[FAIL]\nSET 55 Error.";
	public final static String RET_STR_SET_AA_E = "[FAIL]\nSET AA Error.";
	public final static String RET_STR_RD_55_E = "[FAIL]\nREAD 55 Error.";
	public final static String RET_STR_RD_AA_E = "[FAIL]\nREAD AA Error.";
	public final static String RET_STR_CHK_55_E = "[FAIL]\nCHECK 55 Error.";
	public final static String RET_STR_CHK_AA_E = "[FAIL]\nCHECK AA Error.";
	public final static String RET_STR_FILE_RD_E = "[FAIL]\nFILE read Error.";
	public final static String RET_STR_FILE_WT_E = "[FAIL]\nFILE write Error.";
	public final static String RET_STR_FILE_OP_E = "[FAIL]\nFILE open Error.";
	public final static String RET_STR_UNKNOW_E = "[FAIL]\nUnknow Error.";
	public final static String RET_STR_PAUSE_E = "[PAUSE]\nJUST PAUSE.";

	public static String parseResult(String TAG, int code) {
		String ret = "";
		switch (code) {
		case RET_MEM_NULL_E:
			Logger.d(TAG, RET_STR_MEM_NULL_E);
			ret = RET_STR_MEM_NULL_E;
			break;
		case RET_SET_55_E:
			Logger.d(TAG, RET_STR_SET_55_E);
			ret = RET_STR_SET_55_E;
			break;
		case RET_SET_AA_E:
			Logger.d(TAG, RET_STR_SET_AA_E);
			ret = RET_STR_SET_AA_E;
			break;
		case RET_RD_55_E:
			Logger.d(TAG, RET_STR_RD_55_E);
			ret = RET_STR_RD_55_E;
			break;
		case RET_RD_AA_E:
			Logger.d(TAG, RET_STR_RD_AA_E);
			ret = RET_STR_RD_AA_E;
			break;
		case RET_CHK_55_E:
			Logger.d(TAG, RET_STR_CHK_55_E);
			ret = RET_STR_CHK_55_E;
			break;
		case RET_CHK_AA_E:
			Logger.d(TAG, RET_STR_CHK_AA_E);
			ret = RET_STR_CHK_AA_E;
			break;
		case RET_FILE_RD_E:
			Logger.d(TAG, RET_STR_FILE_RD_E);
			ret = RET_STR_FILE_RD_E;
			break;
		case RET_FILE_WT_E:
			Logger.d(TAG, RET_STR_FILE_WT_E);
			ret = RET_STR_FILE_WT_E;
			break;
		case RET_FILE_OP_E:
			Logger.d(TAG, RET_STR_FILE_OP_E);
			ret = RET_STR_FILE_OP_E;
			break;
		case RET_OK:
			Logger.d(TAG, RET_SRT_OK);
			ret = RET_SRT_OK;
			break;
		default:
			Logger.d(TAG, RET_STR_UNKNOW_E);
			ret = RET_STR_UNKNOW_E;
			break;
		}
		return ret;
	}
}
