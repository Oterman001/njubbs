package com.oterman.njubbs.bean;


public class TopTenInfo {
	public String rankth;//����
	public String board;//���
	public String author;//����
	public String title;//����
	public int replyCount;//�ظ�����
	
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
