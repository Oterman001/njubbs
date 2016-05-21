package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;
/**
 * 获取站内信内容
 * @author oterman
 *
 */
public class MailContentProtocol {

	private HttpUtils httpUtils;

	/**
	 * 从服务器加载十大数据，解析并返回
	 */
	public MailInfo loadFromServer(String url,boolean saveToLocal) {
		MailInfo info=null;
		try {
			String cookie = BaseApplication.cookie;
			if (cookie == null) {
				BaseApplication.autoLogin();
				cookie = BaseApplication.cookie;
			}

			//	"_U_NUM=xx;_U_UID=xx;_U_KEY=xx
			String[] strs = cookie.split(";");
			if(strs.length==3){
				Map<String, String> cookies=new HashMap<>();
				cookies.put("_U_NUM",strs[0].split("=")[1]);
				cookies.put("_U_UID",strs[1].split("=")[1]);
				cookies.put("_U_KEY",strs[2].split("=")[1]);
				
				Document doc= Jsoup.connect(url).cookies(cookies).get();
				String result=doc.html();
				LogUtil.d("站内信内容："+result);
				
				if (!result.contains("您尚未登录")) {
					info = parseHtml(doc);
					LogUtil.d("从服务器获取数据！");
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 从本地加载数据
	 */
	public MailInfo loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());

		if (!TextUtils.isEmpty(html)) {
			Document doc = Jsoup.parse(html);
			LogUtil.d("从本地缓存获取成功！");

			return parseHtml(doc);
		} else {
			return loadFromServer(url,true);
		}
	}

	private String getSaveKey() {
		return "bbsmail_content";
	}

	private MailInfo parseHtml(Document doc) {
		MailInfo info=null;
		
		Elements aEles = doc.select("a");
		String replyUrl=null;
		String delUrl=null;
		
		for(int i=aEles.size()-1;i>=0;i--){
			Element ele = aEles.get(i);
			
			if(ele.text().equals("回信")){
				replyUrl=ele.attr("href");
				continue;
			}
			
			if(ele.text().equals("删除")){
				delUrl=ele.attr("href");
				continue;
			}
		}
		
		Elements tdEles = doc.select("td");
		String text=tdEles.get(0).text();
		
		text = text.replaceFirst("来.*?源.*","");
		
		//Pattern p=Pattern.compile(".*?寄信人:(.*?).标 题:(.*?).发信站.*?\\((.*?\\d{4})\\)(.*?)--.*",Pattern.DOTALL);
		Pattern p=Pattern.compile(".*?寄信人:(.*?)标.*?题:(.*?)发信站.*?\\((.*?)\\)(.*?)--.*",Pattern.DOTALL);
		
		Matcher matcher = p.matcher(text);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		
		if(matcher.find()){
			String author=matcher.group(1).trim();
			String title=matcher.group(2).trim();
			String posttime=matcher.group(3).trim();
			posttime=dateFormat.format(new Date(posttime));
			String content=matcher.group(4).trim();
			
			info=new MailInfo(author, posttime, title, content, replyUrl, delUrl);
			
		}
		
		return info;
	}
}
