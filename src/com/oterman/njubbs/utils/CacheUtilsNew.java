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

public class CacheUtilsNew {
	static  String basePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/";
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
	
	
	public static String loadFromLocal(String fileName) {
		try {
			BufferedReader reader=new BufferedReader(new FileReader(new File(cachePath,fileName)));

			String line=null;
			StringWriter sw=new StringWriter();
			while((line=reader.readLine())!=null){
				sw.write(line);
			}
			reader.close();
			LogUtil.d("读取缓存成功！");
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
