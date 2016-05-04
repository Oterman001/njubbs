package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oterman.njubbs.bean.BoardInfo;

public class HotBoardProtocol extends BaseProtocol<BoardInfo> {

	@Override
	public List<BoardInfo> parseHtml(Document doc, String url) {
		List<BoardInfo> list=new ArrayList<>();
		
		Elements trEles = doc.select("tr");

		for (int i = 1; i < trEles.size(); i++) {

			Element trEle = trEles.get(i);

			Elements tdEles = trEle.select("td");

			String rankth = tdEles.get(0).text();

			String boardName = tdEles.get(1).text();

			String boardUrl = tdEles.get(1).select("a").get(0).attr("href");

			String chineseName = tdEles.get(2).text();

			String peopleCount = tdEles.get(4).text();

			BoardInfo info = new BoardInfo(rankth, boardName, chineseName,
					boardUrl, peopleCount);

			list.add(info);
		}

		return list;
	}

}
