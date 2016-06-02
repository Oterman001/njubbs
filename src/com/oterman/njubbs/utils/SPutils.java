package com.oterman.njubbs.utils;

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
	
	//"\n-\n"+"sent from 小百合\n";
	public static String getTail(){
		String tail=getFromSP("tail");
		if(!TextUtils.isEmpty(tail)){
			return "\n-\n"+"<font color='purple'>Sent From  "+tail+"</font>\n";
		}else{
			return "\n-\n <font color='purple'>Sent From 小百合</font>\n";
		}
	}
	
	public static String getTailNoColor(){
		String tail=getFromSP("tail");
		if(!TextUtils.isEmpty(tail)){
			return "Sent From  "+tail;
		}else{
			return "Sent From 小百合";
		}
	}
}
