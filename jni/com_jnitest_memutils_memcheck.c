#include <stdio.h>
#include <jni.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include "com_jnitest_memutils_memcheck.h"
//#include <utils/Log.h>
#include <android/log.h>

#define AREA_LEN_DEF 2048
#define E_STR_LEN_DEF 4096
#define TAG "MEMCHECK"

#define STR_TYPE_BASE	10
#define STR_TYPE_ST_E	(STR_TYPE_BASE + 1)
#define STR_TYPE_POS_E	(STR_TYPE_BASE + 2)
#define STR_TYPE_DUMP_E	(STR_TYPE_BASE + 3)

#define RET_OK	0
#define RET_MEM_BASE_E	100
#define RET_SET_BASE_E	200
#define RET_RD_BASE_E	300
#define RET_CHK_BASE_E	400
#define RET_FILE_BASE_E	500

#define RET_MEM_NULL_E	(RET_MEM_BASE_E + 1)
#define RET_SET_55_E	(RET_SET_BASE_E + 1)
#define RET_SET_AA_E	(RET_SET_BASE_E + 2)
#define RET_RD_55_E	(RET_RD_BASE_E + 1)
#define RET_RD_AA_E	(RET_RD_BASE_E + 2)
#define RET_CHK_55_E	(RET_CHK_BASE_E + 1)
#define RET_CHK_AA_E	(RET_CHK_BASE_E + 2)
#define RET_FILE_RD_E	(RET_FILE_BASE_E + 1)
#define RET_FILE_WT_E	(RET_FILE_BASE_E + 2)
#define RET_FILE_OP_E	(RET_FILE_BASE_E + 3)

#define RET_SRT_OK		"gdz.log thread: %d doTask=%d -> running ok."
#define RET_STR_MEM_NULL_E	"gdz.log thread: %d doTask=%d -> MEM is NULL."
#define RET_STR_SET_55_E	"gdz.log thread: %d doTask=%d -> SET 55 Error."
#define RET_STR_SET_AA_E	"gdz.log thread: %d doTask=%d -> SET AA Error."
#define RET_STR_RD_55_E		"gdz.log thread: %d doTask=%d -> READ 55 Error."
#define RET_STR_RD_AA_E		"gdz.log thread: %d doTask=%d -> READ AA Error."
#define RET_STR_CHK_55_E	"gdz.log thread: %d doTask=%d -> CHECK 55 Error."
#define RET_STR_CHK_AA_E	"gdz.log thread: %d doTask=%d -> CHECK AA Error."
#define RET_STR_FILE_RD_E	"gdz.log thread: %d doTask=%d -> FILE read Error."
#define RET_STR_FILE_WT_E	"gdz.log thread: %d doTask=%d -> FILE write Error."
#define RET_STR_FILE_OP_E	"gdz.log thread: %d doTask=%d -> FILE open Error."
#define RET_STR_UNKNOW_E	"gdz.log thread: %d doTask=%d -> Unknow Error."

//#define TEST

char gE_Str[E_STR_LEN_DEF];
int debug = 1;

JNIEXPORT jstring JNICALL Java_com_jnitest_memutils_memcheck_doTask(JNIEnv *env, jobject obj, jint thread)
{
	int ret = RET_OK;
	int curThread = (int)thread;
	char retStr[255];
	ret = doTask(thread);
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d thread: %d doTask=%d\n", __func__, __LINE__, thread, ret);
	switch(ret){
	case RET_OK:
		sprintf(retStr, RET_SRT_OK, curThread, ret);
		break;
	case RET_MEM_NULL_E:
		sprintf(retStr, RET_STR_MEM_NULL_E, curThread, ret);
		break;
	case RET_SET_55_E:
		sprintf(retStr, RET_STR_SET_55_E, curThread, ret);
		break;
	case RET_SET_AA_E:
		sprintf(retStr, RET_STR_SET_AA_E, curThread, ret);
		break;
	case RET_RD_55_E:
		sprintf(retStr, RET_STR_RD_55_E, curThread, ret);
		break;
	case RET_RD_AA_E:
		sprintf(retStr, RET_STR_RD_AA_E, curThread, ret);
		break;
	case RET_CHK_55_E:
		sprintf(retStr, RET_STR_CHK_55_E, curThread, ret);
		break;
	case RET_CHK_AA_E:
		sprintf(retStr, RET_STR_CHK_AA_E, curThread, ret);
		break;
	case RET_FILE_RD_E:
		sprintf(retStr, RET_STR_FILE_RD_E, curThread, ret);
		break;
	case RET_FILE_WT_E:
		sprintf(retStr, RET_STR_FILE_WT_E, curThread, ret);
		break;
	case RET_FILE_OP_E:
		sprintf(retStr, RET_STR_FILE_OP_E, curThread, ret);
		break;
	default:
		sprintf(retStr, RET_STR_UNKNOW_E, curThread, ret);
		break;
	}
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d retSrt:%s", __func__, __LINE__, retStr);
	sprintf(retStr, "%d", ret);
	return (*env)->NewStringUTF(env, (char*)retStr);
}

