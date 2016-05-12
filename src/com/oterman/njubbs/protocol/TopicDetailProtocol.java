package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.TopicDetailInfo;


public class TopicDetailProtocol  extends BaseProtocol<TopicDetailInfo>{
	String loadMoreUrl;

	/**
	 * 解析html   
	 * @param doc
	 * @return
	 */
	public List<TopicDetailInfo> parseHtml(Document doc,String url) {
		List<TopicDetailInfo> list = new ArrayList<>();
		
		loadMoreUrl=null;
		Elements aEles = doc.select("a");
		for (int i = aEles.size()-1; i >0 ; i--) {
			Element aEle=aEles.get(i);
			if("本主题下30篇".equals(aEle.text())){
				loadMoreUrl=aEle.attr("href");
			}
		}

		Elements tableEles = doc.select("tbody");
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd HH:mm");
		for (int i = 0; i < tableEles.size(); i++) {
			Elements tdEles = tableEles.get(i).select("td");
			
			int floorth=Integer.parseInt(tdEles.get(1).text());
			
			String str=tdEles.get(2).text();
			
			//指定.可以匹配所有字符 包括行结束符
			Pattern p1=Pattern.compile("发信人:(.+),.*小百合站 \\((.+\\d{4})\\)(.+)--.*",Pattern.DOTALL);
			Matcher matcher = p1.matcher(str);
			if(matcher.find()){
				handleData(list, dateFormat, floorth, matcher);
			}else{//帖子有修改 格式变化
				p1=Pattern.compile("发信人:(.+),.*小百合站 \\((.+\\d{4})\\)(.+)",Pattern.DOTALL);
				matcher = p1.matcher(str);
				
				if(matcher.find()){
					handleData(list, dateFormat, floorth, matcher);
				}
			}
		}
		
		return list;
	}

	private void handleData(
			List<TopicDetailInfo> list,
			SimpleDateFormat dateFormat, 
			int floorth, Matcher matcher) {
		String author=matcher.group(1).trim();
		String pubTime=matcher.group(2).trim();
		pubTime=dateFormat.format(new Date(pubTime));
		
		String content=matcher.group(3).replaceAll("\\[/*uid\\]", "").trim();
		content=content.replaceAll("\\[.*?m", "");
		
		content=content.replaceAll("http.*?(jpg|jpeg|png|JPG|JPEG|PNG|gif|GIF)", "<br><img src=\""+"$0"+"\"/><br>");
		
		TopicDetailInfo info=new TopicDetailInfo(author, floorth+"", pubTime, content, loadMoreUrl);
		list.add(info);
	}


}
