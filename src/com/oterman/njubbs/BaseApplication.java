package com.oterman.njubbs;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.view.View;

public class BaseApplication extends Application {
	public static BaseApplication application;
	private static int mainTid;
	private static Handler handler;
	public static boolean isLogin=false;//Ĭ��Ϊ�ǵ�½
	@Override
	public void onCreate() {
		super.onCreate();
		application=this;
		mainTid = android.os.Process.myTid();
		handler=new Handler();
		
        //����Ĭ�ϵ�ImageLoader���ò���  
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration  
                .createDefault(this);  
          
        //Initialize ImageLoader with configuration.  
        ImageLoader.getInstance().init(configuration);  
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
