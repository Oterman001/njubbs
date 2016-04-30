package com.oterman.njubbs.utils;

import android.util.Log;

/**
 * 自定义日志输出，用于控制全局的输出
 * 通过调节current-level的值来控制全局输出
 */
public class LogUtil {
	
	public static final String TAG="com.oterman.coolweather";
	public static final int LEVEL_ALL=0;
	
	public static final int LEVEL_VERBOSE=1;
	public static final int LEVEL_DEBUG=2;
	public static final int LEVEL_INFO=3;
	public static final int LEVEL_WARNING=4;
	public static final int LEVEL_ERROR=5;
	
	public static final int LEVEL_NONE=6;
	
	public static final int CURRNET_LEVEL=LEVEL_ALL;
	
	public static void v(String msg){
		if(CURRNET_LEVEL<=LEVEL_VERBOSE){
			Log.v(TAG, msg);
		}
	}
	public static void d(String msg){
		if(CURRNET_LEVEL<=LEVEL_DEBUG){
			Log.d(TAG, msg);
		}
	}
	public static void i(String msg){
		if(CURRNET_LEVEL<=LEVEL_INFO){
			Log.i(TAG, msg);
		}
	}
	public static void w(String msg){
		if(CURRNET_LEVEL<=LEVEL_WARNING){
			Log.w(TAG, msg);
		}
	}
	public static void e(String msg){
		if(CURRNET_LEVEL<=LEVEL_ERROR){
			Log.e(TAG, msg);
		}
	}
	
	
}
