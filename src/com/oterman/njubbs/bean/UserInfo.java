package com.oterman.njubbs.bean;

import java.io.Serializable;

/**
 <td id="NET_1"><textarea id="NET-1">IFme (Mico) ����վ 682 �Σ��������� 70 ƪ
[��Ů��]�ϴ��� [Tue May 10 21:31:32 2016] �� [112.21.166.24] ����վһ�Ρ�
���䣺[��]  ����ֵ��[562](�м�վ��) ����ֵ��[38](�ܺ�) ��������[368]��
Ŀǰ����վ��, �ϴ���վʱ�� [(����)]

û�и���˵����

 *
 */
public class UserInfo implements Serializable{
	
	public String id;
	public String nickname;
	public String totalVisit;
	public String totalPub;//��������
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
