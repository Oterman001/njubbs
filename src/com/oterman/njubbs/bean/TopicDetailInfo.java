package com.oterman.njubbs.bean;

import java.io.Serializable;

public class TopicDetailInfo implements Serializable{
	public String author;
	public String floorth;
	public String pubTime;
	public String content;
	
	public String board;
	public String title;
	
	public String rootUrl;//同主题的url;
	
	public String loadMoreUrl;//下一页
	public String replyUrl;//回复本文
	
	public TopicDetailInfo() {
		super();
	}





	public TopicDetailInfo(String pubTime, String content, String title,
			String rootUrl) {
		super();
		this.pubTime = pubTime;
		this.content = content;
		this.title = title;
		this.rootUrl = rootUrl;
	}












	public TopicDetailInfo(String author, String floorth, String pubTime,
			String content, String loadMoreUrl, String replyUrl) {
		super();
		this.author = author;
		this.floorth = floorth;
		this.pubTime = pubTime;
		this.content = content;
		this.loadMoreUrl = loadMoreUrl;
		this.replyUrl = replyUrl;
	}

	@Override
	public String toString() {
		return "TopicDetailInfo [author=" + author + ", floorth=" + floorth
				+ ", pubTime=" + pubTime + ", content=" + content
				+ ", loadMoreUrl=" + loadMoreUrl + ", replyUrl=" + replyUrl
				+ "]";
	}

}
