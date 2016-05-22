package com.oterman.njubbs.activity;

import java.util.List;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.BoardTopicProtocol;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MySwipeRefreshLayout;
import com.oterman.njubbs.view.WaitDialog;

/**
 * 版面详情
 * 
 */
public class BoardDetailActivity extends BaseActivity {

	// TopicInfo topicInfo;
	private List<TopicInfo> dataList;
	private ListView lv;
	private String boardUrl;
	private String board;
	private PullToRefreshListView plv;
	private View rootView;
	private BoardAdapter adapter;
	private BoardTopicProtocol protocol;
	private ActionBar actionBar;
	private MySwipeRefreshLayout sr;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==100){//修改回帖成功后跳转  刷新
			ThreadManager.getInstance().createLongPool().execute(new Runnable() {
				
				@Override
				public void run() {
					if(protocol==null){
						protocol = new BoardTopicProtocol();
					}
					dataList = protocol.loadFromServer(Constants
							.getBoardUrl(boardUrl),false);
					
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
	
	@Override
	public void initViews() {
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
        
		boardUrl = getIntent().getStringExtra("boardUrl");
		board = boardUrl.substring(boardUrl.indexOf("=")+1);

        //添加事件 新帖
        ImageButton btnNewTopic = (ImageButton) view.findViewById(R.id.btn_new_topic);
		btnNewTopic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), NewTopicActivity.class);
				intent.putExtra("board", board);
				intent.putExtra("boardUrl", boardUrl);
				startActivityForResult(intent, 100);
			}
		});
        
		btnNewTopic.setVisibility(View.VISIBLE);
		
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);

		tvTitle.setText(board + "(帖子列表)");
		tvTitle.setTextSize(22);

	}

	@Override
	public View createSuccessView() {
		sr=new MySwipeRefreshLayout(getApplicationContext());
		
		rootView = View.inflate(getApplicationContext(), R.layout.topic_plv, null);
		plv = (PullToRefreshListView) rootView.findViewById(R.id.pLv);
		
		sr.setViewGroup(plv.getRefreshableView());
		
		adapter = new BoardAdapter();
		
		plv.setAdapter(adapter);
		plv.setMode(Mode.PULL_FROM_END);//设置模式为从底部加载更多
		
		//设置条目之间的分割线
		lv=plv.getRefreshableView();
		lv.setDivider(new ColorDrawable(0x77888888));
		lv.setDividerHeight(1);
		
		plv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TopicInfo info = dataList.get(position-1);
				Intent intent = new Intent(getApplicationContext(),TopicDetailActivity.class);
				info.board =board;
				info.boardUrl = boardUrl;
				intent.putExtra("topicInfo", info);
				
				//标记为读过；
				String readedTopics = SPutils.getFromSP("readedTopics");
				String readUrl=info.contentUrl;
				if(TextUtils.isEmpty(readedTopics)){//没有记录
					SPutils.saveToSP("readedTopics",readUrl );
				}else{
					if(!readedTopics.contains(readUrl)){//没读过
						SPutils.saveToSP("readedTopics", readedTopics+"#"+readUrl);
					}
				}
				adapter.notifyDataSetChanged();
				
				startActivity(intent);
			}
		});
		
		//长按弹出对话框
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				LogUtil.d("长按了哦.."+position);
				
				TopicInfo topicInfo = dataList.get(position-1);
				
				AlertDialog.Builder  builder=new AlertDialog.Builder(BoardDetailActivity.this);
				
				View dialogView=View.inflate(getApplicationContext(), R.layout.item_long_click, null);
				
				
				builder.setTitle("请选择操作");
				builder.setView(dialogView);
				
				builder.setNegativeButton("取消", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				
				AlertDialog dialog = builder.create();
				initDialogView(dialogView,topicInfo,dialog);
				dialog.show();
				
				return true;
			}

			private void initDialogView(View dialogView, final TopicInfo topicInfo, final AlertDialog dialog) {
				
				TextView tvAuthurDetail=(TextView) dialogView.findViewById(R.id.tv_author_detail);
				TextView tvModifyTopic=(TextView) dialogView.findViewById(R.id.tv_modify_topic);
				TextView tvDeleteTopic=(TextView) dialogView.findViewById(R.id.tv_delete_topci);
				TextView tvMessage=(TextView) dialogView.findViewById(R.id.tv_message_to_author);
				
				tvAuthurDetail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MyToast.toast("作者详情"+topicInfo.authorUrl);
						
					}
				});
				
				tvModifyTopic.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MyToast.toast("修改帖子"+topicInfo.title);
						
					}
				});
				
				tvDeleteTopic.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						MyToast.toast("删除帖子"+topicInfo.contentUrl);
