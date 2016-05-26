package com.oterman.njubbs.bean;

public class FriendInfo {

	public String rankth;
	public String id;
	public String desc;//±¸×¢
	
	public UserInfo userInfo;
	
	@Override
	public String toString() {
		return "BBSFriend [rankth=" + rankth + ", id=" + id + ", desc=" + desc
				+ "]";
	}
	public FriendInfo(String rankth, String id, String desc) {
		super();
		this.rankth = rankth;
		this.id = id;
		this.desc = desc;
	}
	
}
