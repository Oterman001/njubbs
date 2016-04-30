package com.oterman.njubbs.bean;

public class TopTenInfo {
	public int rankth;//����
	public String board;//���
	public String author;//����
	public String title;//����
	public int replyCount;//�ظ�����
	
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
