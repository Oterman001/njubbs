package com.oterman.njubbs.protocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;
/**
 * ��ȡվ��������
 * @author oterman
 *
 */
public class MailContentProtocol {

	private HttpUtils httpUtils;

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public MailInfo loadFromServer(String url,boolean saveToLocal,Context context) {
		MailInfo info=null;
		try {
			String cookie = BaseApplication.getCookie();
			if (cookie == null) {
				cookie=BaseApplication.autoLogin(context,true);
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
					BaseApplication.autoLogin(context,true);
					cookie = BaseApplication.getCookie();
					cookies.put("_U_NUM",strs[0].split("=")[1]);
					cookies.put("_U_UID",strs[1].split("=")[1]);
					cookies.put("_U_KEY",strs[2].split("=")[1]);
					
					doc= Jsoup.connect(url).cookies(cookies).get();
				}
				String result=doc.html();
				LogUtil.d("վ�������ݣ�"+result);
				
				if (!result.contains("����δ��¼")) {
					info = parseHtml(doc);
					LogUtil.d("�ӷ�������ȡ���ݣ�");
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * �ӱ��ؼ�������
	 */
	public MailInfo loadFromCache(String url,Context context) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());

		if (!TextUtils.isEmpty(html)) {
			Document doc = Jsoup.parse(html);
			LogUtil.d("�ӱ��ػ����ȡ�ɹ���");

			return parseHtml(doc);
		} else {
			return loadFromServer(url,true,context);
		}
	}

	private String getSaveKey() {
		return "bbsmail_content";
	}

	private MailInfo parseHtml(Document doc) {
		MailInfo info=null;
		
		Elements aEles = doc.select("a");
		String replyUrl=null;
		String delUrl=null;
		
		for(int i=aEles.size()-1;i>=0;i--){
			Element ele = aEles.get(i);
			
			if(ele.text().equals("����")){
				replyUrl=ele.attr("href");
				continue;
			}
			
			if(ele.text().equals("ɾ��")){
				delUrl=ele.attr("href");
				continue;
			}
		}
		
		Elements tdEles = doc.select("td");
		String text=tdEles.get(0).text();
		
		text = text.replaceFirst("��.*?Դ.*","");
		
		//Pattern p=Pattern.compile(".*?������:(.*?).�� ��:(.*?).����վ.*?\\((.*?\\d{4})\\)(.*?)--.*",Pattern.DOTALL);
//		Pattern p=Pattern.compile(".*?������:(.*?)��.*?��:(.*?)����վ.*?\\((.*?)\\)(.*?)��.*?��Դ.*",Pattern.DOTALL);
		Pattern p=Pattern.compile(".*?������:(.*?)��.*?��:(.*?)����վ.*?\\((.*?)\\)(.*?)--.*",Pattern.DOTALL);
		
		Matcher matcher = p.matcher(text);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy��MM��dd��  HH:mm");
		
		if(matcher.find()){
			String author=matcher.group(1).trim();
			String title=matcher.group(2).trim();
			String posttime=matcher.group(3).trim();
			posttime=dateFormat.format(new Date(posttime));
			String content=matcher.group(4).trim();

			
			content=content.replaceAll("\\[/*uid\\]", "").trim();
			content=content.replaceAll("\\[.*?m", "");
			content=content.replaceAll("http.*?(jpg|jpeg|png|JPG|JPEG|PNG|gif|GIF)", "<br><img src=\""+"$0"+"\"/><br>");
		
			content=content.replaceAll("\\n", "<br>");
//			p=Pattern.compile("(.*?)--.*?");
//			matcher=p.matcher(content);
//			
//			if(matcher.find()){
//				content=matcher.group(1).trim();
//			}
//			
			info=new MailInfo(author, posttime, title, content, replyUrl, delUrl);
			
		}
		
		return info;
	}
}
