package com.oterman.njubbs.protocol;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.utils.Constants;

public class UserProtocol {
	
	public UserInfo getUserInfoFromServer(String userId){
		
		Document doc;
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy��MM��dd�� HH:mm:ss");
		try {
			doc = Jsoup.connect(Constants.getUserUrl(userId)).get();
			String result=doc.select("td").get(0).text();
			result=result.replaceAll("\\[.*?m","");
			/*
			IFme (Mico) ����վ 682 �Σ��������� 70 ƪ
			[��Ů��]�ϴ��� [Tue May 10 21:31:32 2016] �� [112.21.166.24] ����վһ�Ρ�
			���䣺[��]  ����ֵ��[562](�м�վ��) ����ֵ��[38](�ܺ�) ��������[368]��
			Ŀǰ����վ��, �ϴ���վʱ�� [(����)]
			 */
			
			Pattern p=Pattern.compile("(.*?)\\((.*?)\\).*?����վ(.*?)��.*?����(.*?)ƪ.*?"+
										"\\[(.*?)\\].*?\\[(.*?)\\].*?\\[(.*?)\\].*?"+
										"����ֵ��(.*?)����ֵ��(.*?)��������(.*?)��.*"
										,Pattern.DOTALL);
			Matcher m = p.matcher(result);
			
			if(m.find()){
				String id = m.group(1).trim();
				String nickname=m.group(2).trim();
				String totalVisit=m.group(3).trim();
				String totalPub=m.group(4).trim();
				String xingzuo=m.group(5).trim();
				String lastVisitTime=m.group(6).trim();
				String lastVisitIP=m.group(7).trim();
				String jingyan=m.group(8).trim();
				String biaoxian=m.group(9).trim();
				String life=m.group(10).trim();
				
				if(lastVisitIP==null||lastVisitIP.length()==0){
					lastVisitIP=lastVisitTime;
					lastVisitTime=xingzuo;
					xingzuo="δ֪";
				}
				
				lastVisitTime=dateFormat.format(new Date(lastVisitTime));
		
				UserInfo info=new UserInfo(id, nickname, totalVisit, totalPub, xingzuo, lastVisitTime, lastVisitIP, jingyan, biaoxian, life, false);
				if(result.contains("Ŀǰ��վ��")){
					info.isOnline=true;
				}else{
					info.isOnline=false;
				}
				return info;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
}
