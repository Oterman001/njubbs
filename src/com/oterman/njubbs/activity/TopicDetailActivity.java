package com.oterman.njubbs.activity;

import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.smiley.SelectFaceHelper;
import com.oterman.njubbs.smiley.SelectFaceHelper.OnFaceOprateListener;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;
import com.oterman.njubbs.view.WaitDialog;


@SuppressLint("NewApi")
public class TopicDetailActivity extends BaseActivity implements OnClickListener  {

	private List<TopicDetailInfo> list;
	private TopicDetailAdapter adapter;

	private TopicInfo topicInfo;
	private TopicDetailProtocol protocol;
	private PullToRefreshListView pLv;
	private View view;
	ActionBar actionBar;
	private EditText etContent;
	private CheckBox cbSmiley;
	private ImageButton ibSend;
	private View addFaceToolView;
	SelectFaceHelper mFaceHelper;
	
	
	@Override
	public void initViews() {
		//��ʾ���ؼ�ͷ
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		initActionBar();
		
		//�Զ���actionbar
		actionBar=getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		View view=View.inflate(getApplicationContext(), R.layout.actionbar_custom_backtitle, null);
		
		View back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);
		
        
		
		topicInfo = (TopicInfo) getIntent().getSerializableExtra("topicInfo");
		tvTitle.setText(topicInfo.board+"(�������)");
		tvTitle.setTextSize(22);

		
		//��actionbar��ӵ���¼�  �������뵽��Ӧ�İ���
		tvTitle.setClickable(true);
		tvTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), BoardDetailActivity.class);
				
				intent.putExtra("boardUrl", topicInfo.boardUrl);
				
				startActivity(intent);
				//������
				finish();
			}
		});
		
	}
	
	
	// ���������
	public void hideInputManager(Context ct) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) ct)
					.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
		} catch (Exception e) {
			Log.e("", "hideInputManager Catch error,skip it!", e);
		}
	}
	
	//�������ļ����¼�
	OnFaceOprateListener mOnFaceOprateListener = new OnFaceOprateListener() {
		@Override
		public void onFaceSelected(SpannableString spanEmojiStr) {
			if (null != spanEmojiStr) {
				etContent.append(spanEmojiStr);
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
	
	private WaitDialog dialog;
	
	//��Ŀ�������
	OnItemClickListener mItemClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.d("�����Ŷ"+position);
			TopicDetailInfo detailInfo = list.get(position);
			
			MyToast.toast(detailInfo.toString());
		}
	};;
	
	//�����ɹ��󴴽���ͼ		
	public View createSuccessView() {
		
		view = View.inflate(getApplicationContext(), R.layout.activity_topic_detail, null);
		pLv=(PullToRefreshListView) view.findViewById(R.id.pLv);

		
		//��ʼ���ײ�����view
		initReplyViews();
		
		pLv.setMode(Mode.PULL_FROM_END);//�������ظ���
		
		//���ͷ����
//        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//        View headerView = initHeaderView();
//        headerView.setLayoutParams(layoutParams);
//        
//        ListView lv = pLv.getRefreshableView();
//        lv.addHeaderView(headerView);
//        
//		lv.setDivider(new ColorDrawable(Color.GRAY));
//		lv.setDividerHeight(UiUtils.dip2px(1));
//		lv.setDividerHeight(0);
		
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
		//������Ŀ�������
		pLv.setOnItemClickListener(new  OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtil.d("�����Ŷ"+position);
				TopicDetailInfo detailInfo = list.get(position);
				
				MyToast.toast(detailInfo.toString());
			}
		});
		
		return view;
	}

	/*
	 * ��ʼ����������
	 */
	private void initReplyViews() {
		
		etContent = (EditText) view.findViewById(R.id.et_reply_content);//����
		cbSmiley = (CheckBox) view.findViewById(R.id.cb_reply_smiley);//Ц��
		ibSend = (ImageButton) view.findViewById(R.id.ib_reply_send);//����
		
		addFaceToolView = view.findViewById(R.id.add_tool);//���鼯��
		
		ibSend.setOnClickListener(this);
		//��������� ���ر������
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				addFaceToolView.setVisibility(View.GONE);
				cbSmiley.setChecked(false);
				return false;
			}
		});	
		//���Ц�����л��������״̬
		cbSmiley.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(TopicDetailActivity.this, addFaceToolView);
					//�������ʱ�����ü���
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
				}
				if (!isChecked) {
//					isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
//					isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(TopicDetailActivity.this);//���������
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
		tvTitle.setText(topicInfo.title);
		
		if(topicInfo.replyCount==null){
			tvReplyeCount.setText("��" + list.size() + "���ظ�");
		}else{
			String str = topicInfo.replyCount;
			if(str.contains("/")){
				str=str.split("/")[0];
			}
			
			tvReplyeCount.setText("��" + str + "���ظ�");
		}
		return view;
	}

	/*
	 * �ӷ������м�������
	 */
	public LoadingState loadDataFromServer() {
		String url = Constants.getContentUrl(topicInfo.contentUrl);
		
		protocol = new TopicDetailProtocol();
		list = protocol.loadFromServer(url,false);
		return list == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	/**
	 * ������һҳ����
	 */
	public void onLoadingMore() {
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			private List<TopicDetailInfo> moreList;

			@Override
			public void run() {
				if(protocol==null){
					protocol = new TopicDetailProtocol();
				}
				
				String loadMoreUrl = list.get(list.size()-1).loadMoreUrl;
				if(loadMoreUrl!=null){
					moreList = protocol.loadFromServer(Constants.getContentUrl(loadMoreUrl), false);
				}
				
				UiUtils.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(moreList!=null&&moreList.size()!=0){
							moreList.remove(0);
							list.addAll(moreList);
							adapter.notifyDataSetChanged();
							MyToast.toast("���سɹ���");
						}else{//û�и���
							MyToast.toast("ŷŶ��û�и�����");
						}
						//������ɣ�֪ͨ�ص�
						pLv.onRefreshComplete();
					}
				});
				
			}
		});
		
	}

	class TopicDetailAdapter extends BaseAdapter {
		SmileyParser sp=SmileyParser.getInstance(getApplicationContext());
		
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
				convertView = View.inflate(getApplicationContext(),
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
			//mmlover(mmlover)
			String author=info.author;
			//author=author.replaceFirst("\\(","\n(" );
			holder.tvAuthor.setText(author);
			
			BitmapUtils bu=new BitmapUtils(getApplicationContext());
			
			holder.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// ���ÿɹ���
			holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());// ���ó����ӿ��Դ���ҳ
			
			Spanned spanned = Html.fromHtml(info.content,new URLImageParser(holder.tvContent),new MyTagHandler(getApplicationContext()));
			holder.tvContent.setText(sp.strToSmiley(spanned));
			holder.tvContent.invalidate();
			
			holder.tvFloorth.setText("��" + info.floorth + "¥");
			holder.tvPubTime.setText(info.pubTime);

			if (position % 2 == 0) {
				convertView.setBackgroundColor(0xFFEBEBEB);
			} else {
				convertView.setBackgroundColor(0xAAD0D0E0);
			}
			return convertView;
		}

	  	class ViewHolder {
			public TextView tvContent;
			public TextView tvAuthor;
			public TextView tvPubTime;
			public TextView tvFloorth;
		}
		
	}

	/**
	 * ������Ͱ�ť ���ͻ���
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_reply_send:
			
			//У������
			String content = etContent.getText().toString();
			
			if(TextUtils.isEmpty(content)){
				MyToast.toast("���ݲ���Ϊ��Ŷ!");
				return ;
			}
			
			handleReplyTopic(content);
			
			break;

		default:
			break;
		}
	}

	//�������
	private void handleReplyTopic(final String content) {
		
		dialog = new WaitDialog(this);
		dialog.setMessage("Ŭ���Ļ����С�����");
		dialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			
			@Override
			public void run() {

				try {
					
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					
					//����������Ϊgbk
					RequestParams params = new RequestParams("gbk");
					
					//�������
					handlePostParams(content, params);
					//post�ύ
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,Constants.getNewTopicUrl(topicInfo.board), params);
					
					String result = BaseApplication.StreamToStr(stream);
					
					LogUtil.d("���������" + result);
					
					if(result.contains("�Ҵҹ���")){//����ʧ���ˡ�
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (dialog != null) {
										dialog.dismiss();
									}
									dialog.dismiss();
									MyToast.toast("����ʧ�ܣ����¼��");
									cbSmiley.setChecked(false);
									//��ת����½ҳ��
									Intent intent=new Intent(TopicDetailActivity.this,LoginActivity.class);
									startActivity(intent);
								}
							});
					}else{
						//�����ɹ�
						if(protocol==null){
							protocol = new TopicDetailProtocol();
						}
						//������������
						String url = Constants.getContentUrl(topicInfo.contentUrl);
						list = protocol.loadFromServer(url,false);
						
						//���½���
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								dialog.dismiss();
								MyToast.toast("�����ɹ���");
								//ˢ�½���
								adapter.notifyDataSetChanged();
								etContent.setText("");
								cbSmiley.setChecked(false);
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					//���½���
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (dialog != null) {
								dialog.dismiss();
							}
							dialog.dismiss();
							MyToast.toast("�ظ�ʧ�ܣ��������磡");
							//ˢ�½���
							cbSmiley.setChecked(false);
						}
					});
				}
				
			}

			/**
			 * �������ʱ�ύ�Ĳ���
			 * @param content
			 * @param params
			 */
			private void handlePostParams(final String content,
					RequestParams params) {
				String title="Re:"+topicInfo.title;
				//������������
				String reusr=topicInfo.author;
				
				//file=M.1463541584.A  ��ȡ���� reid ����
				Pattern p=Pattern.compile("file=M\\.(\\d+)\\.");
				Matcher matcher = p.matcher(topicInfo.contentUrl);
				String reid=null;
				if(matcher.find()){
					reid=matcher.group(1);
				}
				
				//���cookie �Զ���½��
				String cookie =BaseApplication.cookie;
				if(cookie==null){
					BaseApplication.autoLogin();
					cookie = BaseApplication.cookie;
				}
				
				//��ȡpid
				String pid=getPid(cookie,Constants.getReplyPageUrl(topicInfo.contentUrl));
				
				//��Ӳ��� ����7��
				params.addBodyParameter("title", title);
				params.addBodyParameter("pid", pid);
				params.addBodyParameter("reid", reid);
				
				params.addBodyParameter("signature", 1 + "");
				params.addBodyParameter("autocr", "on");
				params.addBodyParameter("reusr", reusr);
				params.addBodyParameter("text", content);
				
				params.addHeader("Cookie", cookie);
			}

			private String  getPid(String cookie,String url) {
				
				RequestParams rp=new RequestParams();
				rp.setHeader("Cookie",cookie);
				
				try {
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST, url, rp);
					
					String result = BaseApplication.StreamToStr(stream);
					Document doc = Jsoup.parse(result);
					
					String  pid= doc.select("input[name=pid]").get(0).attr("value");
					
					
					LogUtil.d("����ҳ�棺"+result);
					LogUtil.d("pid:"+pid);
					return pid;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
		});

	}
	

}

