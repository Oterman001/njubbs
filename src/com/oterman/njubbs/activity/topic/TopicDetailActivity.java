package com.oterman.njubbs.activity.topic;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.activity.board.BoardDetailActivity;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.activity.mail.MailNewActivity;
import com.oterman.njubbs.activity.topic.TopicDetailActivity.TopicDetailAdapter.ViewHolder;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.dialog.ChosePicDialog;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.holders.OptionsDialogHolder;
import com.oterman.njubbs.holders.OptionsDialogHolder.MyOnclickListener;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.smiley.SelectFaceHelper;
import com.oterman.njubbs.smiley.SelectFaceHelper.OnFaceOprateListener;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.TopicUtils;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;

@SuppressLint("NewApi")
public class TopicDetailActivity extends BaseActivity implements
		OnClickListener {

	private List<TopicDetailInfo> list;
	private TopicDetailAdapter adapter;

	private TopicInfo originTopicInfo;
	private TopicDetailProtocol protocol;
	private PullToRefreshListView pLv;
	private View view;
	ActionBar actionBar;
	private EditText etContent;
	private CheckBox cbSmiley;
	private ImageButton ibSend;
	private View addFaceToolView;
	private WaitDialog waitDialog;
	SelectFaceHelper mFaceHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void initViews() {
		// 判断是否为点击其他链接进入
		Intent intent = getIntent();
		Uri contentUri = intent.getData();

		if (contentUri != null) {// 为点击其他链接进入
			// http://bbs.nju.edu.cn/bbstcon?board=Pictures&file=M.1465637109.A
			String uri = contentUri.toString();
			// 自己拼装
			originTopicInfo = createInfoFromUrl(uri);

		} else {
			originTopicInfo = (TopicInfo) getIntent().getSerializableExtra(
					"topicInfo");
		}// 正常进入

		tvBarTitle.setText(originTopicInfo.board + "(点击进入)");
		ibShare = (ImageButton) actionBarView.findViewById(R.id.btn_share);
		ibShare.setVisibility(View.VISIBLE);
		ibShare.setOnClickListener(this);

		// 给actionbar添加点击事件 点击后进入到对应的版面
		tvBarTitle.setClickable(true);
		tvBarTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BoardDetailActivity.class);
				intent.putExtra("boardUrl", originTopicInfo.boardUrl);
				startActivity(intent);
				// 结束掉
				finish();
			}
		});
	}

	private TopicInfo createInfoFromUrl(String url) {
		// http://bbs.nju.edu.cn/bbstcon?board=Pictures&file=M.1465637109.A
		String board = url.substring(url.indexOf('=') + 1, url.indexOf('&'));
		String contentUrl = url.substring(url.indexOf("/bbstcon") + 1);

		// board?board=Pictures
		String boardUrl = "board?board=" + board;

		TopicInfo info = new TopicInfo(board, "", boardUrl, contentUrl);

		return info;
	}

	/*
	 * 从服务器中加载数据
	 */
	public LoadingState loadDataFromServer() {
		String url = Constants.getContentUrl(originTopicInfo.contentUrl);
		protocol = new TopicDetailProtocol();
		list = protocol.loadFromServer(url, false);
		return list == null || list.size() == 0 ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	// 联网成功后创建视图
	public View createSuccessView() {

		view = View.inflate(getApplicationContext(),
				R.layout.activity_topic_detail, null);
		pLv = (PullToRefreshListView) view.findViewById(R.id.pLv);
		if (list.size() != 0) {

			louzhu = list.get(0).author;
			originTopicInfo.title = list.get(0).title;
			originTopicInfo.author = louzhu.substring(0, louzhu.indexOf("("))
					.trim();

		}

		// 初始化底部回帖view
		initReplyViews();

		pLv.setMode(Mode.PULL_FROM_END);// 上拉加载更多

		// 添加头布局
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		View headerView = initHeaderView();

		headerView.setLayoutParams(layoutParams);

		ListView lv = pLv.getRefreshableView();
		lv.addHeaderView(headerView);

		// lv.setDivider(new ColorDrawable(Color.GRAY));
		lv.setDivider(new ColorDrawable(0x88888888));
		lv.setDividerHeight(UiUtils.dip2px(1));

		lv.setDividerHeight(1);

		// 滑动的时候不加载图片
		lv.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), true, true));

		adapter = new TopicDetailAdapter();
		pLv.setAdapter(adapter);
		pLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pLv.getLoadingLayoutProxy().setRefreshingLabel("正在加载...嘿咻嘿咻");
				pLv.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
				pLv.getLoadingLayoutProxy().setReleaseLabel("松手开始加载");

				onLoadingMore();

			}
		});
		// 设置条目点击监听
		pLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtil.d("点击了哦" + position);
				// TopicDetailInfo detailInfo = list.get(position);
				//
				// MyToast.toast(detailInfo.toString());
			}
		});

		// 设置长点击事件
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 1) {
					handleItemLongClick(position);
				}
				return true;
			}
		});

		return view;
	}

	/*
	 * 初始化回帖布局
	 */
	private void initReplyViews() {
		etContent = (EditText) view.findViewById(R.id.et_reply_content);// 内容
		cbSmiley = (CheckBox) view.findViewById(R.id.cb_reply_smiley);// 笑脸
		cbChosePic = (CheckBox) view.findViewById(R.id.cb_chose_pic);// 图片
		ibSend = (ImageButton) view.findViewById(R.id.ib_reply_send);// 发送

		addFaceToolView = view.findViewById(R.id.add_tool);// 表情集合

		cbChosePic.setOnClickListener(this);
		ibSend.setOnClickListener(this);
		// 触摸输入框 隐藏表情键盘
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				addFaceToolView.setVisibility(View.GONE);
				cbSmiley.setChecked(false);
				return false;
			}
		});
		// 点击笑脸，切换表情键盘状态
		cbSmiley.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(
							TopicDetailActivity.this, addFaceToolView);
					// 点击表情时，设置监听
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
				}
				if (!isChecked) {
					// isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
					// isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(TopicDetailActivity.this);// 隐藏软键盘
				}
			}
		});
	}

	// 初始化头布局
	private View initHeaderView() {
		View view = View.inflate(getApplicationContext(),
				R.layout.topic_detail_header, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_topic_titile);
		TextView tvReplyeCount = (TextView) view
				.findViewById(R.id.tv_topic_replycount);

		tvTitle.setText(originTopicInfo.title);

		if (originTopicInfo.replyCount == null) {
			tvReplyeCount.setText("共" + list.size() + "条回复");
		} else {
			String str = originTopicInfo.replyCount;
			if (str.contains("/")) {
				str = str.split("/")[0];
			}

			tvReplyeCount.setText("共" + str + "条回复");
		}
		return view;
	}

	/*
	 * 处理长按点击
	 */
	private void handleItemLongClick(int position) {

		final TopicDetailInfo detailInfo = list.get(position - 2);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				TopicDetailActivity.this);

		final String author = detailInfo.author.substring(0,
		// mmlover(xxx) 处理id
				detailInfo.author.indexOf('(')).trim();
		final String floorth = detailInfo.floorth;

		OptionsDialogHolder holder = new OptionsDialogHolder(
				getApplicationContext(), author, true);

		builder.setTitle("请选择操作");
		builder.setView(holder.getRootView());

		builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog optionsDialog = builder.create();

		holder.setListener(new MyOnclickListener() {
			@Override
			public void onDelete() {
				handleDelReply(detailInfo, optionsDialog);
				optionsDialog.dismiss();
			}

			@Override
			public void OnQueryAuthurDetail() {
				// 处理查看用户信息
				handleShowUserDetail(author, optionsDialog);
			}

			@Override
			public void OnModify() {
				optionsDialog.dismiss();
				// 跳转到修改页面
				Intent intent = new Intent(TopicDetailActivity.this,
						ModifyReplyActivity.class);
				detailInfo.title = originTopicInfo.title;
				intent.putExtra("topicDetailInfo", detailInfo);
				startActivityForResult(intent, 100);

			}

			@Override
			public void OnMailTo() {
				optionsDialog.dismiss();
				Intent intent = new Intent(getApplicationContext(),
						MailNewActivity.class);
				if (originTopicInfo != null) {
					intent.putExtra("receiver", author);
				}
				startActivity(intent);
			}

			@Override
			public void onReplyFloor() {
				optionsDialog.dismiss();
				// 处理回复具体某一楼
				etContent.setText("@" + floorth + "楼-" + author + ":");
				int length = etContent.getText().toString().length();
				etContent.setSelection(length);
			}

			@Override
			public void onQueryTopicHis() {
				optionsDialog.dismiss();
				// String author = topicInfo.author;
				Intent intent = new Intent(getApplicationContext(),
						MyTopicActivity.class);
				intent.putExtra("author", author);
				startActivity(intent);
			}

		});

		optionsDialog.show();
	}

	/**
	 * 处理查看用户信息
	 * 
	 * @param userId
	 * @param replyDialog
	 */
	protected void handleShowUserDetail(String userId,
			final AlertDialog replyDialog) {
		replyDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog = null;
		UserDetailHolder holder = new UserDetailHolder(this);
		// 更新用户详情
		holder.updateStatus(userId);
		builder.setView(holder.getRootView());
		dialog = builder.show();
		holder.setOwnerDialog(dialog);
	}

	/**
	 * 删除回帖
	 */
	private void handleDelReply(final TopicDetailInfo detailInfo,
			final AlertDialog replyDialog) {
		if (waitDialog == null) {
			waitDialog = new WaitDialog(this);
		}
		waitDialog.setMessage("努力的删帖中。。。");
		waitDialog.show();

		ThreadManager.getInstance().createShortPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();
				String url = Constants.getReplyDelUrl(detailInfo.replyUrl);
				LogUtil.d("删除回帖链接：" + url);
				try {
					RequestParams rp = new RequestParams();
					// 先自动登陆
					String cookie = BaseApplication.getCookie();
					if (cookie == null) {
						cookie = BaseApplication.autoLogin(
								TopicDetailActivity.this, true);
					}

					rp.addHeader("Cookie", cookie);

					ResponseStream responseStream = httpUtils.sendSync(
							HttpMethod.GET, url, rp);

					String result = BaseApplication.StreamToStr(responseStream);
					LogUtil.d("删除回帖结果：" + result);

					if (result.contains("返回本讨论区")) {// 删除成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("删除成功");
								list.remove(detailInfo);
								adapter.notifyDataSetChanged();
							}
						});
					} else if (result.contains("无权")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("删除失败，无权删除该文");
								adapter.notifyDataSetChanged();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("删除失败，请检查网络");
							waitDialog.dismiss();
						}
					});
				}
			}
		});
	}

	/**
	 * 回帖
	 * 
	 * @param content
	 */
	private void handleReplyTopic(final String content) {

		waitDialog = new WaitDialog(this);
		waitDialog.setMessage("努力的回帖中。。。");
		waitDialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;

			@Override
			public void run() {
				try {

					if (httpUtils == null) {
						httpUtils = new HttpUtils();
					}

					// 服务器编码为gbk
					RequestParams params = new RequestParams("gbk");

					// 处理参数
					handlePostParams(content, params);
					// post提交
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,
							Constants.getNewTopicUrl(originTopicInfo.board),
							params);

					String result = BaseApplication.StreamToStr(stream);

					LogUtil.d("回帖结果：" + result);

					if (result.contains("匆匆过客")) {// 发帖失败了。

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (waitDialog != null) {
									waitDialog.dismiss();
								}
								waitDialog.dismiss();
								MyToast.toast("自动登陆失败，请登录！");
								cbSmiley.setChecked(false);
								// 跳转到登陆页面
								// Intent intent = new Intent(
								// TopicDetailActivity.this,
								// LoginActivity.class);
								// startActivity(intent);
							}
						});

					} else if (result.contains("发文间隔过密")) {
						// 更新界面
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (waitDialog != null) {
									waitDialog.dismiss();
								}
								waitDialog.dismiss();
								MyToast.toast("回帖失败！发文间隔过密！");
								// 刷新界面
								cbSmiley.setChecked(false);
							}
						});
					} else {
						// 回帖成功
						String url = Constants
								.getContentUrl(originTopicInfo.contentUrl);
						List<TopicDetailInfo> tempList = protocol
								.loadFromServer(url, false);

						String mailto_louzhu = SPutils
								.getFromSP("mailto_louzhu");
						String mailto_at = SPutils.getFromSP("mailto_at");

						// 检查是否有@，如果有，发送站内
						if (!"no".equals(mailto_at)) {
							handleSendMailToAt(content);
						}
						
						// 回帖提醒，给楼主发站内信
						if (!"no".equals(mailto_louzhu)) {
							// 检查是否为楼主自己回帖，自己回帖时， 不提醒
							handleSendMailToLouzhu(content);
						}

						// 重新请求数据 刷新界面
						if (protocol == null) {
							protocol = new TopicDetailProtocol();
						}

						if (tempList != null || tempList.size() > 0) {
							list.clear();
							list.addAll(tempList);
							// 更新界面
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (waitDialog != null) {
										waitDialog.dismiss();
									}
									waitDialog.dismiss();
									MyToast.toast("回帖成功！");
									// 刷新界面
									adapter.notifyDataSetChanged();
									// pLv.getRefreshableView().setSelection(list.size());
									etContent.setText("");
									cbSmiley.setChecked(false);
								}
							});
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
					// 更新界面
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (waitDialog != null) {
								waitDialog.dismiss();
							}
							waitDialog.dismiss();
							MyToast.toast("回复失败，请检查网络！");
							// 刷新界面
							cbSmiley.setChecked(false);
						}
					});
				}

			}

			/**
			 * 处理回帖时提交的参数
			 * 
			 * @param content
			 * @param params
			 */
			private void handlePostParams(final String content,
					RequestParams params) {
				String title = "Re:" + originTopicInfo.title;
				// 处理中文问题
				String reusr = originTopicInfo.author;

				// file=M.1463541584.A 获取数字 reid 参数
				Pattern p = Pattern.compile("file=M\\.(\\d+)\\.");
				Matcher matcher = p.matcher(originTopicInfo.contentUrl);
				String reid = null;
				if (matcher.find()) {
					reid = matcher.group(1);
				}

				// 添加cookie 自动登陆；
				String cookie = BaseApplication.getCookie();
				if (cookie == null) {
					cookie = BaseApplication.autoLogin(
							TopicDetailActivity.this, true);
				}

				// 获取pid
				String pid = getPid(cookie,
						Constants.getReplyPageUrl(originTopicInfo.contentUrl));

				// 添加参数 共有7个
				params.addBodyParameter("title", title);
				params.addBodyParameter("pid", pid);
				params.addBodyParameter("reid", reid);

				params.addBodyParameter("signature", 1 + "");
				params.addBodyParameter("autocr", "on");
				params.addBodyParameter("reusr", reusr);
				// 处理手动换行

				// String content2=content+"\n-\n"+"sent from 小百合\n";
				String content2 = UiUtils.addNewLineMark(content)
						+ SPutils.getTail();

				params.addBodyParameter("text", content2);

				params.addHeader("Cookie", cookie);
			}

			private String getPid(String cookie, String url) {

				RequestParams rp = new RequestParams();
				rp.setHeader("Cookie", cookie);

				try {
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,
							url, rp);

					String result = BaseApplication.StreamToStr(stream);
					Document doc = Jsoup.parse(result);

					String pid = doc.select("input[name=pid]").get(0)
							.attr("value");

					LogUtil.d("pid:" + pid);
					return pid;
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
		});

	}

	/**
	 * 给楼主发信息
	 */
	protected void handleSendMailToLouzhu(String content) {
		final String receiver = originTopicInfo.author;
		final String title = SPutils.getFromSP("id") + "回复了帖子【"
				+ originTopicInfo.title + "】";
		String contentUrl = Constants.getContentUrl(originTopicInfo.contentUrl);

		final String mailContent = "回帖内容:\n***************\n" + content
				+ "\n***************\n详情请戳：" + contentUrl
				+SPutils.getAdTail();

		// 检查回帖人是否为楼主自己，如果是，不提醒
		if (receiver.equals(SPutils.getFromSP("id"))) {
			// 回复者和楼主同人，不发送站内提醒；
		} else {
			sendMail(receiver, title, mailContent, "站内楼主提醒成功！");
		}
		
	}

	/**
	 * 处理给有@的发站内提醒
	 */
	protected void handleSendMailToAt(final String content) {
		Pattern p = Pattern.compile("@\\d{1,3}楼-([a-zA-Z0-9]*?):.*");
		Matcher matcher = p.matcher(content);
		if (matcher.find()) {// 确实有at情况
			final String receiver = matcher.group(1).trim();
			String louzhu=originTopicInfo.author;
			//判断是否@楼主并且给楼主发站内开启
			if(louzhu.equals(receiver)&&"yes".equals(SPutils.getFromSP("mailto_louzhu"))){
				return ;
			}
			
			final String title = SPutils.getFromSP("id") + "在帖子【"
					+ originTopicInfo.title + "】提到了你";
			String contentUrl = Constants
					.getContentUrl(originTopicInfo.contentUrl);
			final String mailContent = "我在帖子【" + originTopicInfo.title
					+ "】中提到了你:\n***************\n" + content
					+ "\n***************\n帖子详情请戳：" + contentUrl
					+SPutils.getAdTail();
			// 发送邮件
			sendMail(receiver, title, mailContent, "站内@后作者提醒成功！");
		}
	}

	/**
	 * 处理发邮件
	 */
	private void sendMail(final String receiver, final String title,
			final String mailContent, final String message) {
		// 给他发站内
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;

			@Override
			public void run() {
				// 回帖逻辑
				try {
					if (httpUtils == null) {
						httpUtils = new HttpUtils();
					}
					RequestParams rp = new RequestParams("gbk");

					rp.addQueryStringParameter("pid", "0");
					rp.addQueryStringParameter("userid", "");

					rp.addBodyParameter("signature", "1");
					rp.addBodyParameter("userid", receiver);
					rp.addBodyParameter("title", title);

					rp.addBodyParameter("text", mailContent);

					// 添加cookie
					String cookie = BaseApplication.getCookie();
					if (cookie == null) {// 自动登陆
						cookie = BaseApplication.autoLogin(
								TopicDetailActivity.this, true);
					}

					rp.addHeader("Cookie", cookie);

					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,
							Constants.REPLY_MAIL_URL, rp);

					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("发站内结果：" + result);

					if (result.contains("信件已寄给")) {// 成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								MyToast.toast(message);
							}
						});
					} else {// 回信失败
						LogUtil.d("回信失败");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// MyToast.toast("自动登陆失败，请手动登录");

							}
						});
					}

				} catch (final Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// MyToast.toast("发信失败！"+e.getMessage());
						}
					});
				}
			}
		});
	}

	public static String addNewLineMark(String str) {
		StringBuffer sb = new StringBuffer(str);

		for (int i = 40; i < sb.length(); i += 41) {
			sb.insert(i, "\n");
		}

		return sb.toString();
	}

	/**
	 * 点击发送按钮 发送回帖
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_reply_send:// 处理发帖
			// 校验数据
			String content = etContent.getText().toString();
			if (TextUtils.isEmpty(content)) {
				MyToast.toast("内容不能为空哦!");
				return;
			}
			handleReplyTopic(content);
			break;

		case R.id.btn_share:
			// MyToast.toast("分享成功");
			showShare();
			break;

		case R.id.cb_chose_pic:// 打开图库 选择图片
			// ChosePicDialog picDialog = new ChosePicDialog(200,this);
			// picDialog.show();
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			this.startActivityForResult(intent, 200);

			break;

		default:
			break;
		}
	}
	
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		String url = "http://bbs.nju.edu.cn/" + originTopicInfo.contentUrl;

		String content = list.get(0).content;
		content = content.replaceAll("<br>", "\n");
		content = content.replaceAll("\\s+<img.*?/>\\s+", "");

		String title = originTopicInfo.title;
		

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		String conntent = "我在南大小百合" + originTopicInfo.board + "版看到一篇帖子：["
				+ title + "]\n详情请查看：" + url + "\n--分享自南大小百合安卓客户端";
		oks.setText(conntent);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		 oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		
//		oks.setImageUrl("http://bbs.nju.edu.cn/file/O/oterman/icon_108_round.png");
		
		oks.setImageUrl("http://bbs.nju.edu.cn/file/O/oterman/icon_green_oterman.png");
		
//		http://bbs.nju.edu.cn/file/O/oterman/icon_null.png
//		http://bbs.nju.edu.cn/file/O/oterman/icon_white.png

		// url仅在微信（包括好友和朋友圈）中使用
//		String path = "file:///android_asset/icon_512.png";
//		oks.setImagePath(path);
		
		oks.setUrl(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);

		// 启动分享GUI
		oks.show(this);
	}

	/**
	 * 加载下一页数据
	 */
	public void onLoadingMore() {

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			private List<TopicDetailInfo> moreList;

			@Override
			public void run() {
				if (protocol == null) {
					protocol = new TopicDetailProtocol();
				}

				String loadMoreUrl = list.get(list.size() - 1).loadMoreUrl;
				if (loadMoreUrl != null) {
					moreList = protocol.loadFromServer(
							Constants.getContentUrl(loadMoreUrl), false);
				}

				UiUtils.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (moreList != null && moreList.size() != 0) {
							moreList.remove(0);
							list.addAll(moreList);
							adapter.notifyDataSetChanged();
							MyToast.toast("加载成功！");
						} else {// 没有更多
							MyToast.toast("欧哦，没有更多了");
						}
						// 加载完成，通知回掉
						pLv.onRefreshComplete();
					}
				});

			}
		});

	}

	// 表情点击的监听事件
	OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
		@Override
		public void onFaceSelected(SpannableString spanEmojiStr) {
			if (null != spanEmojiStr) {
				// 在光标处插入表情
				String oriText = etContent.getText().toString();// 原始文字
				int index = Math.max(etContent.getSelectionStart(), 0);// 获取光标处位置，没有光标，返回-1

				StringBuffer sb = new StringBuffer(oriText);
				sb.insert(index, spanEmojiStr);
				String string = sb.toString().replaceAll("\n", "<br>");

				Spanned spanned = Html.fromHtml(string);
				CharSequence text = SmileyParser.getInstance(
						getApplicationContext()).strToSmiley(spanned);
				etContent.setText(text);

				etContent.setSelection(index + spanEmojiStr.length());
				// etContent.append(spanEmojiStr);
			}
		}

		@Override
		public void onFaceDeleted() {
			int selection = etContent.getSelectionStart();
			String text = etContent.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					etContent.getText().delete(start, end);
					return;
				}
				etContent.getText().delete(selection - 1, selection);
			}
		}
	};

	private ImageButton ibShare;
	private String louzhu;
	private CheckBox cbChosePic;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {// 修改回帖成功后跳转 刷新
			updateModifiedViews();
		} else if (requestCode == 200&&data!=null) {//从图库选中图片返回
			// 从intent中得到选中图片的路径
			String picturePath = TopicUtils.getPicPathFromUri(TopicDetailActivity.this, data);
			// 展示选中的图片,上传逻辑包含在其中
			TopicUtils.showChosedPic(TopicDetailActivity.this, picturePath,etContent);
		}
		
	}

	/**
	 * 修改帖子成功后，返回时更新页面
	 */
	private void updateModifiedViews() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				String url = Constants
						.getContentUrl(originTopicInfo.contentUrl);
				if (protocol == null) {
					protocol = new TopicDetailProtocol();
				}

				List<TopicDetailInfo> tempList = protocol.loadFromServer(url,
						false);
				if (tempList != null && tempList.size() > 0) {
					list.clear();
					list.addAll(tempList);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});

				}
			}
		});
	}

	/*
	 * 处理上传图片的逻辑
	 */
	protected void handleUploadPic(final Bitmap bitmap) {
		final WaitDialog waitDialog = new WaitDialog(this);
		waitDialog.setMessage("正在努力上传。。");
		waitDialog.show();

		// 将bitmap缓存到本地
		String dirPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/njubbs/photo/";
		File dirFile = new File(dirPath);
		if (!dirFile.exists())
			dirFile.mkdirs();
		// njubbskdsadjkfa.jpg
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

		String date2 = sdf.format(date);

		// String filename="nju_bbs"+date2+".jpg";
		String filename = "bbs" + date2 + ".jpg";

		UiUtils.saveBitmapToLocal(bitmap, filename);

		File file = new File(dirFile, filename);
		TopicUtils.uploadFile(this, waitDialog, file, etContent);
	}

	// 隐藏软键盘
	public void hideInputManager(Context ct) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(((Activity) ct).getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			Log.e("", "hideInputManager Catch error,skip it!", e);
		}
	}

	class TopicDetailAdapter extends BaseAdapter {
		SmileyParser sp = SmileyParser.getInstance(getApplicationContext());
		Random r = new Random();

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = View.inflate(TopicDetailActivity.this,
						R.layout.list_item_topic_detial, null);

				holder.tvAuthor = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_author);

				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_content);
				holder.tvFloorth = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_floorth);
				holder.tvPubTime = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_pubtime);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TopicDetailInfo info = list.get(position);
			// mmlover(mmlover)
			String author = info.author;
			// author=author.replaceAll("\\)", "");
			// author=author.replaceFirst("\\(", "\n");
			// author=author.replaceFirst("\\(","\n(" );

			// 做标记
			// if (author != null && originTopicInfo.author != null
			// && author.contains(originTopicInfo.author)) {
			if (author != null && louzhu != null && author.equals(louzhu)) {
				author = " 楼主 " + author;
				SpannableStringBuilder ssb = new SpannableStringBuilder(author);
				int start = 0;
				int end = start + " 楼主 ".length();

				ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new AbsoluteSizeSpan(UiUtils.dip2px(12)), start,
						end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.tvAuthor.setText(ssb);
			} else {
				String id = SPutils.getFromSP("id");
				if (!TextUtils.isEmpty(id) && author.contains(id)) {
					author = " 我 " + author;
					SpannableStringBuilder ssb = new SpannableStringBuilder(
							author);
					int start = 0;
					int end = start + " 我 ".length();

					ssb.setSpan(new BackgroundColorSpan(0xFF8a2be2), start,
							end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start,
							end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					holder.tvAuthor.setText(ssb);
				} else {
					holder.tvAuthor.setText(author);
				}
			}
			// 超链接可点击
			holder.tvContent.setAutoLinkMask(Linkify.WEB_URLS
					| Linkify.EMAIL_ADDRESSES);
			holder.tvContent.setMovementMethod(ScrollingMovementMethod
					.getInstance());// 设置可滚动
			holder.tvContent
					.setMovementMethod(LinkMovementMethod.getInstance());// 设置超链接可以打开网页

			Spanned spanned = Html.fromHtml(info.content, new URLImageParser(
					holder.tvContent),
					new MyTagHandler(getApplicationContext()));

			holder.tvContent.setText(sp.strToSmiley(spanned));
			holder.tvContent.invalidate();

			holder.tvFloorth.setText("第" + info.floorth + "楼");
			holder.tvPubTime.setText(info.pubTime);

			// if (position % 2 == 0) {
			// convertView.setBackgroundColor(0xFFEBEBEB);
			// } else {
			// convertView.setBackgroundColor(0xAAD0D0E0);
			// }

			Drawable drawable;

//			if (r.nextInt(2) % 2 != 0) {
//				drawable = getResources().getDrawable(
//						R.drawable.ic_gender_female);
//			} else {
//				drawable = getResources()
//						.getDrawable(R.drawable.ic_gender_male);
//			}
//
//			// 随机设置左边的图标
//			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//					drawable.getMinimumHeight());
//			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

			return convertView;
		}

		class ViewHolder {
			public TextView tvContent;
			public TextView tvAuthor;
			public TextView tvPubTime;
			public TextView tvFloorth;
		}

	}

}
