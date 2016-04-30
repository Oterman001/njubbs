package com.oterman.njubbs.protocol;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.bean.TopTenInfo;
import com.oterman.njubbs.utils.CacheUtils;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;

/**
 * ����ʮ������ ��������һ������
 * 
 * @author oterman
 */
public class TopTenProtocol {

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public List<TopTenInfo> loadFromServer() {
		List<TopTenInfo> list = null;
		try {

			Document doc = Jsoup.connect(Constants.TOP_TEN_URL).get();
			// ���浽������
			CacheUtils.saveToLocal("topten", doc.html());

			if(doc!=null){
				list=parseHtml(doc);
			}
			LogUtil.d("�������ȡʮ�����ݳɹ���");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * �ӱ��ؼ�������
	 */
	public List<TopTenInfo> loadFromCache() {
		String html = CacheUtils.loadFromLocal("topten");
		
		if(!TextUtils.isEmpty(html)){
			Document doc=Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡʮ�����ݳɹ���");
			
			return parseHtml(doc);
		}else{
			return loadFromServer();
		}
	}

	/**
	 * ����html
	 * @param doc
	 * @return
	 */
	public List<TopTenInfo> parseHtml(Document doc) {
		List<TopTenInfo> list = new ArrayList<>();
		Elements trEles = doc.select("tr");
		for (int i = 1; i < trEles.size(); i++) {
			Element trEle = trEles.get(i);
			Elements tdEles = trEle.select("td");

			String rankth = tdEles.get(0).text();
			String board = tdEles.get(1).text();
			String boardUrl = tdEles.get(1).select("a").get(0).attr("href");

			String title = tdEles.get(2).text();
			String contentUrl = tdEles.get(2).select("a").get(0).attr("href");

			String author = tdEles.get(3).text();
			String authorUrl = tdEles.get(3).select("a").get(0).attr("href");

			String replyCount = tdEles.get(4).text();

			TopTenInfo info = new TopTenInfo(rankth, board, author, title,
					Integer.parseInt(replyCount), boardUrl, contentUrl,
					authorUrl);

			list.add(info);
		}
		return list;
	}

	/**
	 * ʹ��xutil���������
	 */
	public String loadFromServerUseXutls() {
		String result = null;
		try {
			HttpUtils httpUtils = new HttpUtils();

			String url = Constants.TOP_TEN_URL;
			ResponseStream rs = httpUtils.sendSync(HttpMethod.GET, url);

			InputStream in = rs.getBaseStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"gbk"));
			StringBuffer sb = new StringBuffer();

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			result = sb.toString();

			LogUtil.d("ʮ�����ݣ�" + result);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
