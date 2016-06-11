package com.oterman.njubbs.fragment.secondary;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.protocol.TopAllProtocol;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class TopAllFragment extends BaseFragment implements OnRefreshListener {

	private SwipeRefreshLayout srl;
	private ExpandableListView expLv;
	private TopAllAdapter adapter;
	private TopAllProtocol protocol;
	private Map<String, List<TopicInfo>> dataMap;
	private Map<Integer, String> keyMap;
	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 初始化第一页
		showViewFromServer();
	}
	
	public SwipeRefreshLayout getSRL(){
		return srl;
	}
	
	@Override
	public View createSuccessView() {

		srl = new SwipeRefreshLayout(getContext());

		expLv = new ExpandableListView(getContext());
		expLv.setDivider(new ColorDrawable(0x55888888));
		expLv.setDividerHeight(1);
		adapter = new TopAllAdapter();

		expLv.setAdapter(adapter);

		expLv.setDivider(new ColorDrawable(0x55888888));
		expLv.setDividerHeight(1);

		expLv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				TopicInfo info = adapter.getChild(groupPosition, childPosition);

				Intent intent = new Intent(getContext(),TopicDetailActivity.class);
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
				return true;
			}
		});
		
		for(int i=0;i<keyMap.size();i++){
			expLv.expandGroup(i);
		}
		//去掉箭头
		expLv.setGroupIndicator(null);

		srl.addView(expLv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		// 下拉刷新 当下拉时 会出发该方法
		srl.setOnRefreshListener(this);
		
		return srl;
	}

	@Override
	public LoadingState loadDataFromServer() {
		if (protocol == null) {
			protocol = new TopAllProtocol();
		}

		dataMap = protocol.loadFromCache(Constants.TOP_ALL_URL);
//		dataMap = protocol.loadFromServer(Constants.TOP_ALL_URL,true);
		keyMap = protocol.getKeyMap();

		return dataMap == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public void onRefresh() {
		refresh(true);
	}

	public void refresh(final boolean showToast) {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				//重新加载数据
				final boolean result=updateData();
				
				UiUtils.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(result){
							adapter.notifyDataSetChanged();
							if(showToast){
								MyToast.toast("刷新成功!");
							}
						}else{
							if(showToast){
								MyToast.toast("刷新失败，请检查网络!");
							}
						}
						
						if(srl!=null){
							srl.post(new Runnable() {
								@Override
								public void run() {
									srl.setRefreshing(false);
								}
							});
							srl.setRefreshing(false);
						}
					}
				});
				
			}
		});
	}

	/**
	 * 刷新数据
	 */
	public boolean updateData(){
		if(protocol==null){
			protocol = new TopAllProtocol();
		}
		Map<String, List<TopicInfo>> map = protocol.loadFromServer(Constants.TOP_ALL_URL,true);
		if(map==null||map.keySet().size()==0){
			return false;
		}
		dataMap=map;
		return true;
	}

	class TopAllAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return dataMap.keySet().size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return dataMap.get(keyMap.get(groupPosition)).size();
		}

		@Override
		public String getGroup(int groupPosition) {

			return keyMap.get(groupPosition);
		}

		@Override
		public TopicInfo getChild(int groupPosition, int childPosition) {
			String key = keyMap.get(groupPosition);
			return dataMap.get(key).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = View.inflate(getContext(),
					R.layout.list_item_group_topall, null);

			int childCount = getChildrenCount(groupPosition);
			TextView tvTitle = (TextView) view
					.findViewById(R.id.tv_group_title);
			TextView tvCount = (TextView) view
					.findViewById(R.id.tv_group_count);

			tvCount.setText("(" + childCount+")");
			tvTitle.setText(getGroup(groupPosition));

			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = null;
			GroupHolder holder = null;
			if (convertView == null) {
				view = View.inflate(getContext(), R.layout.list_item_topall,
						null);
				holder = new GroupHolder();
				holder.tvAuthor = (TextView) view
						.findViewById(R.id.tv_top_item_author);
				holder.tvBoard = (TextView) view
						.findViewById(R.id.tv_top_item_board);
				holder.tvTitle = (TextView) view
						.findViewById(R.id.tv_top_item_title);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (GroupHolder) view.getTag();
			}

			TopicInfo info = getChild(groupPosition, childPosition);

			//标记是否已读
			String readedTopics = SPutils.getFromSP("readedTopics");
			if(!TextUtils.isEmpty(readedTopics)&&readedTopics.contains(info.contentUrl)){
				holder.tvTitle.setTextColor(0x70000000);
			}else{
				holder.tvTitle.setTextColor(0xff000000);
			}
			
			holder.tvAuthor.setText(info.author);
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);

			return view;
		}

		class GroupHolder {
			TextView tvTitle;
			TextView tvBoard;
			TextView tvAuthor;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
