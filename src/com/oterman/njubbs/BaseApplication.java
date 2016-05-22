package com.oterman.njubbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.UiUtils;

public class BaseApplication extends Application {
	public static BaseApplication application;
	private static int mainTid;
	private static Handler handler;
	public static boolean isLogin = false;// Ĭ��Ϊ�ǵ�½
	public static String cookie = null;
	public static HttpUtils httpUtil = null;
	public static UserInfo userInfo = null;

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		mainTid = android.os.Process.myTid();
		handler = new Handler();

		// ����Ĭ�ϵ�ImageLoader���ò���
		//ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
		
		
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration  
			    .Builder(this)  
			    .threadPoolSize(5)//�̳߳��ڼ��ص�����  
			    .threadPriority(Thread.NORM_PRIORITY - 2)  
			    .denyCacheImageMultipleSizesInMemory()  
			    .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/�����ͨ���Լ����ڴ滺��ʵ��  
			    .memoryCacheSize(2 * 1024 * 1024)    
			    .discCacheSize(50 * 1024 * 1024)    
			    .discCacheFileNameGenerator(new Md5FileNameGenerator())//�������ʱ���URI������MD5 ����  
			    .tasksProcessingOrder(QueueProcessingType.LIFO)  
			    .discCacheFileCount(100) //������ļ�����  
			    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())  
			    .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)��ʱʱ��  
			    .writeDebugLogs() // Remove for release app  
			    .build();//��ʼ����  

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);
	}

	public static Context getApplication() {
		return application;
	}

	public static Handler getHandler() {
		return handler;
	}

	public static int getMainTid() {
		return mainTid;
	}

	/**
	 * �Զ���½
	 */
	public static void autoLogin() {
		// �������ļ���ȡid������
		final String id = SPutils.getFromSP("id");
		final String passwd = SPutils.getFromSP("pwd");

		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(passwd)) {
			LogUtil.d("��δ��½��,�Զ���½ʧ�ܣ�");
			return;
		}

		LogUtil.d("�����Զ���½��...");

		// �����½���߼�
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
		params.addBodyParameter("pw", passwd);

		try {
			if (httpUtil == null) {
				httpUtil = new HttpUtils();
			}
			ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST,Constants.LOGIN_URL, params);
			// �����ص�������Ϊ�ַ���
			String sb = StreamToStr(responseStream);

			final String result = sb.toString();
			LogUtil.d("��½���:" + result);

			if (result.contains("Net.BBS.setCookie")) {// ��½�ɹ�
				// ��������
				SPutils.saveToSP("id", id);
				SPutils.saveToSP("pwd", passwd);

				// ����cookie
				handleCookie(result);

				if (cookie != null) {
					
					// ��ȡ�ղصİ���
					getFavBoards();
					// �������ȡuser����Ϣ
					handleUserInfo(id);
					// �ص����̣߳���ʾ��½�ɹ�
					autoLogInOk(result);

					// ��ǵ�½
					BaseApplication.isLogin = true;
				}

			} else {// ��½ʧ��
				autoLoginFailed("�Զ���½ʧ��,���ֶ���¼��");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// ��½�ɹ�����ʾ
			autoLoginFailed("��½ʧ�ܣ��������磡");
		}
	}

	
	private static void getFavBoards() throws Exception {
		RequestParams rp=new RequestParams();
		rp.addHeader("Cookie", cookie);
		
		//��������
		ResponseStream stream = httpUtil.sendSync(HttpMethod.GET, Constants.BBSLEFT_URL, rp);
		
		String favHtml = StreamToStr(stream);
		
		//LogUtil.d("bbsleft:\n"+favHtml.toString());
		
		//����
		Document doc= Jsoup.parse(favHtml.toString());
		
		Elements aEles = doc.select("a");
		
		StringBuffer sbFav=new StringBuffer();
		boolean flag=false;
		for (int i = 0; i < aEles.size(); i++) {
			Element aEle = aEles.get(i);
			if(flag&&!aEle.text().equals("Ԥ������")){//�ҵ��ղصİ���
				sbFav.append(aEle.text()).append("#");
			}
			if(aEle.text().equals("Ԥ��������")){//��ʼ��¼
				flag=true;
			}
			if(aEle.text().equals("Ԥ������")){//��ʼ��¼
				flag=false;
			}
		}
		
		SPutils.saveToSP("favBoards", sbFav.toString());
		
		LogUtil.d("favBoards:"+sbFav.toString());
		
	}

	private static void handleUserInfo(final String id) {
		UserProtocol protocol = new UserProtocol();
		userInfo = protocol.getUserInfoFromServer(id);
	}

	private  static void handleCookie(final String result) {
		String reg = "Net.BBS.setCookie\\(\\'" + "(\\d+)" + "N" + "(.*?)\\+"
				+ "(\\d+)" + "\\'\\)";
		Pattern p = Pattern.compile(reg, Pattern.DOTALL);
		Matcher matcher = p.matcher(result);

		if (matcher.find()) {
			Integer _U_NUM = Integer.parseInt(matcher.group(1)) + 2;
			String _U_UID = matcher.group(2);
			Integer _U_KEY = Integer.parseInt(matcher.group(3)) - 2;

			String cookie = "_U_NUM=" + _U_NUM + ";_U_UID=" + _U_UID
					+ ";_U_KEY=" + _U_KEY;
			
			LogUtil.d("cookie:" + cookie);

			// ����cookie
			BaseApplication.cookie = cookie;
		}
	}

	private static void autoLogInOk(String result) {
		UiUtils.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtil.d("�Զ���½�ɹ���");
			}
		});
	}

	private static void autoLoginFailed(final String msg) {
		UiUtils.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtil.d(msg);
			}
		});
	}

	public static String StreamToStr(ResponseStream responseStream)
			throws UnsupportedEncodingException, IOException {

		InputStream is = responseStream.getBaseStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));

		String line = null;
		StringBuffer sb = new StringBuffer();

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

}
