package com.oterman.njubbs.bean;

public class TopicDetailInfo {
	public String author;
	public String floorth;
	public String pubTime;
	public String content;
	
	
	public String loadMoreUrl;//下一页
	
	public String replyUrl;//回复本文
	
	public TopicDetailInfo() {
		super();
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
