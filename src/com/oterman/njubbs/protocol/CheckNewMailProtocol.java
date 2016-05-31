package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;
/**
 * ����Ƿ����µ�վ����  ����У������µķ���
 */
public class CheckNewMailProtocol {


	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public int checkFromServer(String url) {
		try {
			String cookie = BaseApplication.getCookie();
			if (cookie == null) {
				cookie=BaseApplication.autoLogin();
			}

			//	"_U_NUM=xx;_U_UID=xx;_U_KEY=xx
			String[] strs = cookie.split(";");
			if(strs.length==3){
				Map<String, String> cookies=new HashMap<>();
				cookies.put("_U_NUM",strs[0].split("=")[1]);
				cookies.put("_U_UID",strs[1].split("=")[1]);
				cookies.put("_U_KEY",strs[2].split("=")[1]);
				
				Document doc= Jsoup.connect(url).cookies(cookies).get();
				if(doc.select("td").size()==0){
					BaseApplication.autoLogin();
					cookie = BaseApplication.getCookie();
					cookies.put("_U_NUM",strs[0].split("=")[1]);
					cookies.put("_U_UID",strs[1].split("=")[1]);
					cookies.put("_U_KEY",strs[2].split("=")[1]);
					
					doc= Jsoup.connect(url).cookies(cookies).get();
				}
				
				String result=doc.html();
				//LogUtil.d("����µ�վ���Ž����"+result);
				
				if (!result.contains("����δ��¼")) {
					return parseHtml(doc);
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}


	private int parseHtml(Document doc) {
		int count=doc.select("tr").size()-1;
		LogUtil.d("��վ��������"+count);
		
		return count;
	}
}
