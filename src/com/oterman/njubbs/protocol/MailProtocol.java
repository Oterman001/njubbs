package com.oterman.njubbs.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.activity.LoginActivity;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.utils.CacheUtilsNew;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.UiUtils;

public class MailProtocol {

	private HttpUtils httpUtils;

	/**
	 * �ӷ���������ʮ�����ݣ�����������
	 */
	public List<MailInfo> loadFromServer(String url,boolean saveToLocal) {
		List<MailInfo> list = null;
		try {
			httpUtils = new HttpUtils();
			// ��½�󣬻�ȡվ����Ϣ
			RequestParams rp = new RequestParams();
			String cookie = BaseApplication.getCookie();
			
			if (cookie == null) {
				cookie=BaseApplication.autoLogin();
			}

			rp.addHeader("Cookie", cookie);

			ResponseStream stream = httpUtils.sendSync(HttpMethod.GET,url, rp);

			String result = BaseApplication.StreamToStr(stream);
			
			//LogUtil.d("վ�ڽ����"+result);
			
			if (!result.contains("����δ��¼")) {
				Document doc = Jsoup.parse(result);
				if (doc != null) {
					list = parseHtml(doc);
					LogUtil.d("�ӷ�������ȡ���ݣ�");
					// ���浽����
					if (saveToLocal) {
						CacheUtilsNew.saveToLocal(getSaveKey(), result);
					}
				}
			}else if(result.contains("δ��¼")){
				//δ��¼����ת����½����
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("�Զ���½ʧ�ܣ����ֶ���¼");
						//��ת����½����
						Intent intent=new Intent(UiUtils.getContext(),LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						UiUtils.getContext().startActivity(intent);
					}
				});

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * �ӱ��ؼ�������
	 */
	public List<MailInfo> loadFromCache(String url) {
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
		return "bbsmail";
	}

	private List<MailInfo> parseHtml(Document doc) {
		List<MailInfo> list=new ArrayList<MailInfo>();
		String loadingMoreUrl = null;
		Elements aEles = doc.select("a");
		
		for (int i = aEles.size() - 1; i > 0; i--) {
			Element aEle = aEles.get(i);
			if (aEle.text().equals("��һҳ")) {
				loadingMoreUrl = aEle.attr("href");
				break;
			}
		}

		Pattern p = Pattern.compile(
				".*������������Ϊ: (.*?)K,.*?�ż�����: (.*?)��.*?�ܼ�:(.*?)K.*",
				Pattern.DOTALL);

		Matcher matcher = p.matcher(doc.html());
		if (matcher.find()) {
			MailInfo.totalSpace = matcher.group(1);
			MailInfo.totalCount = matcher.group(2);
			MailInfo.usedSpace = matcher.group(3);
		}

		Elements trEles = doc.select("tr");
		for (int i = 1; i < trEles.size(); i++) {

			Elements tdEles = trEles.get(i).select("td");

			if (tdEles.size() == 6) {
				boolean hasRead=true;
				if(tdEles.get(2).select("img").size()==1){
					hasRead=false;
				}
				
				String author = tdEles.get(3).text();

				String postTime = tdEles.get(4).text();

				Element aEle = tdEles.get(5).select("a").get(0);
				String title = aEle.text();
				title=title.replaceFirst("��", "").trim();
				String contentUrl = aEle.attr("href");

				MailInfo mail = new MailInfo(author, postTime, title,
						contentUrl, loadingMoreUrl,hasRead);
				list.add(0,mail);
			}

		}
		
		return list;
	}
}
