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
				LogUtil.d("查询结果："+doc.html());
			}
			
			if(doc.html().contains("匹配字串至少需要")){
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("查询条件不能太短");
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
