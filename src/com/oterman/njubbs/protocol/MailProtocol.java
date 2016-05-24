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
	 * 从服务器加载十大数据，解析并返回
	 */
	public List<MailInfo> loadFromServer(String url,boolean saveToLocal) {
		List<MailInfo> list = null;
		try {
			httpUtils = new HttpUtils();
			// 登陆后，获取站内信息
			RequestParams rp = new RequestParams();
			String cookie = BaseApplication.getCookie();
			
			if (cookie == null) {
				cookie=BaseApplication.autoLogin();
			}

			rp.addHeader("Cookie", cookie);

			ResponseStream stream = httpUtils.sendSync(HttpMethod.GET,url, rp);

			String result = BaseApplication.StreamToStr(stream);
			
			//LogUtil.d("站内结果："+result);
			
			if (!result.contains("您尚未登录")) {
				Document doc = Jsoup.parse(result);
				if (doc != null) {
					list = parseHtml(doc);
					LogUtil.d("从服务器获取数据！");
					// 保存到缓存
					if (saveToLocal) {
						CacheUtilsNew.saveToLocal(getSaveKey(), result);
					}
				}
			}else if(result.contains("未登录")){
				//未登录，跳转到登陆界面
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("自动登陆失败，请手动登录");
						//跳转到登陆界面
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
	 * 从本地加载数据
	 */
	public List<MailInfo> loadFromCache(String url) {
		String html = CacheUtilsNew.loadFromLocal(getSaveKey());

		if (!TextUtils.isEmpty(html)) {
			Document doc = Jsoup.parse(html);
			LogUtil.d("从本地缓存获取成功！");

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
			if (aEle.text().equals("上一页")) {
				loadingMoreUrl = aEle.attr("href");
				break;
			}
		}

		Pattern p = Pattern.compile(
				".*您的信箱容量为: (.*?)K,.*?信件总数: (.*?)封.*?总计:(.*?)K.*",
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
				title=title.replaceFirst("★", "").trim();
				String contentUrl = aEle.attr("href");

				MailInfo mail = new MailInfo(author, postTime, title,
						contentUrl, loadingMoreUrl,hasRead);
				list.add(0,mail);
			}

		}
		
		return list;
	}
}
