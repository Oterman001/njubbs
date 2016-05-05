package com.oterman.njubbs.utils;

public class Constants {
	public static final String NJU_BBS_BASE_URL = "http://bbs.nju.edu.cn/";
	public static final String TOP_TEN_URL = "http://bbs.nju.edu.cn/bbstop10";
	public static final String HOT_BOARD_ULR = "http://bbs.nju.edu.cn/bbstopb10";
	public static final String TOP_ALL_URL = "http://bbs.nju.edu.cn/bbstopall";
	
	
	public static String getContentUrl(String contentUrl) {
		return NJU_BBS_BASE_URL+contentUrl;
	}


	public static String getBoardUrl(String boardUrl) {
		boardUrl=boardUrl.replaceAll("bbsdoc", "bbstdoc");
		return NJU_BBS_BASE_URL+boardUrl;
	}
}