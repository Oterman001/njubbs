package com.oterman.njubbs.protocol;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.text.TextUtils;

import com.oterman.njubbs.utils.CacheUtils;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;


public abstract class BaseProtocol<E> {

	/**
	 * 从服务器加载十大数据，解析并返回
	 */
	public List<E> loadFromServer(String url,boolean saveToLocal) {
		List<E> list = null;
		try {
			Document doc = Jsoup.connect(url).timeout(8000).get();
			if(doc!=null){
				list=parseHtml(doc,url);
				LogUtil.d("从服务器获取数据！");
				//保存到缓存
				if(saveToLocal){
					CacheUtilsNew.saveToLocal(getSaveKey(), doc.html());
				}
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
	public  abstract List<E> parseHtml(Document doc,String url);

	/**
	 * 需要缓存的必须重写   缓存文件名
	 * @return
	 */
	
	public String getSaveKey(){
		return null;
	}

	/**
	 * 从本地加载数据
	 */
	public List<E> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("从本地缓存获取成功！");
			
			return parseHtml(doc,null);
		}else{
			return loadFromServer(url,true);
		}
	}


}
