package com.oterman.njubbs.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.activity.BaseActivity;


public class UiUtils {
	/**
	 * 获取到字符数组 
	 * @param tabNames  字符数组的id
	 */
	public static String[] getStringArray(int tabNames) {
		return getResource().getStringArray(tabNames);
	}

	public static Resources getResource() {
		return BaseApplication.getApplication().getResources();
	}
	public static String getString(int id){
		return getResource().getString(id);
	}
	public static Context getContext(){
		return BaseApplication.getApplication();
	}
	/** dip转换px */
	public static int dip2px(int dip) {
		final float scale = getResource().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/** pxz转换dip */

	public static int px2dip(int px) {
		final float scale = getResource().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
	/**
	 * 把Runnable 方法提交到主线程运行
	 * @param runnable
	 */
	public static void runOnUiThread(Runnable runnable) {
		// 在主线程运行
		if(android.os.Process.myTid()==BaseApplication.getMainTid()){
			runnable.run();
		}else{
			//获取handler  
			BaseApplication.getHandler().post(runnable);
		}
	}

	public static View inflate(int id) {
		return View.inflate(getContext(), id, null);
	}

	public static Drawable getDrawalbe(int id) {
		return getResource().getDrawable(id);
	}
	
	//手工换行 满满四十个字符换行
	public static String addNewLineMark(String  str){
		StringBuffer sb=new StringBuffer(str);
		for (int i = 0; i <sb.length()-40; ) {
			String sub= sb.substring(i, i+40);
			if(sub.contains("\n")){
				int index= sub.lastIndexOf("\n");
				i=i+index+1;
				continue;
			}
			sb.insert(i+40, "\n");
			i+=41;
		}
		return sb.toString();
	}
}
