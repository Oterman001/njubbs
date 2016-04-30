package com.oterman.njubbs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import android.os.Environment;
import android.os.SystemClock;

public class CacheUtils {
	static  String basePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/CoolMarket/";
	static  String cachePath=basePath+"cache/";
	
	public static void saveToLocal(String fileName,String data){
		File dir=new File(cachePath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		File file=new File(dir,fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter bw=null;
		try {
			bw=new BufferedWriter(new FileWriter(file));
			bw.write(System.currentTimeMillis()+1000*60*2+"");//设置过期时间
			bw.newLine();
			bw.write(data);
			bw.flush();
			LogUtil.d("保存包缓存！"+file.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	/**
	 * 从本地缓存读时  换行符有问题
	 */

	/**
	 * 从本地缓存读取
	 * @param fileName
	 * @return
	 */
	public static String loadFromLocal2(String fileName) {
		try {
			BufferedReader reader=new BufferedReader(new FileReader(new File(cachePath,fileName)));
			StringBuffer sb=new StringBuffer();
			String line=reader.readLine();
			//判断下时间是否过期
			long time = Long.parseLong(line);
			if(System.currentTimeMillis()>time){//过期
				LogUtil.d("缓存存在，但是已经过期");
				return null;
			}
			
			while((line=reader.readLine())!=null){
				sb.append(line);
			}
			reader.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String loadFromLocal(String fileName) {
		try {
			BufferedReader reader=new BufferedReader(new FileReader(new File(cachePath,fileName)));

			String line=reader.readLine();
			//判断下时间是否过期
			long time = Long.parseLong(line);
			if(System.currentTimeMillis()>time){//过期
				LogUtil.d("缓存存在，但是已经过期");
				return null;
			}
			StringWriter sw=new StringWriter();
			while((line=reader.readLine())!=null){
				sw.write(line);
			}
			reader.close();
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
