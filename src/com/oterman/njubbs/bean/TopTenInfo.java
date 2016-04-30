package com.oterman.njubbs.bean;

public class TopTenInfo {
	public int rankth;//排名
	public String board;//版块
	public String author;//作者
	public String title;//标题
	public int replyCount;//回复数量
	
	public TopTenInfo() {
		super();
	}

	public TopTenInfo(int rankth, String board, String author, String title,
			int replyCount) {
		super();
		this.rankth = rankth;
		this.board = board;
		this.author = author;
		this.title = title;
		this.replyCount = replyCount;
	}



	@Override
	public String toString() {
		return "TopTenInfo [rankth=" + rankth + ", board=" + board
				+ ", author=" + author + ", title=" + title + ", replyCount="
				+ replyCount + "]";
	}
	
	
	
}
