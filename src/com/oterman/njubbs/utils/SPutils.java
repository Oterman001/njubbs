package com.oterman.njubbs.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
}
