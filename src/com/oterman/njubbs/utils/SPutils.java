package com.oterman.njubbs.utils;

import com.oterman.njubbs.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class SPutils {
	static SharedPreferences sp;
	static{
		sp=UiUtils.getContext().getSharedPreferences("bbs_config", Context.MODE_PRIVATE);
	}

	public static void saveToSP(String key,String value){
		
		Editor editor = sp.edit();
		
		editor.putString(key, value);
		
		editor.commit();
		
	}
	
	public static String getFromSP(String key){
		
		return sp.getString(key, "");
	}
/*
 [1;37m¹þ¹þ¹þ[m  #000000
[1;31m¹þ¹þ¹þ[m  #E00000
[1;32m¹þ¹þ¹þ[m  #008000
[1;33m¹þ¹þ¹þ[m  #808000
[1;34m¹þ¹þ¹þ[m  #0000FF
[1;35m¹þ¹þ¹þ[m  #D000D0
[1;36m¹þ¹þ¹þ[m  #33A0A0
 * 
 * 	
 */
	//"\n-\n"+"sent from Ð¡°ÙºÏ\n";
	public static String getTail(){
		String tail=getFromSP("tail");
		String tailColor=getFromSP("tail_color");
		//[1;32m·¢ËÍ×Ô ÎÒµÄÐ¡°ÙºÏAndroid¿Í»§¶Ë by PE-TL20©Y[m
		switch (tailColor) {
		case "color_1":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;37mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;37mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_2":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;31mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;31mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_3":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;32mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;32mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_4":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;33mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;33mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_5":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;34mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;34mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_6":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;35mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;35mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		case "color_7":
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;36mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;36mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}

		default://Ä¬ÈÏ×ÏÉ«
			if(!TextUtils.isEmpty(tail)){
				return "\n-\n"+"[1;35mSent From "+tail+"[m\n";
			}else{
				String model = android.os.Build.MODEL;
				return "\n-\n[1;35mSent From ÄÏ´óÐ¡°ÙºÏ by "+model+"[m\n";
			}
		}
		
		

	}
/*
[1;31m¹þ¹þ¹þ[m  #E00000  
[1;32m¹þ¹þ¹þ[m  #008000
[1;33m¹þ¹þ¹þ[m  #808000
[1;34m¹þ¹þ¹þ[m  #0000FF
[1;35m¹þ¹þ¹þ[m  #D000D0
[1;36m¹þ¹þ¹þ[m  #33A0A0 
[1;37m¹þ¹þ¹þ[m  #000000  ºÚ
	
*/
	
	
	
	
	public static String getTailNoColor(){
		String tail=getFromSP("tail");
		if(!TextUtils.isEmpty(tail)){
			return "Sent From "+tail;
		}else{
			String model = android.os.Build.MODEL;
			LogUtil.d(model);
			return "Sent From ÄÏ´óÐ¡°ÙºÏ  by "+model;
		}
	}

	public static String getAdTail() {
		String tail = getFromSP("mail_tail");
		if("no".equals(tail)){//²»¼Ó
			return "";
		}else{//¼ÓÉÏÕ¾ÄÚÐ¡Î²°Í
			return "\n-\n¸ÃÕ¾ÄÚÐÅ×Ô¶¯·¢ËÍ×ÔÄÏ´óÐ¡°ÙºÏ°²×¿¿Í»§¶Ë\n¿Í»§¶ËÏêÇé£ºhttp://bbs.nju.edu.cn/bbstcon?board=Pictures&file=M.1465807881.A";
		}
	}
}
