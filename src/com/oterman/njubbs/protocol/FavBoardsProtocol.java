package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.SPutils;
/**
 * 解析收藏的版面
 *
 */
public class FavBoardsProtocol {
	HttpUtils httpUtils=null;
	//联网解析
	public List<BoardInfo> loadFromServer(String url,boolean saveToLocal) {
			
			httpUtils=new HttpUtils();
			
			String cookie=BaseApplication.getCookie();
			
			if(cookie==null){
				cookie=BaseApplication.autoLogin();
			}
			
			RequestParams rp=new RequestParams();
			rp.addHeader("Cookie", cookie);
			
			//发送请求,获取包含收藏的版面的页面
			ResponseStream stream;
			try {
				stream = httpUtils.sendSync(HttpMethod.GET, url, rp);
				String favHtml = BaseApplication.StreamToStr(stream);
				//解析
				Document doc= Jsoup.parse(favHtml.toString());
				//LogUtil.d("bbsleft:\n"+favHtml.toString());
				
				if(saveToLocal){//保存起来
					CacheUtilsNew.saveToLocal(getSaveKey(), doc.html());
				}
				
				return parseHtml(doc);
			} catch (Exception e) {//联网失败
				e.printStackTrace();
				LogUtil.d("获取搜藏版面数据失败");
			}
			return null;
	}

	//本地缓存解析
	public List<BoardInfo> loadFromCache(String url) {
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
		return "favboards_"+SPutils.getFromSP("id");
	}
	
	private List<BoardInfo> parseHtml(Document doc) {
		List<BoardInfo> list=new ArrayList<>();
		Elements aEles = doc.select("a");
		
		StringBuffer sb=new StringBuffer();
		
		boolean flag=false;
		
		for (int i = 0; i < aEles.size(); i++) {
			Element aEle = aEles.get(i);
			if(flag&&!aEle.text().equals("预定管理")){//找到收藏的版面
				BoardInfo info=new BoardInfo(null, aEle.text(), null, null);
				sb.append(aEle.text()).append("#");
				list.add(info);
			}
			if(aEle.text().equals("预定讨论区")){//开始记录
				flag=true;
			}
			if(aEle.text().equals("预定管理")){//开始记录
				flag=false;
			}
		}
		
		SPutils.saveToSP("favBoards_"+SPutils.getFromSP("id"), sb.toString());
		LogUtil.d("保存收藏版面：favBoards_"+SPutils.getFromSP("id")+"  :"+sb.toString());
		return list;
	}
}
