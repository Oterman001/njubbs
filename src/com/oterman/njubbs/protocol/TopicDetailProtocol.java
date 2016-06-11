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
import com.oterman.njubbs.utils.UiUtils;


public class TopicDetailProtocol  extends BaseProtocol<TopicDetailInfo>{
	String loadMoreUrl;

	/**
	 * ½âÎöhtml   
	 * @param doc
	 * @return
	 */
	public List<TopicDetailInfo> parseHtml(Document doc,String url) {
		List<TopicDetailInfo> list = new ArrayList<>();
		
		loadMoreUrl=null;
		Elements aEles = doc.select("a");
		for (int i = aEles.size()-1; i >0 ; i--) {
			Element aEle=aEles.get(i);
			if("±¾Ö÷ÌâÏÂ30Æª".equals(aEle.text())){
				loadMoreUrl=aEle.attr("href");
			}
		}

		Elements tableEles = doc.select("tbody");
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		for (int i = 0; i < tableEles.size(); i++) {
			Elements tdEles = tableEles.get(i).select("td");
			
			//»Ø¸´±¾ÎÄÁ´½Ó
			Element aEle = tdEles.get(0).select("a").get(1);
			String replyUrl= aEle.attr("href");
			
			int floorth=Integer.parseInt(tdEles.get(1).text());
			
			String str=tdEles.get(2).text();
			
			//Ö¸¶¨.¿ÉÒÔÆ¥ÅäËùÓÐ×Ö·û °üÀ¨ÐÐ½áÊø·û
			Pattern p1=Pattern.compile("·¢ÐÅÈË:(.+?),.*Ð¡°ÙºÏÕ¾ \\s*\\((.+?\\d{4})\\)*(.+)--.*",Pattern.DOTALL);
			Matcher matcher = p1.matcher(str);
			if(matcher.find()){
				handleData(list, dateFormat, floorth, matcher,replyUrl);
			}else{//Ìû×ÓÓÐÐÞ¸Ä ¸ñÊ½±ä»¯
				p1=Pattern.compile("·¢ÐÅÈË:(.+?),.*Ð¡°ÙºÏÕ¾\\s*\\((.+?\\d{4})\\)*(.+)",Pattern.DOTALL);
				matcher = p1.matcher(str);
				
				if(matcher.find()){
					handleData(list, dateFormat, floorth, matcher,replyUrl);
				}else{//×Ô¶¯·¢ÐÅ
					p1=Pattern.compile("·¢ÐÅÈË:(.+?),.*×Ô¶¯·¢ÐÅÏµÍ³\\s*\\((.+?\\d{4})\\)(.+)",Pattern.DOTALL);
					matcher = p1.matcher(str);
					if(matcher.find()){
						handleData(list, dateFormat, floorth, matcher,replyUrl);
					}else{
						p1=Pattern.compile("·¢ÐÅÈË:(.+?),.*BBS\\s*\\((.+?\\d{4})\\)(.+)",Pattern.DOTALL);
						matcher = p1.matcher(str);
						if(matcher.find()){
							handleData(list, dateFormat, floorth, matcher,replyUrl);
						}
					}
				}
			}
		}
		return list;
	}

	private void handleData(
			List<TopicDetailInfo> list,
			SimpleDateFormat dateFormat, 
			int floorth, Matcher matcher,String replyUrl) {
		String author=matcher.group(1).trim();
		String pubTime=matcher.group(2).trim();
		pubTime=pubTime.replaceAll("\\)", "").trim();
		pubTime=dateFormat.format(new Date(pubTime));
		
		String content=matcher.group(3).trim();
		
		content=UiUtils.deleteNewLineMark(content);
		
		content=content.replaceAll("\\[/*uid\\]", "").trim();
		//[1;35mSent From ÄÏ´óÐ¡°ÙºÏ  by MI NOTE LTE[m
		content=content.replaceAll("\\[1;35m", "<font color='purple'>").replaceAll("\\[m", "</font>");
		
		content=content.replaceAll("\\[.*?m", "");
		
		content=content.replaceAll("http.*?(jpg|jpeg|png|JPG|JPEG|PNG|gif|GIF)", "<br><img src=\""+"$0"+"\"/><br>");
		
		
		content=content.replaceAll("\\n", "<br>");
		
		
//		content=content.replaceAll("\\s+<br>","");
		
		//System.out.println("content:================\n"+content);
		
		//content=ToSBC(content);
		TopicDetailInfo info=new TopicDetailInfo(author, floorth+"", pubTime, content, loadMoreUrl,replyUrl);
		list.add(info);
	}
	
	  public static String ToSBC(String input) { 
	        char c[] = input.toCharArray(); 
	        for (int i = 0; i < c.length; i++) { 
	            if (c[i] == ' ') { 
	                c[i] = '\u3000'; 
	            } else if (c[i] < '\177') { 
	                c[i] = (char) (c[i] + 65248); 
	            } 
	        } 
	        return new String(c); 
	    } 

}
