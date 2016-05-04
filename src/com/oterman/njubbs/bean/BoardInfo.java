package com.oterman.njubbs.bean;

public class BoardInfo {
	public String rankth;
	public String boardName;
	public String chineseName;
	public String boardUrl;
	public String peopleCount;
	
	public BoardInfo(String rankth, String boardName, String chineseName,
			String boardUrl, String peopleCount) {
		super();
		this.rankth = rankth;
		this.boardName = boardName;
		this.chineseName = chineseName;
		this.boardUrl = boardUrl;
		this.peopleCount = peopleCount;
	}
	public BoardInfo(String rankth, String boardName, String chineseName,
			String boardUrl) {
		super();
		this.rankth = rankth;
		this.boardName = boardName;
		this.chineseName = chineseName;
		this.boardUrl = boardUrl;
	}
	@Override
	public String toString() {
		return "BoardInfo [rankth=" + rankth + ", boardName=" + boardName
				+ ", chineseName=" + chineseName + ", boardUrl=" + boardUrl
				+ ", peopleCount=" + peopleCount + "]";
	}

}
