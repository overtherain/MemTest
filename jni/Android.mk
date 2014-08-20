LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE := libmemcheck
LOCAL_SRC_FILES:= com_jnitest_memutils_memcheck.c
include $(BUILD_SHARED_LIBRARY)
