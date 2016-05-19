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
		//显示返回箭头
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		initActionBar();
		
		//自定义actionbar
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
		tvTitle.setText(topicInfo.board+"(点击进入)");
		tvTitle.setTextSize(22);

		
		//给actionbar添加点击事件  点击后进入到对应的版面
		tvTitle.setClickable(true);
		tvTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), BoardDetailActivity.class);
				
				intent.putExtra("boardUrl", topicInfo.boardUrl);
				
				startActivity(intent);
				//结束掉
				finish();
			}
		});
		
	}
	
	
	// 隐藏软键盘
	public void hideInputManager(Context ct) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) ct)
					.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
		} catch (Exception e) {
			Log.e("", "hideInputManager Catch error,skip it!", e);
		}
	}
	
	//表情点击的监听事件
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
	
	//条目点击监听
	OnItemClickListener mItemClickListener=new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			LogUtil.d("点击了哦"+position);
			TopicDetailInfo detailInfo = list.get(position);
			
			MyToast.toast(detailInfo.toString());
		}
	};;
	
	//联网成功后创建视图		
	public View createSuccessView() {
		
		view = View.inflate(getApplicationContext(), R.layout.activity_topic_detail, null);
		pLv=(PullToRefreshListView) view.findViewById(R.id.pLv);

		
		//初始化底部回帖view
		initReplyViews();
		
		pLv.setMode(Mode.PULL_FROM_END);//上拉加载更多
		
		//添加头布局
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
				pLv.getLoadingLayoutProxy().setRefreshingLabel("正在加载...嘿咻嘿咻");
				pLv.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
				pLv.getLoadingLayoutProxy().setReleaseLabel("松手开始加载");
				
				onLoadingMore();
				
			}
		});
		//设置条目点击监听
		pLv.setOnItemClickListener(new  OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtil.d("点击了哦"+position);
				TopicDetailInfo detailInfo = list.get(position);
				
				MyToast.toast(detailInfo.toString());
			}
		});
		
		return view;
	}

	/*
	 * 初始化回帖布局
	 */
	private void initReplyViews() {
		
		etContent = (EditText) view.findViewById(R.id.et_reply_content);//内容
		cbSmiley = (CheckBox) view.findViewById(R.id.cb_reply_smiley);//笑脸
		ibSend = (ImageButton) view.findViewById(R.id.ib_reply_send);//发送
		
		addFaceToolView = view.findViewById(R.id.add_tool);//表情集合
		
		ibSend.setOnClickListener(this);
		//触摸输入框 隐藏表情键盘
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				addFaceToolView.setVisibility(View.GONE);
				cbSmiley.setChecked(false);
				return false;
			}
		});	
		//点击笑脸，切换表情键盘状态
		cbSmiley.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(TopicDetailActivity.this, addFaceToolView);
					//点击表情时，设置监听
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener);
				}
				if (!isChecked) {
//					isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
//					isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(TopicDetailActivity.this);//隐藏软键盘
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
		tvTitle.setText(topicInfo.title);
		
		if(topicInfo.replyCount==null){
			tvReplyeCount.setText("共" + list.size() + "条回复");
		}else{
			String str = topicInfo.replyCount;
			if(str.contains("/")){
				str=str.split("/")[0];
			}
			
			tvReplyeCount.setText("共" + str + "条回复");
		}
		return view;
	}

	/*
	 * 从服务器中加载数据
	 */
	public LoadingState loadDataFromServer() {
		String url = Constants.getContentUrl(topicInfo.contentUrl);
		
		protocol = new TopicDetailProtocol();
		list = protocol.loadFromServer(url,false);
		return list == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	/**
	 * 加载下一页数据
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
							MyToast.toast("加载成功！");
						}else{//没有更多
							MyToast.toast("欧哦，没有更多了");
						}
						//加载完成，通知回掉
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
			
			holder.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
			holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());// 设置超链接可以打开网页
			
			Spanned spanned = Html.fromHtml(info.content,new URLImageParser(holder.tvContent),new MyTagHandler(getApplicationContext()));
			holder.tvContent.setText(sp.strToSmiley(spanned));
			holder.tvContent.invalidate();
			
			holder.tvFloorth.setText("第" + info.floorth + "楼");
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
	 * 点击发送按钮 发送回帖
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_reply_send:
			
			//校验数据
			String content = etContent.getText().toString();
			
			if(TextUtils.isEmpty(content)){
				MyToast.toast("内容不能为空哦!");
				return ;
			}
			
			handleReplyTopic(content);
			
			break;

		default:
			break;
		}
	}

	//处理回帖
	private void handleReplyTopic(final String content) {
		
		dialog = new WaitDialog(this);
		dialog.setMessage("努力的回帖中。。。");
		dialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			
			@Override
			public void run() {

				try {
					
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					
					//服务器编码为gbk
					RequestParams params = new RequestParams("gbk");
					
					//处理参数
					handlePostParams(content, params);
					//post提交
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,Constants.getNewTopicUrl(topicInfo.board), params);
					
					String result = BaseApplication.StreamToStr(stream);
					
					LogUtil.d("回帖结果：" + result);
					
					if(result.contains("匆匆过客")){//发帖失败了。
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (dialog != null) {
										dialog.dismiss();
									}
									dialog.dismiss();
									MyToast.toast("回帖失败，请登录！");
									cbSmiley.setChecked(false);
									//跳转到登陆页面
									Intent intent=new Intent(TopicDetailActivity.this,LoginActivity.class);
									startActivity(intent);
								}
							});
					}else{
						//回帖成功
						if(protocol==null){
							protocol = new TopicDetailProtocol();
						}
						//重新请求数据
						String url = Constants.getContentUrl(topicInfo.contentUrl);
						list = protocol.loadFromServer(url,false);
						
						//更新界面
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								dialog.dismiss();
								MyToast.toast("回帖成功！");
								//刷新界面
								adapter.notifyDataSetChanged();
								etContent.setText("");
								cbSmiley.setChecked(false);
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
					//更新界面
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (dialog != null) {
								dialog.dismiss();
							}
							dialog.dismiss();
							MyToast.toast("回复失败，请检查网络！");
							//刷新界面
							cbSmiley.setChecked(false);
						}
					});
				}
				
			}

			/**
			 * 处理回帖时提交的参数
			 * @param content
			 * @param params
			 */
			private void handlePostParams(final String content,
					RequestParams params) {
				String title="Re:"+topicInfo.title;
				//处理中文问题
				String reusr=topicInfo.author;
				
				//file=M.1463541584.A  获取数字 reid 参数
				Pattern p=Pattern.compile("file=M\\.(\\d+)\\.");
				Matcher matcher = p.matcher(topicInfo.contentUrl);
				String reid=null;
				if(matcher.find()){
					reid=matcher.group(1);
				}
				
				//添加cookie 自动登陆；
				String cookie =BaseApplication.cookie;
				if(cookie==null){
					BaseApplication.autoLogin();
					cookie = BaseApplication.cookie;
				}
				
				//获取pid
				String pid=getPid(cookie,Constants.getReplyPageUrl(topicInfo.contentUrl));
				
				//添加参数 共有7个
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
					
					
					LogUtil.d("回帖页面："+result);
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

