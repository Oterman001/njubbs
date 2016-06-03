package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.UiUtils;

public class QueryTopicProtocol {
	
	public List<TopicInfo> loadFromServer(Map<String, String> paramMap){
		List<TopicInfo> list=new ArrayList<>();
		String url=Constants.QUERY_TOPIC_URL;
		try {
			Connection conn = Jsoup.connect(url);
			
			Document doc = conn.data(paramMap).postDataCharset("gbk").post();
			Elements trEles = doc.select("tr");
			if(trEles.size()<8){
				LogUtil.d("��ѯ�����"+doc.html());
			}
			if(doc.html().contains("ʹ�������ļ������")){
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("�������Ӧ����10��");
					}
				});
				
				return null;
			}
			
			for (int i = 0; i < trEles.size(); i++) {
				 Elements tdEles = trEles.get(i).select("td");
				 if(tdEles.size()==4){
					 String author=tdEles.get(1).text();
					 
					 String date = tdEles.get(2).text();
					 
					 Element tdEle = tdEles.get(3);

					 String title=tdEle.text();
					 //http://bbs.nju.edu.cn/bbstcon?board=Pictures&file=M.1464589051.A
					 //bbscon?board=test&amp;file=M.1453422453.A&amp;num=973
					 
					 String contentUrl=tdEle.select("a").get(0).attr("href");
					 
					 contentUrl=contentUrl.replaceFirst("bbscon", "bbstcon");
					 
					 contentUrl=contentUrl.substring(0, contentUrl.lastIndexOf('&'));
					 String board=contentUrl.substring(contentUrl.indexOf('=')+1,contentUrl.indexOf('&')).trim();
					 String boardUrl="bbstdoc?board="+board;
					 TopicInfo info=new TopicInfo(board, author, title, contentUrl, date,boardUrl);
					 list.add(info);
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list.size()==0?null:list;
		
	}
	
	public List<TopicInfo> loadFromServer(String keys){
		
		Map<String, String> paramMap = handleParams(keys);
		
		return loadFromServer(paramMap);
		
	}

	//������������ѯ
	public List<TopicInfo> queryByAuthor(String author) {
		return loadFromServer(author);
	}

	
	
	private Map<String, String> handleParams(String keys) {
			Map<String, String> map=new HashMap<>();
			map.put("title3", "Re");//������
	//		map.put("day2", "30");//180��
			map.put("flag", "1");//
			map.put("day", "0");
	
			String[] strs = keys.split("\\s+");
			//�ж��Ƿ������Ϊid
			Pattern p=Pattern.compile("[a-zA-Z0-9]+");
			
			Matcher matcher = p.matcher(strs[0]);
			
			if(matcher.matches()){//������������ѯ  ��ѯ999��
				map.put("user", strs[0]);//����
				map.put("day2", "999");//180��
			}else{//������������ѯ
				if(strs.length>=2){
					map.put("title", strs[0].trim());//�������
					map.put("title2", strs[1].trim());//���⻹����
				}else{
					map.put("title", strs[0].trim());//�������
					map.put("title2", "");//���⻹����
				}
				map.put("user", "");//����
				map.put("day2", "30");//180��
			}
			return map;
		}
	
}
