package com.oterman.njubbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
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
import com.oterman.njubbs.activity.LoginActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.db.BoardDao;
import com.oterman.njubbs.protocol.AllBoardProtocol;
import com.oterman.njubbs.protocol.CheckNewMailProtocol;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

public class BaseApplication extends Application {

	public static BaseApplication application;
	private static int mainTid;
	private static Handler handler;
	private static String cookie = null;

	private static HttpUtils httpUtil = null;
	private static UserInfo userInfo = null;
	private static UserProtocol userProtocol;
	private static int newMailCount = -1;
	public static boolean myTopicUpdated=false;//����ҵ����Ӹ��¹�
	public static boolean myReplyUpdate=false;//����ҵĻ������¹�

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		mainTid = android.os.Process.myTid();
		handler = new Handler();

		// ����Ĭ�ϵ�ImageLoader���ò���
		// ImageLoaderConfiguration configuration =
		// ImageLoaderConfiguration.createDefault(this);

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
				this)
				.threadPoolSize(5)
				// �̳߳��ڼ��ص�����
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/�����ͨ���Լ����ڴ滺��ʵ��
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// �������ʱ���URI������MD5 ����
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// ������ļ�����
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout
																			// (5
																			// s),
																			// readTimeout
																			// (30
																			// s)��ʱʱ��
				.writeDebugLogs() // Remove for release app
				.build();// ��ʼ����

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);

		prepateAllBoardsData();

		// ����Ƿ����µ�վ����
		// checkHasNewMail();

	}

	private void checkHasNewMail() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				LogUtil.d("����Ƿ������ʼ���");
				CheckNewMailProtocol protocol = new CheckNewMailProtocol();
				String url = Constants.HAS_NEW_MAIL_URL;
				newMailCount = protocol.checkFromServer(url, getApplication());
				LogUtil.d("�������" + newMailCount);

			}
		});
	}

	// �������а�����Ϣ
	private void prepateAllBoardsData() {
		// �Ȳ�ѯ����
		BoardDao dao = new BoardDao();
		int size = dao.getCount();
		System.out.println("���ݿ��¼����" + size);
		if (size < 100) {
			ThreadManager.getInstance().createLongPool()
					.execute(new Runnable() {
						@Override
						public void run() {
							AllBoardProtocol protocol = new AllBoardProtocol();
							protocol.saveAllBoards();
							LogUtil.d("prepateAllBoardsData...����ɹ���");
						}
					});
		}
	}

	public static String getCookie() {
		return cookie;
	}

	public static void setCookie(String ck) {
		cookie = ck;
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
	 * �Զ���½ ����cookie
	 */
	public static String autoLogin(final Context context, boolean autoJump) {
		// �������ļ���ȡid������
		final String id = SPutils.getFromSP("id");
		final String passwd = SPutils.getFromSP("pwd");

		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(passwd)) {
			LogUtil.d("��δ��½��,�Զ���½ʧ�ܣ�");
			// ��ת����½����ȥ
			if (autoJump) {
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("��ǰ������Ҫ��¼�����¼��");
						Intent intent = new Intent(context, LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				});
			}

			return null;
		}

		return autoLogin(id, passwd);

	}

	public static String autoLogin(String id, String pwd) {

		LogUtil.d("�����Զ���½��...");
		// �����½���߼�
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
		params.addBodyParameter("pw", pwd);

		try {
			if (httpUtil == null) {
				httpUtil = new HttpUtils();
			}
			ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST,
					Constants.LOGIN_URL, params);
			// �����ص�������Ϊ�ַ���
			String sb = StreamToStr(responseStream);

			final String result = sb.toString();
			LogUtil.d("��½���:" + result);

			if (result.contains("Net.BBS.setCookie")) {// ��½�ɹ�
				// ��������
				SPutils.saveToSP("id", id);
				SPutils.saveToSP("pwd", pwd);
				// ����cookie
				String cookie = handleCookie(result);
				if (cookie != null) {
					// �����û�����
					updateUserInfo();
					// �ص����̣߳���ʾ��½�ɹ�
					autoLogInOk(result);
					return handleCookie(result);
				}
			} else {// ��½ʧ��
				autoLoginFailed("�Զ���½ʧ��,���ֶ���¼��");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// ��½�ɹ�����ʾ
			autoLoginFailed("��½ʧ�ܣ��������磡");
		}
		return null;

	}

	/**
	 * ��ȡ�û���Ϣ
	 */
	public static UserInfo updateUserInfo() {
		if (userInfo == null) {
			if (userProtocol == null) {
				userProtocol = new UserProtocol();
			}
			userInfo = userProtocol.getUserInfoFromServer(SPutils
					.getFromSP("id"));
			LogUtil.d("��ȡ�û���Ϣ�ɹ�������" + userInfo.toString());
		}

		return userInfo;
	}

	public static String handleCookie(final String result) {
		String reg = "Net.BBS.setCookie\\(\\'" + "(\\d+)" + "N" + "(.*?)\\+"
				+ "(\\d+)" + "\\'\\)";
		Pattern p = Pattern.compile(reg, Pattern.DOTALL);
		Matcher matcher = p.matcher(result);

		String cookie2 = null;
		if (matcher.find()) {
			Integer _U_NUM = Integer.parseInt(matcher.group(1)) + 2;
			String _U_UID = matcher.group(2);
			Integer _U_KEY = Integer.parseInt(matcher.group(3)) - 2;
			
			//����cookie
			//NetUtils2.setMyCookie(_U_NUM+"", _U_UID, _U_KEY+"");
			
			cookie2 = "_U_NUM=" + _U_NUM + ";_U_UID=" + _U_UID + ";_U_KEY="
					+ _U_KEY;

			LogUtil.d("cookie:" + cookie);

			// ����cookie
			BaseApplication.cookie = cookie2;
		}
		return cookie2;
	}

	public static UserInfo getLogedUser() {
		return userInfo;
	}

	public static void setLogedUser(UserInfo user) {
		userInfo = user;
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
