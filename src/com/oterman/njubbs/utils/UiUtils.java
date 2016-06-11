package com.oterman.njubbs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.oterman.njubbs.BaseApplication;


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
	
	public static String deleteNewLineMark(String originStr){
		BufferedReader br=new BufferedReader(new StringReader(originStr));
		String line=null;
		StringBuffer sb=new StringBuffer();
		try {
			while((line=br.readLine())!=null){
				sb.append(line);
				if(line.getBytes("gbk").length==78||line.getBytes("gbk").length==79){
					continue;
				}else{
					sb.append("\n");
				}			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//手工换行 满满四十个字符换行
	public static String addNewLineMark(String  str){
		List<String> urlList=new ArrayList<String>();
		
		String reg="\nhttp://.*?jpg";
		
		Pattern p=Pattern.compile(reg);
		
		Matcher matcher = p.matcher(str);
		
		while(matcher.find()){
			urlList.add(matcher.group());
		}
		
		str=str.replaceAll(reg, "\n#@\n");
		
		StringBuffer sb=new StringBuffer(str);
		for (int i = 0; i <sb.length()-39; ) {
			String sub= sb.substring(i, i+39);
			if(sub.contains("\n")){
				int index= sub.lastIndexOf("\n");
				i=i+index+1;
				continue;
			}
			sb.insert(i+39, "\n");
			i+=40;
		}
		
		p=Pattern.compile("#@");
		matcher=p.matcher(sb.toString());
		String result=sb.toString();
		int i=0;
		while(matcher.find()){
			result=result.replaceFirst("#@",urlList.get(i++));
		}
		
//		return sb.toString();
		return result;
	}
}
