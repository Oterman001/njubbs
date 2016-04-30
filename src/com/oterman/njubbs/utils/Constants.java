package com.oterman.njubbs.utils;

public class Constants {
	public static final String SERVER_URL="http://127.0.0.1:8090";
	public static final String TOP_TEN_URL = "http://bbs.nju.edu.cn/bbstop10";
	
	public static String getHomeUrl(int index){
		
		return SERVER_URL+"/home?index="+index;
	}

	/**
	 * 得到app icon的地址
	 * http://127.0.0.1:8080/image?name=app/com.youyuan.yyhl/icon.jpg
	 */
	public static String getAppIcon(String iconUrl) {
		
		return SERVER_URL+"/image?name="+iconUrl;
	}

	public static String getDataUrl(int index, String key) {
		return SERVER_URL+"/"+key+"?index="+index;
	}

	/**
	 * http://127.0.0.1:8090/image?name=image/xxx.jpg
	 * @param url
	 * @return
	 */
	public static String getSubjectPicUrl(String url) {
		return SERVER_URL+"/image?name="+url;
	}

	public static String getPicUrl(String url) {
		return SERVER_URL+"/image?name="+url;
	}
}
