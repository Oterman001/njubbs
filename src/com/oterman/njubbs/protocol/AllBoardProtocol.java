package com.oterman.njubbs.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.db.BoardDao;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;

public class AllBoardProtocol {

	BoardDao boardDao;
	
	public void saveAllBoards(){
		 
		if(boardDao==null)boardDao=new BoardDao();
		
		String url=Constants.ALL_BOARDS_URL;
		
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			LogUtil.d("获取所有的版面信息");
			Elements trEles = doc.select("tr");
			
			for (int i = 1; i < trEles.size(); i++) {
				Elements tdEles = trEles.get(i).select("td");
				
				if(tdEles.size()==5){
					int id = Integer.parseInt(tdEles.get(0).text());
					String boardName=tdEles.get(1).text().trim();
					String category=tdEles.get(2).text().trim();
					category=category.substring(1,category.length()-1);
					String chineseName=tdEles.get(3).text().replaceFirst("○ ", "").trim();
					
					String boardUrl="bbstdoc?board="+boardName;
					BoardInfo info=new BoardInfo(id, boardName, category, chineseName, boardUrl);
					boardDao.insert(info);
				}
			}
			
			LogUtil.d("保存所有的版面信息到数据库中成功！");
			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d("保存版面信息失败！");
		}
		
	}
	
	
}
