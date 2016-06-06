package com.oterman.njubbs.protocol;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.TopicDetailInfo;

public class TopicReDetailProtocol  {
	public TopicDetailInfo loadDataFromServer(String url) {
		Document doc;
		TopicDetailInfo info=null;
		try {
			doc = Jsoup.connect(url).get();
			//获取同主题阅读的链接
			Elements aEles = doc.select("a");
			String rootUrl=null;
			for (int i = aEles.size()-1; i > 0; i--) {
				String text = aEles.get(i).text();
				if("同主题阅读".equals(text)){
					rootUrl=aEles.get(i).attr("href");
				}
			}
			
			Elements tableEles = doc.select("tbody");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			for (int i = 0; i < tableEles.size(); i++) {
				Elements tdEles = tableEles.get(i).select("td");
				
				String str=tdEles.get(0).text();
				
				Pattern p1=Pattern.compile("发信人:(.+),.*?信区:(.*?)\\..*?题:(.*?)发信站.*小百合站 \\((.+\\d{4})\\)(.+)--.*",Pattern.DOTALL);
				Matcher matcher = p1.matcher(str);
				if(matcher.find()){
					String author=matcher.group(1).trim();
					String board=matcher.group(2).trim();
					String title=matcher.group(3).trim();
					String pubTime=matcher.group(4).trim();
					pubTime=dateFormat.format(new Date(pubTime));
					
					String content=matcher.group(5).replaceAll("\\[/*uid\\]", "").trim();
					content=content.replaceAll("\\[.*?m", "");
					if(content.contains("中提到: 】")){
						String temp="中提到: 】";
						int index= content.indexOf(temp);
						content=content.substring(0, index+temp.length());
					}
					content=content.replaceAll("http.*?(jpg|jpeg|png|JPG|JPEG|PNG|gif|GIF)", "<br><img src=\""+"$0"+"\"/><br>");
					content=content.replaceAll("\\n", "<br>");
					
					info=new TopicDetailInfo(pubTime, content, title, rootUrl);
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
		
	}
}
