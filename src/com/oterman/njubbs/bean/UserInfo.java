package com.oterman.njubbs.bean;

import java.io.Serializable;

/**
 <td id="NET_1"><textarea id="NET-1">IFme (Mico) 共上站 682 次，发表文章 70 篇
[处女座]上次在 [Tue May 10 21:31:32 2016] 从 [112.21.166.24] 到本站一游。
信箱：[⊙]  经验值：[562](中级站友) 表现值：[38](很好) 生命力：[368]。
目前不在站上, 上次离站时间 [(不详)]

没有个人说明档

 *
 */
public class UserInfo implements Serializable{
	
	public String id;
	public String nickname;
	public String totalVisit;
	public String totalPub;//发表文章
	public String xingzuo;
	public String lastVisitTime;
	public String lastVistiIP;
	public String jingyan;
	public String biaoxian;
	public String life;
	public boolean isOnline;
	
	public String gender;
	
	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserInfo(String id, String nickname, String totalVisit,
			String totalPub, String xingzuo, String lastVisitTime,
			String lastVistiIP, String jingyan, String biaoxian, String life,
			boolean isOnline) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.totalVisit = totalVisit;
		this.totalPub = totalPub;
		this.xingzuo = xingzuo;
		this.lastVisitTime = lastVisitTime;
		this.lastVistiIP = lastVistiIP;
		this.jingyan = jingyan;
		this.biaoxian = biaoxian;
		this.life = life;
		this.isOnline = isOnline;
	}

	
	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", nickname=" + nickname
				+ ", totalVisit=" + totalVisit + ", totalPub=" + totalPub
				+ ", xingzuo=" + xingzuo + ", lastVisitTime=" + lastVisitTime
				+ ", lastVistiIP=" + lastVistiIP + ", jingyan=" + jingyan
				+ ", biaoxian=" + biaoxian + ", life=" + life + ", isOnline="
				+ isOnline + ", gender=" + gender + "]";
	}
	
	public UserInfo(String id, String nickname, String totalVisit,
			String totalPub, String xingzuo, String lastVisitTime,
			String lastVistiIP, String jingyan, String biaoxian, String life,
			boolean isOnline, String gender) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.totalVisit = totalVisit;
		this.totalPub = totalPub;
		this.xingzuo = xingzuo;
		this.lastVisitTime = lastVisitTime;
		this.lastVistiIP = lastVistiIP;
		this.jingyan = jingyan;
		this.biaoxian = biaoxian;
		this.life = life;
		this.isOnline = isOnline;
		this.gender = gender;
	}
	
	
	
	

}
