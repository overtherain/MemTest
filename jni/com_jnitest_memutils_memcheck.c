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

char gE_Str[E_STR_LEN_DEF];
int debug = 1;

JNIEXPORT jstring JNICALL Java_com_jnitest_memutils_memcheck_doTask(JNIEnv *env, jobject obj, jint thread)
{
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log thread: %d doTask=%d\n", thread, doTask(thread));
	return (*env)->NewStringUTF(env, (char*)gE_Str);
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

	int ret = 0;
	char tmp = 0;
	int i = 0;
	// 1. malloc a gpAREA
	__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log Thread: %d Malloc gpAREA and gpERR_AREA.\n", gCur_Thread);
	gpAREA = (char *)malloc(sizeof(char) * AREA_LEN_DEF);
	gpERR_AREA = (char *)malloc(sizeof(char) * AREA_LEN_DEF);
	if(gpAREA == NULL){
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gpAREA is NULL.\n", gCur_Thread);
		ret = 1001;
	}
	if(gpERR_AREA == NULL){
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gpERR_AREA is NULL.\n", gCur_Thread);
		ret = 1002;
	}
	//assert(gpAREA != NULL);
	//assert(gpERR_AREA != NULL);
	// 2. clear it to 0
	memset(gpAREA, 0, sizeof(char)*AREA_LEN_DEF);
	memset(gpERR_AREA, 0, sizeof(char)*AREA_LEN_DEF);

	// 3. set to value
	while(1){
		// 3. set to value
		value = 0x55;
		if(1 == debug){
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log set %02x Thread: %d Begin to set 55 to mem.\n",
						value, gCur_Thread);
		}
		for(i=0; i<AREA_LEN_DEF; i++){
			gpAREA[i] = value;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = 1;
				sprintf(gE_Str, "[ERROR] set 0x55 at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gE_Str: %s\n", gCur_Thread, gE_Str);
				dumpMem(gpAREA, gCur_Thread);
				break;
			}
		}
		if(1 == debug){
			debug = 0;
			dumpMem(gpAREA, gCur_Thread);
		}
		// 4. read if value
		for(i=0; i<AREA_LEN_DEF; i++){
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = 2;
				sprintf(gE_Str, "[ERROR] set 0xaa at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gE_Str: %s\n", gCur_Thread, gE_Str);
				dumpMem(gpAREA, gCur_Thread);
				break;
			}
		}
		value = 0xaa;
		if(1 == debug){
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log set %02x Thread: %d Begin to set aa to mem.\n",
						value, gCur_Thread);
		}
		for(i=0; i<AREA_LEN_DEF; i++){
			gpAREA[i] = value;
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = 3;
				sprintf(gE_Str, "[ERROR] set 0x55 at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d, gE_Str: %s\n", gCur_Thread, gE_Str);
				dumpMem(gpAREA, gCur_Thread);
				break;
			}
		}
		if(1 == debug){
			dumpMem(gpAREA, gCur_Thread);
		}
		// 4. read if value
		for(i=0; i<AREA_LEN_DEF; i++){
			tmp = gpAREA[i];
			if( (value == (tmp & value))
					&& gRelease
					&& 0x0 == (tmp & ~value)){
			}else{
				ret = 4;
				sprintf(gE_Str, "[ERROR] set 0xaa at %04d, value: %02x", gE_Pos, gE_Val);
				__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log Thread: %d gE_Str: %s\n", gCur_Thread, gE_Str);
				dumpMem(gpAREA, gCur_Thread);
				break;
			}
		}
	}
	free(gpAREA);
	free(gpERR_AREA);
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
int dumpMem(char *str, int num)
{
	int ret = 0;
	int i = 0;
	FILE *out = NULL;
	char * gE_File = "/sdcard/mem.dump";
	char tmp[4];
	char *head = NULL;

	out = fopen(gE_File, "a+");
	//assert(out != NULL);
	if(out == NULL){
		out = fopen(gE_File, "w+");
		__android_log_print(ANDROID_LOG_ERROR, TAG, "gdz.log dumpMem-> gCur_Thread: %d out is NULL.\n", num);
		ret = 1;
	}else{
		__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log dumpMem-> gCur_Thread: %d open memcheck.error file ok.\n", num);
		fputs("===================== MEM.DUMP begin >>>>>>>>>>>>>>>>>>>>>>>\n", out);
		sprintf(tmp, "%02d", num);
		fputs("This is the thread ", out);
		fputs(tmp, out);
		fputs(" memory dump area and error postion.\n", out);
		fputs("gE_Str:\n", out);
		fputs(gE_Str, out);
		fputs("\ngpERR_AREA:\n", out);
		for(i=0; i<AREA_LEN_DEF; i++){
			sprintf(tmp, "%02x", str[i]);
			__android_log_print(ANDROID_LOG_DEBUG, TAG, "gdz.log dumpMem-> tmp: %s", tmp);
			fputs(tmp, out);
		}
		fputs("\n", out);
		fputs("===================== MEM.DUMP end <<<<<<<<<<<<<<<<<<<<<<<\n\n", out);
	}
	fclose(out);

	return ret;
}
