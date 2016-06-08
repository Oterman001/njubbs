package com.oterman.njubbs.fragment.secondary;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.activity.topic.TopicReplyActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.protocol.MyTopicHistProtocol;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;

/**
 * 我的发帖
 * 要求有缓存 一进来显示缓存 自动刷新
 */
public class MyPostTopicFragment extends BaseFragment implements
		OnRefreshListener {

	private List<TopicInfo> dataList;
	private ListView lv;
	private SwipeRefreshLayout srl;
	private MyPostTopicAdatper adapter;
	private MyTopicHistProtocol protocol;
	private TextView tvState;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		LogUtil.d("onActivityCreated  执行力。。。");
		showViewFromServer();
	}

	@Override
	public LoadingState loadDataFromServer() {
		if (protocol == null) {
			protocol = new MyTopicHistProtocol();
		}
		// 重缓存中获取 缓存无法获取 会自动联网获取
		dataList = protocol.loadFromCache(getContext(), true, true);

		return dataList == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public View createSuccessView() {
		View rootView = View.inflate(getContext(),
				R.layout.activity_my_topic, null);
		
		srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_refresh);
		
		tvState = (TextView) rootView.findViewById(R.id.tv_my_state);
		lv = (ListView) rootView.findViewById(R.id.lv_my_topics);

		lv.setDivider(new ColorDrawable(0x55888888));
		lv.setDividerHeight(1);

		lv.setScrollbarFadingEnabled(true);
		lv.setVerticalScrollBarEnabled(true);
		

		if(dataList==null||dataList.size()==0){
			showEmptyView();
		}else{//展示listview
			
			updateSuccess();
		}
		
//		srl.addView(lv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_blue_light);
		//下拉刷新 当下拉时 会出发该方法
		srl.setOnRefreshListener(this);
		
		//一进来就自动刷  判断是否刚刚刷新过
		if(!BaseApplication.myTopicUpdated){

			onRefresh();
			
			srl.post(new Runnable() {
				@Override
				public void run() {
					srl.setRefreshing(true);
				}
			});
		}
		
		return rootView;
	}

	private void showEmptyView() {
		tvState.setVisibility(View.VISIBLE);
		lv.setVisibility(View.INVISIBLE);
	}

	private void updateSuccess() {
		tvState.setVisibility(View.INVISIBLE);
		lv.setVisibility(View.VISIBLE);
		adapter=new MyPostTopicAdatper();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//判断点击的是否为回帖
				TopicInfo topicInfo = dataList.get(position);
				if(topicInfo.title.contains("Re")){//为回帖
					Intent intent=new Intent(getContext(),TopicReplyActivity.class);
					intent.putExtra("topicInfo", topicInfo);
					startActivity(intent);
				}else{
					Intent intent=new Intent(getContext(),TopicDetailActivity.class);
					intent.putExtra("topicInfo", topicInfo);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		//刷新数据
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				if(protocol==null){
					protocol= new MyTopicHistProtocol();
				}
				 
				final List<TopicInfo> tempList = protocol.loadFromServer(getContext(), true, true);
				
				if(tempList!=null){
					if(getActivity()!=null){
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dataList.clear();
								dataList.addAll(tempList);

								//数据从无到有
								if(dataList.size()>0){//有数据
									if(adapter==null){
										updateSuccess();
										adapter.notifyDataSetChanged();
									}else{
										adapter.notifyDataSetChanged();
									}
								}else{//没有数据
									showEmptyView();
								}
								
								
								srl.post(new Runnable() {
									@Override
									public void run() {
										srl.setRefreshing(false);
									}
								});
								
								srl.setRefreshing(false);
								MyToast.toast("刷新成功");
							}
						});
					}
				}else{
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							srl.post(new Runnable() {
								@Override
								public void run() {
									srl.setRefreshing(false);
								}
							});
							srl.setRefreshing(false);
							MyToast.toast("刷新失败！");
						}
					});
				}
			}
		});
	}

	class MyPostTopicAdatper extends BaseAdapter {
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
			View view=null;
			ViewHolder holder=null;
			if(convertView==null){
				view=View.inflate(getContext(), R.layout.list_item_topten, null);
				holder=new ViewHolder();
				holder.tvTitle=(TextView) view.findViewById(R.id.tv_top_item_title);
				holder.tvBoard=(TextView) view.findViewById(R.id.tv_top_item_board);
				holder.tvAuthor=(TextView) view.findViewById(R.id.tv_top_item_author);
				
				//不需要
				holder.tvRank=(TextView) view.findViewById(R.id.tv_top_item_rankth);
				holder.tvReplyCount=(TextView) view.findViewById(R.id.tv_top_item_replycount);
				
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
			}
			
			TopicInfo info = dataList.get(position);
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);
			
			holder.tvReplyCount.setVisibility(View.INVISIBLE);
			holder.tvRank.setVisibility(View.INVISIBLE);
			
			return view;
		}
		
		class ViewHolder{
			TextView tvTitle;
			TextView tvBoard;
			TextView tvAuthor;
			TextView tvReplyCount;
			TextView tvRank;
		}

	}

}
