package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.UiUtils;

public class QueryTopicProtocol {
	
	public List<TopicInfo> loadFromServer(Map<String, String> paramMap){
		List<TopicInfo> list=new ArrayList<>();
		String url=Constants.QUERY_TOPIC_URL;
		try {
			Connection conn = Jsoup.connect(url).timeout(10000);
			
			Document doc = conn.data(paramMap).postDataCharset("gbk").post();
			Elements trEles = doc.select("tr");
			if(trEles.size()<8){
				LogUtil.d("查询结果："+doc.html());
			}
			if(doc.html().contains("使用搜索的间隔请勿")){
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("搜索间隔应大于10秒");
					}
				});
				
				return null;
			}
			
			for (int i = 0; i < trEles.size(); i++) {
				 Elements tdEles = trEles.get(i).select("td");
				 if(tdEles.size()==4){
					 String author=tdEles.get(1).text();
					 
					 String date = tdEles.get(2).text();
					 
					 Element tdEle = tdEles.get(3);
	
					 String title=tdEle.text();
					 //http://bbs.nju.edu.cn/bbstcon?board=Pictures&file=M.1464589051.A
					 //bbscon?board=test&amp;file=M.1453422453.A&amp;num=973
					 
					 String contentUrl=tdEle.select("a").get(0).attr("href");
					 
					 if(!title.contains("Re")){//不是回帖
						 contentUrl=contentUrl.replaceFirst("bbscon", "bbstcon");
						 contentUrl=contentUrl.substring(0, contentUrl.lastIndexOf('&'));
					 }
					 
					 String board=contentUrl.substring(contentUrl.indexOf('=')+1,contentUrl.indexOf('&')).trim();
					 String boardUrl="bbstdoc?board="+board;
					 
					 TopicInfo info=new TopicInfo(board, author, title, contentUrl, date,boardUrl);
					 list.add(info);
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.size()==0?null:list;
		
	}

	public List<TopicInfo> loadFromServer(String keys){
		Map<String, String> paramMap = handleParams(keys);
		return loadFromServer(paramMap);
		
	}

	//根据作者来查询
	public List<TopicInfo> queryByAuthor(String author) {
		return loadFromServer(author);
	}
	/**
	 * 处理按照作者查询时的参数
	 * @param author
	 * @return
	 */
	private Map<String, String> handleParamsByAuthor(String author) {
		Map<String, String> map=new HashMap<>();
		
		String byauthor_re = SPutils.getFromSP("byauthor_re");
		if("yes".equals(byauthor_re)){//包含re
			map.put("title3", "");//不包含
		}else{//不包含re
			map.put("title3", "Re");//不包含
		}
		map.put("flag", "1");//
		map.put("day", "0");//开始
		
		map.put("title", "");//标题包含
		map.put("title2", "");//标题还包含
		
		map.put("user", author);//作者
		
		String byauthor_day = SPutils.getFromSP("byauthor_day");
		if(TextUtils.isEmpty(byauthor_day)){//默认截至日期
			map.put("day2", "999");//截至日期
		}else{
			map.put("day2", byauthor_day);//截至日期
		}
		return map;
	}
	/**
	 * 处理按标题查询时的参数
	 * @param author
	 * @return
	 */
	private Map<String, String> handleParamsByTitle(String title1,String title2) {
		Map<String, String> map=new HashMap<>();
		map.put("flag", "1");
		map.put("day", "0");
		
		String bytitle_re = SPutils.getFromSP("bytitle_re");
		if("yes".equals(bytitle_re)){//包含re
			map.put("title3", "");//title3表示不包含的关键字
		}else{//不包含re
			map.put("title3", "Re");
		}
		
		map.put("user", "");//作者
		
		String bytitle_day = SPutils.getFromSP("bytitle_day");
		if(TextUtils.isEmpty(bytitle_day)){//默认截至日期
			map.put("day2", "30");//截至日期 默认30天
		}else{//设置了截至日期
			map.put("day2", bytitle_day);//截至日期
		}
		
		map.put("title", title1);//标题包含
		map.put("title2", title2);//标题还包含
		
		return map;
	}
	
	private Map<String, String> handleParams(String keys) {
		if(keys!=null){
			String[] strs = keys.split("\\s+");
			//判断是否输入的为id
			Pattern p=Pattern.compile("[a-zA-Z0-9]+");
			Matcher matcher = p.matcher(strs[0]);
			
			if(matcher.matches()){//按照作者来查询  查询999天
				return handleParamsByAuthor(strs[0]);
			}else{//按照内容来查询
				if(strs.length>=2){
					return handleParamsByTitle(strs[0].trim(), strs[1].trim());
				}else{
					return handleParamsByTitle(strs[0].trim(), "");
				}
			}
		}
		
		return null;
		
	}
	
	private Map<String, String> handleParams_old(String keys) {
		
			Map<String, String> map=new HashMap<>();
			map.put("title3", "Re");//不包含
			map.put("flag", "1");//
			map.put("day", "0");
			if(keys!=null){
				String[] strs = keys.split("\\s+");
				
				//判断是否输入的为id
				Pattern p=Pattern.compile("[a-zA-Z0-9]+");
				
				Matcher matcher = p.matcher(strs[0]);
				
				if(matcher.matches()){//按照作者来查询  查询999天
					map.put("user", strs[0]);//作者
					map.put("day2", "999");//180天
				}else{//按照内容来查询
					if(strs.length>=2){
						map.put("title", strs[0].trim());//标题包含
						map.put("title2", strs[1].trim());//标题还包含
					}else{
						map.put("title", strs[0].trim());//标题包含
						map.put("title2", "");//标题还包含
					}
					
					map.put("user", "");//作者
					map.put("day2", "30");//180天
				}
			}
			

			return map;
		}
	
}
