package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.text.TextUtils;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.utils.CacheUtils;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.UiUtils;

/*
 * 处理我的发帖记录
 */
public class MyTopicHistProtocol  {

	public List<TopicInfo> loadFromServer(Context context,boolean saveToLocal,boolean isPost) {
		//自动登陆一下
		String id=SPutils.getFromSP("id");
		if(BaseApplication.getCookie()==null&&TextUtils.isEmpty(id)){
			String  cookie=BaseApplication.autoLogin(context, true);
			if(cookie==null){
				return null;
			}
		}
		
		
		List<TopicInfo> list=null;
		
		String url=Constants.QUERY_TOPIC_URL;
		try {
			Connection conn = Jsoup.connect(url).timeout(10000);
			
			//处理参数
			Map<String, String> paramMap=handleParamMap(isPost);
			
			Document doc = conn.data(paramMap).postDataCharset("gbk").post();

			if(doc.html().contains("使用搜索的间隔请勿")){
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("搜索间隔应大于10秒");
					}
				});
				return null;
			}
			
			//保存缓存
			if(id!=null){
				if(isPost){
					CacheUtils.saveToLocal(id+"_mypost", doc.html());
				}else{
					CacheUtils.saveToLocal(id+"_myreply", doc.html());
				}
			}
			list=parseHtml(doc);
			//标记下刚更新过
			if(isPost){
				BaseApplication.myTopicUpdated=true;
			}else{
				BaseApplication.myReplyUpdate=true;
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
	private List<TopicInfo> parseHtml(Document doc) {
		List<TopicInfo> list=new ArrayList<>();
		
		Elements trEles = doc.select("tr");
		if(trEles.size()<8){
			//LogUtil.d("查询结果："+doc.html());
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
		
		return list;
	}


	private Map<String, String> handleParamMap(boolean isPost) {
		//处理参数
		Map<String, String> map=new HashMap<>();
		
		if(isPost){//只要发帖不要回复
			map.put("title3", "Re");//不包含
			map.put("title", "");//标题包含
			map.put("title2", "");//标题还包含
		}else{//只要回复  不要发帖   帖子标题包含Re
			map.put("title3", "");
			map.put("title", "Re");//标题包含
			map.put("title2", "");//标题还包含
		}
		map.put("flag", "1");//
		map.put("day", "0");//开始
		
		map.put("user", SPutils.getFromSP("id"));//作者
		map.put("day2", "9999");//截至日期
		return map;
	}


	/**
	 * 从本地加载数据
	 */
	public List<TopicInfo> loadFromCache(Context context,boolean saveToLocal,boolean isPost) {
		String id=SPutils.getFromSP("id");
		String html=null;
		if(isPost){
			html = CacheUtilsNew.loadFromLocal((id+"_mypost"));
		}else{
			html = CacheUtilsNew.loadFromLocal((id+"_myreply"));
		}
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("从本地缓存获取成功！");
			return parseHtml(doc);
		}else{
			return loadFromServer(context,true,isPost);
		}
	}
	
	
}