int doTask(int num)
{
	char * gpAREA;
	char * gpERR_AREA;
	int gE_Pos = -1;
	int gCur_Thread = num;
	char gE_Val = 0;
	char value = 0;

	int gRelease = 1;

	int ret = RET_OK;
	char tmp = 0;
	int i = 0;
	// 1. malloc a gpAREA
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d Thread: %d Malloc gpAREA and gpERR_AREA.\n", __func__, __LINE__, gCur_Thread);
	gpAREA = (char *)malloc(sizeof(char) * AREA_LEN_DEF);
	if(gpAREA == NULL){
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d gpAREA is NULL.\n", __func__, __LINE__, gCur_Thread);
		ret = RET_MEM_NULL_E;
	}
	//assert(gpAREA != NULL);
	// 2. clear it to 0
	memset(gpAREA, 0, sizeof(char)*AREA_LEN_DEF);
	#if 0
	gpERR_AREA = (char *)malloc(sizeof(char) * AREA_LEN_DEF);
	if(gpERR_AREA == NULL){
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gpERR_AREA is NULL.\n", gCur_Thread);
		ret = 1002;
	}
	//assert(gpERR_AREA != NULL);
	memset(gpERR_AREA, 0, sizeof(char)*AREA_LEN_DEF);
	#endif

	// 3. set to value
	while(1){
		// 3. set to value
		value = 0x55;
		if(1 == debug){
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d set %02x Thread: %d Begin to set 55 to mem.\n",
						__func__, __LINE__, value, gCur_Thread);
		}
		for(i=0; i<AREA_LEN_DEF; i++){
			gE_Pos = i;
			gpAREA[i] = value;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = RET_SET_55_E;
				sprintf(gE_Str, "[ERROR] set 0x55 at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d gE_Str: %s\n", __func__, __LINE__, gCur_Thread, gE_Str);
				dumpMem(gE_Str, gCur_Thread, STR_TYPE_POS_E);
				dumpMem(gpAREA, gCur_Thread, STR_TYPE_DUMP_E);
				//goto FINISH;
				break;
			}
		}
		if(RET_OK != ret) break;
		if(1 == debug){
			debug = 0;
			dumpMem(gE_Str, gCur_Thread, STR_TYPE_ST_E);
		}
		// 4. read if value
		for(i=0; i<AREA_LEN_DEF; i++){
			gE_Pos = i;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
#ifdef TEST
				if(0 == debug && 1023 == i){
					ret = RET_CHK_55_E;
					sprintf(gE_Str, "[ERROR] set 0x55 at %04d, value: %02x", gE_Pos, gE_Val);
					__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d gE_Str: %s\n", __func__, __LINE__, gCur_Thread, gE_Str);
					dumpMem(gE_Str, gCur_Thread, STR_TYPE_POS_E);
					dumpMem(gpAREA, gCur_Thread, STR_TYPE_DUMP_E);
					//goto FINISH;
					break;
				}
#endif
			}else{
				ret = RET_CHK_55_E;
				sprintf(gE_Str, "[ERROR] set 0xaa at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d gE_Str: %s\n", __func__, __LINE__, gCur_Thread, gE_Str);
				dumpMem(gE_Str, gCur_Thread, STR_TYPE_POS_E);
				dumpMem(gpAREA, gCur_Thread, STR_TYPE_DUMP_E);
				//goto FINISH;
				break;
			}
		}
		if(RET_OK != ret) break;
		value = 0xaa;
		if(1 == debug){
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d set %02x Thread: %d Begin to set aa to mem.\n",
						__func__, __LINE__, value, gCur_Thread);
		}
		for(i=0; i<AREA_LEN_DEF; i++){
			gE_Pos = i;
			gpAREA[i] = value;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = RET_SET_AA_E;
				sprintf(gE_Str, "[ERROR] set 0x55 at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d, gE_Str: %s\n", __func__, __LINE__, gCur_Thread, gE_Str);
				dumpMem(gE_Str, gCur_Thread, STR_TYPE_POS_E);
				dumpMem(gpAREA, gCur_Thread, STR_TYPE_DUMP_E);
				//goto FINISH;
				break;
			}
		}
		if(RET_OK != ret) break;
		if(1 == debug){
			dumpMem(gpAREA, gCur_Thread, STR_TYPE_ST_E);
		}
		// 4. read if value
		for(i=0; i<AREA_LEN_DEF; i++){
			gE_Pos = i;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = RET_CHK_AA_E;
				sprintf(gE_Str, "[ERROR] set 0xaa at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d Thread: %d gE_Str: %s\n", __func__, __LINE__, gCur_Thread, gE_Str);
				dumpMem(gE_Str, gCur_Thread, STR_TYPE_POS_E);
				dumpMem(gpAREA, gCur_Thread, STR_TYPE_DUMP_E);
				//goto FINISH;
				break;
			}
		}
		if(RET_OK != ret) break;
	}
