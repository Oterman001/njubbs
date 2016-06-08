package com.oterman.njubbs.activity.topic;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
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
	public void initViews() {

		ibShare = (ImageButton) actionBarView.findViewById(R.id.btn_share);
		ibShare.setVisibility(View.VISIBLE);
		ibShare.setOnClickListener(this);

		originTopicInfo = (TopicInfo) getIntent().getSerializableExtra("topicInfo");
		tvBarTitle.setText(originTopicInfo.board + "(�������)");

		// ��actionbar��ӵ���¼� �������뵽��Ӧ�İ���
		tvBarTitle.setClickable(true);
		tvBarTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BoardDetailActivity.class);
				intent.putExtra("boardUrl", originTopicInfo.boardUrl);
				startActivity(intent);
				// ������
				finish();
			}
		});
	}

	/**
	 * ������һҳ����
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
							MyToast.toast("���سɹ���");
						} else {// û�и���
							MyToast.toast("ŷŶ��û�и�����");
						}
						// ������ɣ�֪ͨ�ص�
						pLv.onRefreshComplete();
					}
				});

			}
		});

	}

	/*
	 * �ӷ������м�������
	 */
	public LoadingState loadDataFromServer() {
		String url = Constants.getContentUrl(originTopicInfo.contentUrl);
		protocol = new TopicDetailProtocol();
		list = protocol.loadFromServer(url, false);
		return list == null||list.size()==0 ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	// �����ɹ��󴴽���ͼ
	public View createSuccessView() {

		view = View.inflate(getApplicationContext(),
				R.layout.activity_topic_detail, null);
		pLv = (PullToRefreshListView) view.findViewById(R.id.pLv);
		
		louzhu = list.get(0).author;
		
		// ��ʼ���ײ�����view
		initReplyViews();

		pLv.setMode(Mode.PULL_FROM_END);// �������ظ���

		// ���ͷ����
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

		// ������ʱ�򲻼���ͼƬ
		lv.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), true, true));

		adapter = new TopicDetailAdapter();
		pLv.setAdapter(adapter);
		pLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pLv.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���...���ݺ���");
				pLv.getLoadingLayoutProxy().setPullLabel("�������ظ���");
				pLv.getLoadingLayoutProxy().setReleaseLabel("���ֿ�ʼ����");

				onLoadingMore();

			}
		});
		// ������Ŀ�������
		pLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtil.d("�����Ŷ" + position);
				// TopicDetailInfo detailInfo = list.get(position);
				//
				// MyToast.toast(detailInfo.toString());
			}
		});

		// ���ó�����¼�
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position>1){
					handleItemLongClick(position);
				}
				return true;
			}
		});

		return view;
	}

	/*
	 * ��ʼ����������
	 */
	private void initReplyViews() {
		etContent = (EditText) view.findViewById(R.id.et_reply_content);// ����
		cbSmiley = (CheckBox) view.findViewById(R.id.cb_reply_smiley);// Ц��
		ibSend = (ImageButton) view.findViewById(R.id.ib_reply_send);// ����

		addFaceToolView = view.findViewById(R.id.add_tool);// ���鼯��

		ibSend.setOnClickListener(this);
		// ��������� ���ر������
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				addFaceToolView.setVisibility(View.GONE);
				cbSmiley.setChecked(false);
				return false;
			}
		});
		// ���Ц�����л��������״̬
		cbSmiley.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(
							TopicDetailActivity.this, addFaceToolView);
					// �������ʱ�����ü���
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
				}
				if (!isChecked) {
					// isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
					// isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(TopicDetailActivity.this);// ���������
				}
			}
		});
	}

	// ��ʼ��ͷ����
	private View initHeaderView() {
		View view = View.inflate(getApplicationContext(),
				R.layout.topic_detail_header, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_topic_titile);
		TextView tvReplyeCount = (TextView) view
				.findViewById(R.id.tv_topic_replycount);
		tvTitle.setText(originTopicInfo.title);

		if (originTopicInfo.replyCount == null) {
			tvReplyeCount.setText("��" + list.size() + "���ظ�");
		} else {
			String str = originTopicInfo.replyCount;
			if (str.contains("/")) {
				str = str.split("/")[0];
			}

			tvReplyeCount.setText("��" + str + "���ظ�");
		}
		return view;
	}

	/*
	 * ���������
	 */
	private void handleItemLongClick(int position) {

		final TopicDetailInfo detailInfo = list.get(position - 2);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				TopicDetailActivity.this);

		// mmlover(xxx) ����id
		final String author = detailInfo.author.substring(0,
				detailInfo.author.indexOf('(')).trim();

		OptionsDialogHolder holder = new OptionsDialogHolder(
				getApplicationContext(), author,true);

		builder.setTitle("��ѡ�����");
		builder.setView(holder.getRootView());

		builder.setNegativeButton("ȡ��", new AlertDialog.OnClickListener() {
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
				// ����鿴�û���Ϣ
				handleShowUserDetail(author, optionsDialog);
			}

			@Override
			public void OnModify() {
				optionsDialog.dismiss();
				// ��ת���޸�ҳ��
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
				//����ظ�����ĳһ¥
				etContent.setText("@"+author+":");
			}

			@Override
			public void onQueryTopicHis() {
				optionsDialog.dismiss();
//				String author = topicInfo.author;
				Intent intent=new Intent(getApplicationContext(),MyTopicActivity.class);
				intent.putExtra("author", author);
				startActivity(intent);
				
			}
		});

		optionsDialog.show();
	}

	/**
	 * ����鿴�û���Ϣ
	 * @param userId
	 * @param replyDialog
	 */
	protected void handleShowUserDetail(String userId,final AlertDialog replyDialog) {
		replyDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog dialog=null;
		UserDetailHolder holder = new UserDetailHolder(this);
		// �����û�����
		holder.updateStatus(userId);
		builder.setView(holder.getRootView());
		dialog=builder.show();
		holder.setOwnerDialog(dialog);
	}

	/**
	 * ɾ������
	 */
	private void handleDelReply(final TopicDetailInfo detailInfo,
			final AlertDialog replyDialog) {
		if (waitDialog == null) {
			waitDialog = new WaitDialog(this);
		}
		waitDialog.setMessage("Ŭ����ɾ���С�����");
		waitDialog.show();

		ThreadManager.getInstance().createShortPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();
				String url = Constants.getReplyDelUrl(detailInfo.replyUrl);
				LogUtil.d("ɾ���������ӣ�" + url);
				try {
					RequestParams rp = new RequestParams();
					// ���Զ���½
					String cookie = BaseApplication.getCookie();
					if (cookie == null) {
						cookie = BaseApplication.autoLogin(TopicDetailActivity.this,true);
					}

					rp.addHeader("Cookie", cookie);

					ResponseStream responseStream = httpUtils.sendSync(
							HttpMethod.GET, url, rp);

					String result = BaseApplication.StreamToStr(responseStream);
					LogUtil.d("ɾ�����������" + result);

					if (result.contains("���ر�������")) {// ɾ���ɹ�
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("ɾ���ɹ�");
								list.remove(detailInfo);
								adapter.notifyDataSetChanged();
							}
						});
					} else if (result.contains("��Ȩ")) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								waitDialog.dismiss();
								MyToast.toast("ɾ��ʧ�ܣ���Ȩɾ������");
								adapter.notifyDataSetChanged();
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("ɾ��ʧ�ܣ���������");
							waitDialog.dismiss();
						}
					});
				}
			}
		});
	}

	/**
	 * ����
	 * @param content
	 */
	private void handleReplyTopic(final String content) {

		waitDialog = new WaitDialog(this);
		waitDialog.setMessage("Ŭ���Ļ����С�����");
		waitDialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;

			@Override
			public void run() {

				try {

					if (httpUtils == null) {
						httpUtils = new HttpUtils();
					}

					// ����������Ϊgbk
					RequestParams params = new RequestParams("gbk");

					// �������
					handlePostParams(content, params);
					// post�ύ
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,
							Constants.getNewTopicUrl(originTopicInfo.board), params);

					String result = BaseApplication.StreamToStr(stream);

					LogUtil.d("���������" + result);

					if (result.contains("�Ҵҹ���")) {// ����ʧ���ˡ�

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (waitDialog != null) {
									waitDialog.dismiss();
								}
								waitDialog.dismiss();
								MyToast.toast("�Զ���½ʧ�ܣ����¼��");
								cbSmiley.setChecked(false);
								// ��ת����½ҳ��
//								Intent intent = new Intent(
//										TopicDetailActivity.this,
//										LoginActivity.class);
//								startActivity(intent);
							}
						});

					} else if (result.contains("���ļ������")) {
						// ���½���
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (waitDialog != null) {
									waitDialog.dismiss();
								}
								waitDialog.dismiss();
								MyToast.toast("����ʧ�ܣ����ļ�����ܣ�");
								// ˢ�½���
								cbSmiley.setChecked(false);
							}
						});
					} else {
						// �����ɹ�
						if (protocol == null) {
							protocol = new TopicDetailProtocol();
						}
						// ������������
						String url = Constants
								.getContentUrl(originTopicInfo.contentUrl);
						list = protocol.loadFromServer(url, false);

						// ���½���
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (waitDialog != null) {
									waitDialog.dismiss();
								}
								waitDialog.dismiss();
								MyToast.toast("�����ɹ���");
								// ˢ�½���
								adapter.notifyDataSetChanged();
								// pLv.getRefreshableView().setSelection(list.size());
								etContent.setText("");
								cbSmiley.setChecked(false);
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					// ���½���
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (waitDialog != null) {
								waitDialog.dismiss();
							}
							waitDialog.dismiss();
							MyToast.toast("�ظ�ʧ�ܣ��������磡");
							// ˢ�½���
							cbSmiley.setChecked(false);
						}
					});
				}

			}

			/**
			 * �������ʱ�ύ�Ĳ���
			 * 
			 * @param content
			 * @param params
			 */
			private void handlePostParams(final String content,
					RequestParams params) {
				String title = "Re:" + originTopicInfo.title;
				// ������������
				String reusr = originTopicInfo.author;

				// file=M.1463541584.A ��ȡ���� reid ����
				Pattern p = Pattern.compile("file=M\\.(\\d+)\\.");
				Matcher matcher = p.matcher(originTopicInfo.contentUrl);
				String reid = null;
				if (matcher.find()) {
					reid = matcher.group(1);
				}

				// ���cookie �Զ���½��
				String cookie = BaseApplication.getCookie();
				if (cookie == null) {
					cookie = BaseApplication.autoLogin(TopicDetailActivity.this,true);
				}

				// ��ȡpid
				String pid = getPid(cookie,
						Constants.getReplyPageUrl(originTopicInfo.contentUrl));

				// ��Ӳ��� ����7��
				params.addBodyParameter("title", title);
				params.addBodyParameter("pid", pid);
				params.addBodyParameter("reid", reid);

				params.addBodyParameter("signature", 1 + "");
				params.addBodyParameter("autocr", "on");
				params.addBodyParameter("reusr", reusr);
				//�����ֶ�����
				
//				String content2=content+"\n-\n"+"sent from С�ٺ�\n";
				String content2=UiUtils.addNewLineMark(content)+SPutils.getTail();
				
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		});

	}
	
	public static String addNewLineMark(String  str){
		StringBuffer sb=new StringBuffer(str);
		
		for (int i =40; i <sb.length(); i+=41) {
			sb.insert(i, "\n");
		}
		
		return sb.toString();
	}

	/**
	 * ������Ͱ�ť ���ͻ���
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_reply_send:

			// У������
			String content = etContent.getText().toString();

			if (TextUtils.isEmpty(content)) {
				MyToast.toast("���ݲ���Ϊ��Ŷ!");
				return;
			}

			handleReplyTopic(content);

			break;
			
		case R.id.btn_share:
			//MyToast.toast("����ɹ�");
			showShare();
			break;

		default:
			break;
		}
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 
		 String url="http://bbs.nju.edu.cn/"+originTopicInfo.contentUrl;
		 
		 String content=list.get(0).content;
		 content=content.replaceAll("<br>", "\n");
		 content=content.replaceAll("\\s+<img.*?/>\\s+", "");
		 
		 String title=originTopicInfo.title;
		 
		// ����ʱNotification��ͼ�������  2.5.9�Ժ�İ汾�����ô˷���
//		 oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		 oks.setTitle(title);
		 // titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		 oks.setTitleUrl(url);
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 String conntent="�����ϴ�С�ٺ�"+originTopicInfo.board+"�濴��һƪ���ӣ�["+title+"]\n������鿴��"+url+"\n--�������ϴ�С�ٺϰ�׿�ͻ���";
//		 oks.setText("���⣺["+title+"]\n   "+content+" \n������鿴:"+url+"\n--������С�ٺϿͻ���");
		 oks.setText(conntent);
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 //oks.setImagePath("/sdcard/test.jpg");//ȷ��SDcard������ڴ���ͼƬ
		 // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		 oks.setUrl(url);
		 // comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
//		 oks.setComment("���ǲ��������ı�");
		 // site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
//		 oks.setSite(getString(R.string.app_name));
		 // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		 oks.setSiteUrl(url);

		// ��������GUI
		 oks.show(this);
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
//			author=author.replaceAll("\\)", "");
//			author=author.replaceFirst("\\(", "\n");
			// author=author.replaceFirst("\\(","\n(" );
			
			
			// �����
//			if (author != null && originTopicInfo.author != null
//					&& author.contains(originTopicInfo.author)) {
				if (author != null && louzhu!= null
						&& author.equals(louzhu)) {
				author = " ¥�� " + author;
				SpannableStringBuilder ssb = new SpannableStringBuilder(author);
				int start = 0;
				int end = start + " ¥�� ".length();
				
				ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new AbsoluteSizeSpan(UiUtils.dip2px(12)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.tvAuthor.setText(ssb);
			} else {
				String id = SPutils.getFromSP("id");
				if (!TextUtils.isEmpty(id) && author.contains(id)) {
					author = " �� " + author;
					SpannableStringBuilder ssb = new SpannableStringBuilder(
							author);
					int start = 0;
					int end = start + " �� ".length();

					ssb.setSpan(new BackgroundColorSpan(0xFF8a2be2), start,
							end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start,
							end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

					holder.tvAuthor.setText(ssb);
				} else {
					holder.tvAuthor.setText(author);
				}
			}
			//�����ӿɵ��
			holder.tvContent.setAutoLinkMask(Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);

			holder.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// ���ÿɹ���
			holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());// ���ó����ӿ��Դ���ҳ

			Spanned spanned = Html.fromHtml(info.content, new URLImageParser(
					holder.tvContent),
					new MyTagHandler(getApplicationContext()));
			
			holder.tvContent.setText(sp.strToSmiley(spanned));
			holder.tvContent.invalidate();

			holder.tvFloorth.setText("��" + info.floorth + "¥");
			holder.tvPubTime.setText(info.pubTime);

			// if (position % 2 == 0) {
			// convertView.setBackgroundColor(0xFFEBEBEB);
			// } else {
			// convertView.setBackgroundColor(0xAAD0D0E0);
			// }

			Drawable drawable;

			if (r.nextInt(2) % 2 != 0) {
				drawable = getResources().getDrawable(
						R.drawable.ic_gender_female);
			} else {
				drawable = getResources()
						.getDrawable(R.drawable.ic_gender_male);
			}

			// ���������ߵ�ͼ��
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

			return convertView;
		}

		class ViewHolder {
			public TextView tvContent;
			public TextView tvAuthor;
			public TextView tvPubTime;
			public TextView tvFloorth;
		}

	}

	// �������ļ����¼�
	OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
		@Override
		public void onFaceSelected(SpannableString spanEmojiStr) {
			if (null != spanEmojiStr) {
				//�ڹ�괦�������
				String oriText=etContent.getText().toString();//ԭʼ����
				int index=Math.max(etContent.getSelectionStart(),0);//��ȡ��괦λ�ã�û�й�꣬����-1
				
				StringBuffer sb=new StringBuffer(oriText);
				sb.insert(index, spanEmojiStr);
				String string = sb.toString().replaceAll("\n", "<br>");
				
				Spanned spanned = Html.fromHtml(string);
				CharSequence text = SmileyParser.getInstance(getApplicationContext()).strToSmiley(spanned);
				etContent.setText(text);
				
				etContent.setSelection(index+spanEmojiStr.length());
//				etContent.append(spanEmojiStr);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {// �޸Ļ����ɹ�����ת ˢ��
			ThreadManager.getInstance().createLongPool()
					.execute(new Runnable() {
						@Override
						public void run() {
							String url = Constants
									.getContentUrl(originTopicInfo.contentUrl);
							if (protocol == null) {
								protocol = new TopicDetailProtocol();
							}
							list = protocol.loadFromServer(url, false);

							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									adapter.notifyDataSetChanged();
								}
							});

						}
					});

		}
	}

	// ���������
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

}
