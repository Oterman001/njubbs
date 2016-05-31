package com.oterman.njubbs.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Constants {
	public static final String NJU_BBS_BASE_URL = "http://bbs.nju.edu.cn/";
	public static final String TOP_TEN_URL = "http://bbs.nju.edu.cn/bbstop10";
	public static final String HOT_BOARD_ULR = "http://bbs.nju.edu.cn/bbstopb10";
	public static final String TOP_ALL_URL = "http://bbs.nju.edu.cn/bbstopall";
	public static final String LOGIN_URL = "http://bbs.nju.edu.cn/bbslogin?type=2";
	public static final String USER_QUERY_URL = "http://bbs.nju.edu.cn/bbsqry?userid=";
	public static final String BBSLEFT_URL = "http://bbs.nju.edu.cn/bbsleft";
	public static final String BBS_NEW_TOPIC_URL = "http://bbs.nju.edu.cn/bbssnd?board=";
	public static final String BBS_MAIL_URL = "http://bbs.nju.edu.cn/bbsmail";
	public static final String REPLY_MAIL_URL = "http://bbs.nju.edu.cn/bbssndmail";
	public static final String ALL_BOARDS_URL = "http://bbs.nju.edu.cn/bbsall";
	public static final String FAV_BOARD_URL = "http://bbs.nju.edu.cn/bbsmybrd?type=1&confirm1=1";
	public static final String BBS_FRIEND_ALL_URL = "http://bbs.nju.edu.cn/bbsfall";
	public static final String BBS_ADD_FRIEND_URL = "http://bbs.nju.edu.cn/bbsfadd";
	public static final String BBS_FRIEND_DEL_URL = "http://bbs.nju.edu.cn/bbsfdel";
	public static final String BBS_QUERY_USER_URL = "http://bbs.nju.edu.cn/bbsqry?wild=on&userid=";
	public static final String QUERY_TOPIC_URL = "http://bbs.nju.edu.cn/bbsfind";
	public static final String HAS_NEW_MAIL_URL = "http://bbs.nju.edu.cn/bbsnewmail";
	
	
	public static String getQueryUserUrl(String userid,boolean isnick) {
		try {
			userid=URLEncoder.encode(userid,"gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(isnick){//Í¨¹ýêÇ³Æ
			return BBS_QUERY_USER_URL+userid+"&nick=on";
		}else{
			return BBS_QUERY_USER_URL+userid;
		}
		
	}
	
	public static String  getQueryIpUrl(String ip){
		return "http://test.ip138.com/query/?ip="+ip+"&datatype=text";
	}
	
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


	public static String getModifyReplyUrl() {
		return NJU_BBS_BASE_URL+"bbsedit";
	}


	public static String getMailMoreUrl(String moreUrl) {
		return NJU_BBS_BASE_URL+moreUrl;
	}


	public static String getMailContentUrl(String contentUrl) {
		// TODO Auto-generated method stub
		return NJU_BBS_BASE_URL+contentUrl;
	}


	public static String getMailDelUrl(String delUrl) {
		// bbsdelmail?file=M.1463837094.A
		return NJU_BBS_BASE_URL+delUrl;
	}
	
	
}