//FINISH:
	free(gpAREA);
	#if 0
	free(gpERR_AREA);
	#endif
	return ret;
}

#if 0
//char* setArea(char value)
int setArea(char value)
{
	int ret = 0;
	int i = 0;
	// 3. set to value
	for(i=0; i<AREA_LEN_DEF; i++){
		gpAREA[i] = value;
		if(0 != check(value, i)){
			ret = 1;
			break;
		}
	}
	// 4. read if value
	for(i=0; i<AREA_LEN_DEF; i++){
		if(0 != check(value, i)){
			ret = 1;
			break;
		}
	}
	return ret;
}

//char* check(char value, int no)
int check(char value, int no, c)
{
	char tmp = gpAREA[no];
	int ret = 1;
	if( (value == (tmp & value))
			&& gRelease
			&& 0x0 == (tmp & ~value)){
		ret = 0;
	}
	return ret;
}
#endif
int dumpMem(char *str, int num, int type)
{
	int ret = RET_OK;
	int i = 0;
	FILE *out = NULL;
	char * gE_File = "/sdcard/mem.dump";
	char tmp[4];
	char *head = NULL;

	out = fopen(gE_File, "a+");
	//assert(out != NULL);
	if(out == NULL){
		out = fopen(gE_File, "w+");
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log %s->%3d gCur_Thread: %d out is NULL.\n", __func__, __LINE__, num);
		ret = RET_FILE_OP_E;
	}else{
		__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d gCur_Thread: %d open memcheck.error file ok.\n", __func__, __LINE__, num);
		if(STR_TYPE_ST_E == type){
			fputs("===================== PROGRAM RUNNING. >>>>>>>>>>>>>>>>>>>>>>>\n", out);
		}else if(STR_TYPE_POS_E == type){
			fputs("===================== ERROR.POSTION >>>>>>>>>>>>>>>>>>>>>>>\n", out);
			fputs(str, out);
			fputs("\n", out);
		}else if(STR_TYPE_DUMP_E == type){
			fputs("===================== MEM.DUMP begin >>>>>>>>>>>>>>>>>>>>>>>\n", out);
			sprintf(tmp, "%02d", num);
			fputs("This is the thread ", out);
			fputs(tmp, out);
			fputs(" memory dump area and error postion.\n", out);
			//fputs("gE_Str:\n", out);
			//fputs(gE_Str, out);
			fputs("\nMEM.DUMP AREA:\n", out);
			for(i=0; i<AREA_LEN_DEF; i++){
				sprintf(tmp, "%02x", str[i]);
				__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log %s->%3d tmp: %s", __func__, __LINE__, tmp);
				fputs(tmp, out);
			}
			fputs("\n", out);
			fputs("===================== MEM.DUMP end <<<<<<<<<<<<<<<<<<<<<<<\n\n", out);
		}
	}
	fclose(out);

	return ret;
}
