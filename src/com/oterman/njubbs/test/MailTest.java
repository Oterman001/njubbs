package com.oterman.njubbs.test;

import org.jsoup.Jsoup;

import com.lidroid.xutils.HttpUtils;
import com.oterman.njubbs.BaseApplication;

public class MailTest {
	
	public void testMail(){
		BaseApplication.autoLogin(null,true);
		String cookie=BaseApplication.getCookie();
		
//		Jsoup.connect("http://bbs.nju.edu.cn/bbsmailcon?file=M.1463704322.A&num=74").cookies();
		
		
	}

}
