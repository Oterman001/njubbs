package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;

public class TopAllProtocol  {

	public static Map<Integer, String> keyMap=new TreeMap<>();
	
	static{
		keyMap.put(0, "本站系统区");
		keyMap.put(1, "南京大学区");
		keyMap.put(2, "乡情校谊区");
		keyMap.put(3, "电脑技术区");
		keyMap.put(4, "学术科学区");
		keyMap.put(5, "文化艺术区");
		keyMap.put(6, "体育娱乐区");
		keyMap.put(7, "感性休闲区");
		keyMap.put(8, "新闻信息区");
		keyMap.put(9, "百合广角区");
		keyMap.put(10, "校务信箱区");
		keyMap.put(11, "社团群体区");
	}
	
	/**
	 * 从服务器加载十大数据，解析并返回
	 */
	public Map<String, List<TopicInfo>> loadFromServer(String url,boolean saveToLocal) {
		Map<String, List<TopicInfo>> map=null;
		try {
			Document doc = Jsoup.connect(url).get();
			if(doc!=null){
				map=parseHtml(doc,url);
				LogUtil.d("从服务器获取数据！");
				//保存到缓存
				if(saveToLocal){
					CacheUtilsNew.saveToLocal("topall", doc.html());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public   Map<String, List<TopicInfo>> parseHtml(Document doc,String url){
		Map<String, List<TopicInfo>> map=new HashMap<>();
		
		Elements tdSectionEles = doc.select("td[colspan=2]");	
		int j=0;
		Elements tdAllEles = doc.select("td");
		for (int i = 0; i < tdSectionEles.size(); i++) {//12个讨论区
			
			List<TopicInfo> list=new ArrayList<>();
			
			for(;j<tdAllEles.size();j++){
				Element tdEle = tdAllEles.get(j);
				Elements aEles = tdEle.select("a");
				if(aEles.size()!=0){//得到帖子
					Element aEle = aEles.get(0);
					String title=aEle.text();
					String contentUrl=aEle.attr("href");
					
					aEle=aEles.get(1);
					
					String board=aEle.text();
					String boardUrl=aEle.attr("href");
					
					TopicInfo info=new TopicInfo(board, title, boardUrl, contentUrl);
					
					list.add(info);
					continue;
				}
				if(tdEle.select("a").size()==0&&tdEle.select("img").size()==0){//讨论区结束标记 
					j++;
					break;
				}
			}
			//放入map中
			map.put(keyMap.get(i), list);
		}
		
		return map;
	}

	
	public Map<Integer, String> getKeyMap(){
		return keyMap;
	}
	
	/**
	 * 从本地加载数据
	 */
	public Map<String, List<TopicInfo>> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal("topall");
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("从本地缓存获取成功！");
			return parseHtml(doc, null);
		}else{
			return loadFromServer(url, true);
		}
	}
	
	
}
