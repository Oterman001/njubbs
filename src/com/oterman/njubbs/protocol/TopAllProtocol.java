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
		keyMap.put(0, "��վϵͳ��");
		keyMap.put(1, "�Ͼ���ѧ��");
		keyMap.put(2, "����У����");
		keyMap.put(3, "���Լ�����");
		keyMap.put(4, "ѧ����ѧ��");
		keyMap.put(5, "�Ļ�������");
		keyMap.put(6, "����������");
		keyMap.put(7, "����������");
		keyMap.put(8, "������Ϣ��");
		keyMap.put(9, "�ٺϹ����");
		keyMap.put(10, "У��������");
		keyMap.put(11, "����Ⱥ����");
	}
	
	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public Map<String, List<TopicInfo>> loadFromServer(String url,boolean saveToLocal) {
		Map<String, List<TopicInfo>> map=null;
		try {
			Document doc = Jsoup.connect(url).get();
			if(doc!=null){
				map=parseHtml(doc,url);
				LogUtil.d("�ӷ�������ȡ���ݣ�");
				//���浽����
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
		for (int i = 0; i < tdSectionEles.size(); i++) {//12��������
			
			List<TopicInfo> list=new ArrayList<>();
			
			for(;j<tdAllEles.size();j++){
				Element tdEle = tdAllEles.get(j);
				Elements aEles = tdEle.select("a");
				if(aEles.size()!=0){//�õ�����
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
				if(tdEle.select("a").size()==0&&tdEle.select("img").size()==0){//������������� 
					j++;
					break;
				}
			}
			//����map��
			map.put(keyMap.get(i), list);
		}
		
		return map;
	}

	
	public Map<Integer, String> getKeyMap(){
		return keyMap;
	}
	
	/**
	 * �ӱ��ؼ�������
	 */
	public Map<String, List<TopicInfo>> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal("topall");
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡ�ɹ���");
			return parseHtml(doc, null);
		}else{
			return loadFromServer(url, true);
		}
	}
	
	
}
