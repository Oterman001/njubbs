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


public abstract class BaseProtocol<E> {

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public List<E> loadFromServer(String url) {
		List<E> list = null;
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
	 * ����html   
	 * @param doc
	 * @return
	 */
	public  abstract List<E> parseHtml(Document doc,String url);



}
