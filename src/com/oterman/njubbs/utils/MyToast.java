package com.oterman.njubbs.utils;

import android.widget.Toast;

public class MyToast {
	static boolean showToast=true;
	public static void toast(String text){
		if(showToast){
			Toast.makeText(UiUtils.getContext(), text, 0).show();
		}
	}
}
