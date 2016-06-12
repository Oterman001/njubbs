package com.oterman.njubbs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Display;
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
				if(line.getBytes("gbk").length==78||line.getBytes("gbk").length==79||line.getBytes("gbk").length==39){
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
		
		String reg="\n*http://.*?jpg\n*";
		
		Pattern p=Pattern.compile(reg);
		
		Matcher matcher = p.matcher(str);
		
		while(matcher.find()){
			urlList.add(matcher.group());
		}
		
		str=str.replaceAll(reg, "#@");
		
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
		return result;
	}
	
	/**
	 * 将url转换为压缩的bitmap;
	 * @param activity
	 * @param url
	 * @return
	 */
	public static Bitmap parseUriToBm(Activity activity,String url) {
		
		System.out.println("选中图片地址：" + url);

		// 解析图片时需要使用到的参数都封装在这个对象里了
		Options opt = new Options();
		// 不为像素申请内存，只获取图片宽高
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, opt);
		// 拿到图片宽高
		int imageWidth = opt.outWidth;
		int imageHeight = opt.outHeight;

		Display dp = activity.getWindowManager()
				.getDefaultDisplay();
		
		// 拿到屏幕宽高
		int screenWidth = dp.getWidth() / 2;
		int screenHeight = dp.getHeight() / 2;

		// 计算缩放比例
		int scale = 1;
		int scaleWidth = imageWidth / screenWidth;
		int scaleHeight = imageHeight / screenHeight;
		if (scaleWidth >= scaleHeight && scaleWidth >= 1) {
			scale = scaleWidth;
		} else if (scaleWidth < scaleHeight && scaleHeight >= 1) {
			scale = scaleHeight;
		}

		// 设置缩放比例
		opt.inSampleSize = scale;
		opt.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(url, opt);
		
		return bitmap;
	}

	//将bitmap缓存到本地
	public static  void saveBitmapToLocal(Bitmap bitmap, String filename) {
		String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/photo/";
		File dirFile=new File(dirPath);
		if(!dirFile.exists())
			dirFile.mkdirs();
		
		FileOutputStream fos=null;
		
		try {
			fos=new FileOutputStream(dirPath+filename);
			bitmap.compress(CompressFormat.JPEG, 90, fos);
			
			fos.flush();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	  
	
	
}
