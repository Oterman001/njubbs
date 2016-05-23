package com.oterman.njubbs.bean;

public class BoardInfo {
	public String rankth;
	
	public int id;
	public String boardName;
	public String category;
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
	
	
	public BoardInfo(int id, String boardName, String category,
			String chineseName, String boardUrl) {
		super();
		this.id = id;
		this.boardName = boardName;
		this.category = category;
		this.chineseName = chineseName;
		this.boardUrl = boardUrl;
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
		return "BoardInfo [rankth=" + rankth + ", id=" + id + ", boardName="
				+ boardName + ", category=" + category + ", chineseName="
				+ chineseName + ", boardUrl=" + boardUrl + ", peopleCount="
				+ peopleCount + "]";
	}

}
