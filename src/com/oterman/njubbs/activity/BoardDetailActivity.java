package com.oterman.njubbs.activity;

import java.util.List;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.BoardTopicProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

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
        
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);
        
		boardUrl = getIntent().getStringExtra("boardUrl");
		board = boardUrl.substring(boardUrl.indexOf("=")+1);

		tvTitle.setText(board + "(帖子列表)");
		tvTitle.setTextSize(22);

		//getActionBar().setTitle(board + "(帖子列表)");
	}

	@Override
	public View createSuccessView() {
		rootView = View.inflate(getApplicationContext(), R.layout.topic_plv, null);
		plv = (PullToRefreshListView) rootView.findViewById(R.id.pLv);

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
				startActivity(intent);
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
		
		return rootView;
	}
	public View createSuccessView_old() {
		
		lv = new ListView(getApplicationContext());
		
		lv.setDivider(new ColorDrawable(0x77888888));
		lv.setDividerHeight(1);
		
		lv.setAdapter(new BoardAdapter());
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TopicInfo info = dataList.get(position);
				Intent intent = new Intent(getApplicationContext(),
						TopicDetailActivity.class);
				info.board =board;
				info.boardUrl = boardUrl;
				intent.putExtra("topicInfo", info);
				startActivity(intent);
			}
		});
		return lv;
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

			holder.tvTitle.setText(info.title);
			holder.tvAuthor.setText(info.author);
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
