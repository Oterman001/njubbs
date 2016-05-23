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
 * �����ղصİ���
 *
 */
public class FavBoardsProtocol {
	HttpUtils httpUtils=null;
	//��������
	public List<BoardInfo> loadFromServer(String url,boolean saveToLocal) {
			
			httpUtils=new HttpUtils();
			
			String cookie=BaseApplication.getCookie();
			
			if(cookie==null){
				cookie=BaseApplication.autoLogin();
			}
			
			RequestParams rp=new RequestParams();
			rp.addHeader("Cookie", cookie);
			
			//��������,��ȡ�����ղصİ����ҳ��
			ResponseStream stream;
			try {
				stream = httpUtils.sendSync(HttpMethod.GET, url, rp);
				String favHtml = BaseApplication.StreamToStr(stream);
				//����
				Document doc= Jsoup.parse(favHtml.toString());
				//LogUtil.d("bbsleft:\n"+favHtml.toString());
				
				if(saveToLocal){//��������
					CacheUtilsNew.saveToLocal(getSaveKey(), doc.html());
				}
				
				return parseHtml(doc);
			} catch (Exception e) {//����ʧ��
				e.printStackTrace();
				LogUtil.d("��ȡ�Ѳذ�������ʧ��");
			}
			return null;
	}

	//���ػ������
	public List<BoardInfo> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());
		if (!TextUtils.isEmpty(html)) {
			Document doc = Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡ�ɹ���");
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
			if(flag&&!aEle.text().equals("Ԥ������")){//�ҵ��ղصİ���
				BoardInfo info=new BoardInfo(null, aEle.text(), null, null);
				sb.append(aEle.text()).append("#");
				list.add(info);
			}
			if(aEle.text().equals("Ԥ��������")){//��ʼ��¼
				flag=true;
			}
			if(aEle.text().equals("Ԥ������")){//��ʼ��¼
				flag=false;
			}
		}
		
		SPutils.saveToSP("favBoards_"+SPutils.getFromSP("id"), sb.toString());
		LogUtil.d("�����ղذ��棺favBoards_"+SPutils.getFromSP("id")+"  :"+sb.toString());
		return list;
	}
}