//						LogUtil.d("帖子链接："+topicInfo.contentUrl);
						
						//删除帖子逻辑 /bbsdel?board=Pictures&file=M.1463450113.A
						final String url = Constants.getTopicDelUrl(topicInfo.contentUrl);
						
						dialog.dismiss();
						LogUtil.d("删除帖子链接"+url);
						final WaitDialog waitDialog = new WaitDialog(BoardDetailActivity.this);
						
						waitDialog.setMessage("正在删除..");
						waitDialog.show();
						
						ThreadManager.getInstance().createShortPool().execute(new Runnable() {
							@Override
							public void run() {
								SystemClock.sleep(1000);
								
								HttpUtils httpUtils=new HttpUtils();
								try {
									RequestParams rp=new RequestParams();
									String cookie=BaseApplication.getCookie();
									
									if(cookie==null){
										cookie=BaseApplication.autoLogin();
										LogUtil.d("未登录，自动登陆。。："+cookie);
									}
									
									rp.addHeader("Cookie", cookie);
									
									ResponseStream stream = httpUtils.sendSync(HttpMethod.GET, url,rp);
									
									final String result = BaseApplication.StreamToStr(stream);
									
									LogUtil.d("删除结果："+result);
									if(result.contains("返回本讨论区")){
										runOnUiThread(new  Runnable() {
											public void run() {
												MyToast.toast("删除成功");
												
												dataList.remove(topicInfo);
												adapter.notifyDataSetChanged();
												waitDialog.dismiss();
												//dialog.dismiss();
											}
										});
									}else{
										runOnUiThread(new  Runnable() {
											public void run() {
												MyToast.toast("删除失败！无权删除该帖");
												waitDialog.dismiss();
											}
										});
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						
					}
				});
				tvMessage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						MyToast.toast("站内"+topicInfo.authorUrl);
						
					}
				});
				
				
			}
		});
		//设置上拉加载更多刷新
		plv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				plv.getLoadingLayoutProxy().setRefreshingLabel("正在加载...嘿咻嘿咻");
				plv.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
				plv.getLoadingLayoutProxy().setReleaseLabel("松手开始加载");
				
				ThreadManager.getInstance().createLongPool().execute(new Runnable() {
					private List<TopicInfo> moreList;

					@Override
					public void run() {
						if(protocol==null){
							protocol = new BoardTopicProtocol();
						}
						
						String loadMoreUrl = dataList.get(dataList.size()-1).loadMoreUrl;
						if(loadMoreUrl!=null){
							moreList = protocol.loadFromServer(Constants.getBoardUrl(loadMoreUrl), false);
						}
						//加载完后 更新主页面
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(moreList!=null&&moreList.size()!=0){
									dataList.addAll(moreList);
									adapter.notifyDataSetChanged();
									MyToast.toast("加载成功！");
								}else{//没有更多
									MyToast.toast("欧哦，没有更多了");
								}
								//加载完成，通知回掉
								plv.onRefreshComplete();
							}
						});
					}
				});
			}
		});
		
		sr.addView(rootView);
		
		sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				ThreadManager.getInstance().createLongPool().execute(new Runnable() {
					
					@Override
					public void run() {
						if(protocol==null){
							protocol = new BoardTopicProtocol();
						}
						dataList = protocol.loadFromServer(Constants.getBoardUrl(boardUrl),false);
						
						runOnUiThread(new Runnable() {
							public void run() {
								sr.setRefreshing(false);
								MyToast.toast("刷新成功!");
								
								adapter.notifyDataSetChanged();
								
							}
						});
						
					}
				});
				
			}
		});
		
		sr.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		return sr;
	}
	public LoadingState loadDataFromServer() {
		if(protocol==null){
			protocol = new BoardTopicProtocol();
		}
		dataList = protocol.loadFromServer(Constants
				.getBoardUrl(boardUrl),false);

		return dataList == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	class BoardAdapter extends BaseAdapter {
		Random r = new Random();

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_board_topic, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) view
						.findViewById(R.id.tv_board_topic_item_title);
				holder.tvAuthor = (TextView) view
						.findViewById(R.id.tv_board_topic_item_author);
				holder.tvPubTime = (TextView) view
						.findViewById(R.id.tv_board_topic_item_pubtime);
				holder.tvReplyCount = (TextView) view
						.findViewById(R.id.tv_board_topic_item_replycount);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			TopicInfo info = dataList.get(position);
			//检查是否浏览过
			String readedTopics = SPutils.getFromSP("readedTopics");
			if(!TextUtils.isEmpty(readedTopics)&&readedTopics.contains(info.contentUrl)){
				holder.tvTitle.setTextColor(0x70000000);
			}else{
				holder.tvTitle.setTextColor(0xff000000);
			}
			
			
			String title=info.title;
			if(info.shouldTop){//置顶  标记一下
				title=" 置顶 "+title;
				SpannableStringBuilder ssb=new SpannableStringBuilder(title);
				int start=0;
				int end=start+" 置顶 ".length();
				
//				ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new BackgroundColorSpan(0xff008000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				holder.tvTitle.setText(ssb);
				
			}else{//不是置顶
				holder.tvTitle.setText(title);
			}
			
			//检查是否为我发的帖子
			String author=info.author;
			String id = SPutils.getFromSP("id");
			if(!TextUtils.isEmpty(id)&&author.contains(id)){
				author=" 我 "+author;
				SpannableStringBuilder ssb=new SpannableStringBuilder(author);
				int start=0;
				int end=start+" 我 ".length();
				
				ssb.setSpan(new BackgroundColorSpan(0xFF8a2be2), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
				
				holder.tvAuthor.setText(ssb);
			}else{
				holder.tvAuthor.setText(author);
			}
			
			holder.tvReplyCount.setText(info.replyCount + "");
			holder.tvPubTime.setText(info.pubTime);
			Drawable drawable;

			if (r.nextInt(3) % 3 != 0) {
				drawable = getResources().getDrawable(
						R.drawable.ic_gender_female);
			} else {
				drawable = getResources()
						.getDrawable(R.drawable.ic_gender_male);
			}

			// 随机设置左边的图标
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

			return view;
		}

		class ViewHolder {
			TextView tvTitle;
			TextView tvAuthor;
			TextView tvReplyCount;
			TextView tvPubTime;
		}

	}

}
