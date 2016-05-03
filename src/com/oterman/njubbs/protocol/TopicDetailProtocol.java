package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.TopicDetailInfo;


public class TopicDetailProtocol {

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public List<TopicDetailInfo> loadFromServer(String url) {
		List<TopicDetailInfo> list = null;
		try {
			Document doc = Jsoup.connect(url).get();

			if(doc!=null){
				list=parseHtml(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * ����html   
	 * @param doc
	 * @return
	 */
	public List<TopicDetailInfo> parseHtml(Document doc) {
		List<TopicDetailInfo> list = new ArrayList<>();
		Elements tableEles = doc.select("tbody");
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd HH:mm");
		for (int i = 0; i < tableEles.size(); i++) {
			Elements tdEles = tableEles.get(i).select("td");
			
			int floorth=Integer.parseInt(tdEles.get(1).text());
			
			String str=tdEles.get(2).text();
			
			//ָ��.����ƥ�������ַ� �����н�����
			Pattern p1=Pattern.compile("������:(.+),.*С�ٺ�վ \\((.+\\d{4})\\)(.+)--.*",Pattern.DOTALL);
			Matcher matcher = p1.matcher(str);
			if(matcher.find()){
				handleData(list, dateFormat, floorth, matcher);
			}else{//�������޸� ��ʽ�仯
				p1=Pattern.compile("������:(.+),.*С�ٺ�վ \\((.+\\d{4})\\)(.+)",Pattern.DOTALL);
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
		TopicDetailInfo info=new TopicDetailInfo(author, floorth+"", pubTime, content);
		list.add(info);
	}


}
