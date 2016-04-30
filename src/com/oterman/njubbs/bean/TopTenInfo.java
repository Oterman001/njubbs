package com.oterman.njubbs.bean;


public class TopTenInfo {
	public String rankth;//排名
	public String board;//版块
	public String author;//作者
	public String title;//标题
	public int replyCount;//回复数量
	
	public String boardUrl;
	public String contentUrl;
	public String authorUrl;
	
	
	public TopTenInfo() {
		super();
	}

	
	

	public TopTenInfo(String rankth, String board, String author, String title,
			int replyCount, String boardUrl, String contentUrl, String authorUrl) {
		super();
		this.rankth = rankth;
		this.board = board;
		this.author = author;
		this.title = title;
		this.replyCount = replyCount;
		this.boardUrl = boardUrl;
		this.contentUrl = contentUrl;
		this.authorUrl = authorUrl;
	}

	@Override
	public String toString() {
		return "TopTenInfo [rankth=" + rankth + ", board=" + board
				+ ", author=" + author + ", title=" + title + ", replyCount="
				+ replyCount + ", boardUrl=" + boardUrl + ", contentUrl="
				+ contentUrl + ", authorUrl=" + authorUrl + "]";
	}



	
	
	
}
