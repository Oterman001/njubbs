package com.oterman.njubbs.bean;

import java.io.Serializable;

public class MailInfo implements Serializable{
	
	public String author;
	public String postTime;
	public String title;
	
	public String contentUrl;
	public boolean hasRead;
	
	//��������
	public String content;
	public String replyUrl;
	public String delUrl;
	
	public  String loadMoreUrl;
	
	public static String totalSpace=null;//������
	public static String totalCount=null;//�ż���
	public static String usedSpace=null;//�õ��Ŀռ�
	
	@Override
	public String toString() {
		return "BBSMail [author=" + author + ", postTime=" + postTime
				+ ", title=" + title + ", contentUrl=" + contentUrl
				+ ", loadMoreUrl=" + loadMoreUrl + "]";
	}

	public MailInfo(String author, String postTime, String title,
			String contentUrl) {
		super();
		this.author = author;
		this.postTime = postTime;
		this.title = title;
		this.contentUrl = contentUrl;
	}

	public MailInfo(String author, String postTime, String title,
			String contentUrl, String loadMoreUrl,boolean hasRead) {
		super();
		this.author = author;
		this.postTime = postTime;
		this.title = title;
		this.contentUrl = contentUrl;
		this.loadMoreUrl = loadMoreUrl;
		this.hasRead=hasRead;
	}
	
	
	public MailInfo(String author, String postTime, String title,
			String content, String replyUrl, String delUrl) {
		super();
		this.author = author;
		this.postTime = postTime;
		this.title = title;
		this.content = content;
		this.replyUrl = replyUrl;
		this.delUrl = delUrl;
	}

	public static String getAvaiSpace(){
		int ava=Integer.parseInt(totalSpace)-Integer.parseInt(usedSpace);
		return ava+"";
	}
	
	
	

}
