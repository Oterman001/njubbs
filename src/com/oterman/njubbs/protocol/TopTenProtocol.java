package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.utils.CacheUtils;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;

/**
 * 加载十大数据 并解析成一个集合
 * 
 * @author oterman
 */
public class TopTenProtocol extends BaseProtocol<TopicInfo>  {
	
	@Override
	public String getSaveKey() {
		return "topten";
	}
	/**
	 * 解析html
	 * @param doc
	 * @return
	 */
	public List<TopicInfo> parseHtml(Document doc,String url) {
		List<TopicInfo> list = new ArrayList<>();
		Elements trEles = doc.select("tr");
		for (int i = 1; i < trEles.size(); i++) {
			Element trEle = trEles.get(i);
			Elements tdEles = trEle.select("td");

			String rankth = tdEles.get(0).text();
			String board = tdEles.get(1).text();
			String boardUrl = tdEles.get(1).select("a").get(0).attr("href");

			String title = tdEles.get(2).text();
			String contentUrl = tdEles.get(2).select("a").get(0).attr("href");

			String author = tdEles.get(3).text();
			String authorUrl = tdEles.get(3).select("a").get(0).attr("href");

			String replyCount = tdEles.get(4).text();
			
			TopicInfo info=new TopicInfo(board, author, title, replyCount, boardUrl, contentUrl, authorUrl, rankth);

			list.add(info);
		}
		return list;
	}

}
