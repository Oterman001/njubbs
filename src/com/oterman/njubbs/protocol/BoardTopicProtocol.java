package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.TopicInfo;


public class BoardTopicProtocol {

	/**
	 * 从服务器加载十大数据，解析并返回
	 */
	public List<TopicInfo> loadFromServer(String url) {
		List<TopicInfo> list = null;
		try {
			Document doc = Jsoup.connect(url).get();

			if(doc!=null){
				list=parseHtml(doc,url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 解析html   
	 * @param doc
	 * @return
	 */
	public List<TopicInfo> parseHtml(Document doc,String url) {
		List<TopicInfo> list = new ArrayList<>();
		Elements aEles = doc.select("a");
		String loadMoreUrl=null;
		for (Element element : aEles) {
			if(element.text().equals("上一页")){
				loadMoreUrl=element.attr("href");
			}
		}
		
		Elements trEles = doc.select("tr");
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
		for (int i = 1; i < trEles.size(); i++) {
			Elements tdEles = trEles.get(i).select("td");
			
			String id=tdEles.get(0).text();
			String author=tdEles.get(2).text();
			String pubTime=tdEles.get(3).text()+" 2016";
			pubTime=dateFormat.format(new Date(pubTime));
			
			String title=tdEles.get(4).text();
			title=title.replaceAll("○ ", "");
			String contentUrl=tdEles.get(4).select("a").get(0).attr("href");
			
			String reply=tdEles.get(5).text();
			
			TopicInfo info=new TopicInfo(null, author, title, id, loadMoreUrl, pubTime, contentUrl, reply, url);
			list.add(info);
		}
		
		return list;
	}



}
