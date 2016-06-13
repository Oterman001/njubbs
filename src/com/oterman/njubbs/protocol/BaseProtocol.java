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
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public List<E> loadFromServer(String url,boolean saveToLocal) {
		List<E> list = null;
		try {
			Document doc = Jsoup.connect(url).timeout(8000).get();
			if(doc!=null){
				list=parseHtml(doc,url);
				LogUtil.d("�ӷ�������ȡ���ݣ�");
				//���浽����
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
	 * ����html   
	 * @param doc
	 * @return
	 */
	public  abstract List<E> parseHtml(Document doc,String url);

	/**
	 * ��Ҫ����ı�����д   �����ļ���
	 * @return
	 */
	
	public String getSaveKey(){
		return null;
	}

	/**
	 * �ӱ��ؼ�������
	 */
	public List<E> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡ�ɹ���");
			
			return parseHtml(doc,null);
		}else{
			return loadFromServer(url,true);
		}
	}


}
