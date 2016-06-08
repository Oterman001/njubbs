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
	public static boolean myTopicUpdated=false;//标记我的帖子更新过
	public static boolean myReplyUpdate=false;//标记我的回帖更新过

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		mainTid = android.os.Process.myTid();
		handler = new Handler();

		// 创建默认的ImageLoader配置参数
		// ImageLoaderConfiguration configuration =
		// ImageLoaderConfiguration.createDefault(this);

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
				this)
				.threadPoolSize(5)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// 缓存的文件数量
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout
																			// (5
																			// s),
																			// readTimeout
																			// (30
																			// s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();// 开始构建

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(configuration);

		prepateAllBoardsData();

		// 检查是否有新的站内信
		// checkHasNewMail();

	}

	private void checkHasNewMail() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				LogUtil.d("检查是否有新邮件啦");
				CheckNewMailProtocol protocol = new CheckNewMailProtocol();
				String url = Constants.HAS_NEW_MAIL_URL;
				newMailCount = protocol.checkFromServer(url, getApplication());
				LogUtil.d("检查结果：" + newMailCount);

			}
		});
	}

	// 保存所有版面信息
	private void prepateAllBoardsData() {
		// 先查询数据
		BoardDao dao = new BoardDao();
		int size = dao.getCount();
		System.out.println("数据库记录数：" + size);
		if (size < 100) {
			ThreadManager.getInstance().createLongPool()
					.execute(new Runnable() {
						@Override
						public void run() {
							AllBoardProtocol protocol = new AllBoardProtocol();
							protocol.saveAllBoards();
							LogUtil.d("prepateAllBoardsData...保存成功！");
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
	 * 自动登陆 返回cookie
	 */
	public static String autoLogin(final Context context, boolean autoJump) {
		// 从配置文件获取id和密码
		final String id = SPutils.getFromSP("id");
		final String passwd = SPutils.getFromSP("pwd");

		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(passwd)) {
			LogUtil.d("从未登陆过,自动登陆失败！");
			// 跳转到登陆界面去
			if (autoJump) {
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.toast("当前界面需要登录，请登录！");
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

		LogUtil.d("尝试自动登陆中...");
		// 处理登陆的逻辑
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
		params.addBodyParameter("pw", pwd);

		try {
			if (httpUtil == null) {
				httpUtil = new HttpUtils();
			}
			ResponseStream responseStream = httpUtil.sendSync(HttpMethod.POST,
					Constants.LOGIN_URL, params);
			// 将返回的流解析为字符串
			String sb = StreamToStr(responseStream);

			final String result = sb.toString();
			LogUtil.d("登陆结果:" + result);

			if (result.contains("Net.BBS.setCookie")) {// 登陆成功
				// 保存起来
				SPutils.saveToSP("id", id);
				SPutils.saveToSP("pwd", pwd);
				// 处理cookie
				String cookie = handleCookie(result);
				if (cookie != null) {
					// 更新用户数据
					updateUserInfo();
					// 回到主线程，提示登陆成功
					autoLogInOk(result);
					return handleCookie(result);
				}
			} else {// 登陆失败
				autoLoginFailed("自动登陆失败,请手动登录！");
			}

		} catch (Exception e) {
			e.printStackTrace();
			// 登陆成功后显示
			autoLoginFailed("登陆失败，请检查网络！");
		}
		return null;

	}

	/**
	 * 获取用户信息
	 */
	public static UserInfo updateUserInfo() {
		if (userInfo == null) {
			if (userProtocol == null) {
				userProtocol = new UserProtocol();
			}
			userInfo = userProtocol.getUserInfoFromServer(SPutils
					.getFromSP("id"));
			LogUtil.d("获取用户信息成功。。。" + userInfo.toString());
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
			
			//处理cookie
			//NetUtils2.setMyCookie(_U_NUM+"", _U_UID, _U_KEY+"");
			
			cookie2 = "_U_NUM=" + _U_NUM + ";_U_UID=" + _U_UID + ";_U_KEY="
					+ _U_KEY;

			LogUtil.d("cookie:" + cookie);

			// 保存cookie
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
				LogUtil.d("自动登陆成功！");
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
