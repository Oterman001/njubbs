package com.oterman.njubbs;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;

public class BaseApplication extends Application {
	public static BaseApplication application;
	private static int mainTid;
	private static Handler handler;
	@Override
	public void onCreate() {
		super.onCreate();
		application=this;
		mainTid = android.os.Process.myTid();
		handler=new Handler();
	}
	
	public static Context getApplication() {
		return application;
	}

	public static Handler getHandler() {
		return handler;
	}

	public static int getMainTid() {
		return mainTid;
	}
	
}
