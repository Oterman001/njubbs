package com.oterman.njubbs.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.oterman.njubbs.activity.TopicDetailActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.HotBoardProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class BoardsFragment extends BaseFragment implements OnRefreshListener {

	private SwipeRefreshLayout srl;
	private ExpandableListView expLv;
	private BoardsAdapter adapter;
	private HotBoardProtocol protocol;
	private Map<String, List<BoardInfo>> dataMap;
	private List<BoardInfo> hotBoardsList;
	private ArrayList<BoardInfo> favBoardsList;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// 初始化第一页
		showViewFromServer();
	}

	@Override
	public View createSuccessView() {

		srl = new SwipeRefreshLayout(getContext());

		expLv = new ExpandableListView(getContext());
		expLv.setDivider(new ColorDrawable(0x55888888));
		expLv.setDividerHeight(1);
		adapter = new BoardsAdapter();

		expLv.setAdapter(adapter);

		expLv.setDivider(new ColorDrawable(0x55888888));
		expLv.setDividerHeight(1);

		
		expLv.expandGroup(0);
		expLv.expandGroup(1);
		expLv.setGroupIndicator(null);
		
		expLv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
//				TopicInfo info = adapter.getChild(groupPosition, childPosition);
//
//				Intent intent = new Intent(getContext(),
//						TopicDetailActivity.class);
//
//				intent.putExtra("topicInfo", info);
//				startActivity(intent);
				return true;
			}
		});

		srl.addView(expLv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		// 下拉刷新 当下拉时 会出发该方法
		srl.setOnRefreshListener(this);

		return srl;
	}

	@Override
	public LoadingState loadDataFromServer() {
		 
		if(protocol==null){
			protocol=new HotBoardProtocol();
		}
		
		hotBoardsList = protocol.loadFromCache(Constants.HOT_BOARD_ULR);
		//获取收藏的版面
		String strs = SPutils.getFromSP("favBoards");
		String[] boards = strs.split("#");
		
		favBoardsList = new ArrayList<>();
		for (int i = 0; i < boards.length; i++) {
			BoardInfo info=new BoardInfo(null, boards[i], null, null);
			favBoardsList.add(info);
		}

		return hotBoardsList == null ? LoadingState.LOAD_FAILED: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public void onRefresh() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				// 重新加载数据
				final boolean result = updateData();

				UiUtils.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (result) {
							adapter.notifyDataSetChanged();
							MyToast.toast("刷新成功!");
						} else {
							MyToast.toast("刷新失败，请检查网络!");
						}

						srl.setRefreshing(false);
					}
				});

			}
		});

	}

	/**
	 * 刷新数据
	 */
	public boolean updateData() {
		
		return true;
	}

	class BoardsAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return 2;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupPosition==0? (favBoardsList.size()==0?1:favBoardsList.size()):hotBoardsList.size();
		}

		@Override
		public String getGroup(int groupPosition) {
			return groupPosition==0?"收藏版块":"热门版块";
		}

		@Override
		public BoardInfo getChild(int groupPosition, int childPosition) {
			if(groupPosition==0){
				if(favBoardsList.size()==0){
					return null;
				}
				return favBoardsList.get(childPosition);
			}else{
				return hotBoardsList.get(childPosition);
			}
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
			
			View view = View.inflate(getContext(),R.layout.list_item_group_boards, null);
			
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_group_title);

			tvTitle.setText(getGroup(groupPosition));

			return view;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = null;
			GroupHolder holder = null;
			
			if(convertView==null){
				view=View.inflate(getContext(), R.layout.list_item_board, null);
				holder=new GroupHolder();
				holder.tvBoard=(TextView) view.findViewById(R.id.tv_board);
				holder.tvPeopleCount=(TextView) view.findViewById(R.id.tv_peopleCount);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(GroupHolder) view.getTag();
			}
			
			//处理数据
			BoardInfo boardInfo = getChild(groupPosition, childPosition);
			
			if(groupPosition==0){
				holder.tvPeopleCount.setVisibility(View.INVISIBLE);
				
				if(TextUtils.isEmpty(boardInfo.boardName)){
					holder.tvBoard.setText("当前未收藏版面");
				}else{
					holder.tvBoard.setText(boardInfo.boardName);
				}
				
			}else{
				holder.tvPeopleCount.setVisibility(View.VISIBLE);
				holder.tvBoard.setText(boardInfo.rankth+". "+boardInfo.boardName+"("+boardInfo.chineseName+")");
				holder.tvPeopleCount.setText(boardInfo.peopleCount);
			}
			

			return view;
		}

		class GroupHolder {
			TextView tvBoard;
			TextView tvPeopleCount;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
