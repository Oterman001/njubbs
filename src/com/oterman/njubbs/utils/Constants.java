package com.oterman.njubbs.utils;

public class Constants {
	public static final String NJU_BBS_BASE_URL = "http://bbs.nju.edu.cn/";
	public static final String TOP_TEN_URL = "http://bbs.nju.edu.cn/bbstop10";
	public static final String HOT_BOARD_ULR = "http://bbs.nju.edu.cn/bbstopb10";
	public static final String TOP_ALL_URL = "http://bbs.nju.edu.cn/bbstopall";
	public static final String LOGIN_URL = "http://bbs.nju.edu.cn/bbslogin?type=2";
	public static final String USER_QUERY_URL = "http://bbs.nju.edu.cn/bbsqry?userid=";
	public static final String BBSLEFT_URL = "http://bbs.nju.edu.cn/bbsleft";
	public static final String BBS_NEW_TOPIC_URL = "http://bbs.nju.edu.cn/bbssnd?board=";
	
	
	public static String getContentUrl(String contentUrl) {
		//  /bbstcon?board=Pictures&file=M.1463379554.A
		//   bbstcon?board=Pictures&file=M.1463582774.A
		return NJU_BBS_BASE_URL+contentUrl;
	}


	public static String getBoardUrl(String boardUrl) {
		boardUrl=boardUrl.replaceAll("bbsdoc", "bbstdoc");
		return NJU_BBS_BASE_URL+boardUrl;
	}
	
	
	public static String getUserUrl(String id) {
		return USER_QUERY_URL+id;
	}
	
	public static String getNewTopicUrl(String boardname){
		return BBS_NEW_TOPIC_URL+boardname;
	}
	
	public static String getTopicDelUrl(String contentUrl){
		//  /bbsdel?board=Pictures&file=M.1463450113.A
		//contentUrl:/bbstcon?board=Pictures&file=M.1463379554.A   
		String substring = contentUrl.substring(contentUrl.indexOf("?")+1);
		
		return NJU_BBS_BASE_URL+"bbsdel?"+substring;
	}


	public static String getUploadUrl() {
		return NJU_BBS_BASE_URL+"bbsdoupload";
	}
	
	public static String getReplyDelUrl(String replyUrl){
		// replyUrl   bbspst?board=WorldFootball&amp;file=M.1462286742.A
		//bbsdel?board=WorldFootball&amp;file=M.1462286742.A
		String str=replyUrl.substring(replyUrl.indexOf("?")+1);
		
		return NJU_BBS_BASE_URL+"bbsdel?"+str;
	}

	public static String getReplyPageUrl(String contentUrl) {
		//bbstcon?board=Pictures&file=M.1463582774.A
		
		//bbspst?board=Pictures&file=M.1463582774.A
		String url="bbspst?"+contentUrl.substring(contentUrl.lastIndexOf("?")+1);
		
		return NJU_BBS_BASE_URL+url;
	}
	
	
}
