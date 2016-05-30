package com.oterman.njubbs.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.UiUtils;

public class QueryFriendProtocol {
	
	public List<String> loadFromServer(String userid,boolean byNick){
		
		List<String> list=new ArrayList<>();
		
		String url=Constants.getQueryUserUrl(userid, byNick);
		Document doc=null;
		try {
			doc = Jsoup.connect(url).get();
			
			Elements tdEles = doc.select("td");
			
			if(tdEles.size()<6){
				LogUtil.d("��ѯ�����"+doc.html());
			}
			
			if(doc.html().contains("ƥ���ִ�������Ҫ")){
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("��ѯ��������̫��");
					}
				});
			}
			
			
			for (int i = 0; i < tdEles.size(); i++) {
				Element tdEle = tdEles.get(i);
				String text=tdEle.text();
				list.add(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list.size()==0?null:list;
	}
	
	
}
