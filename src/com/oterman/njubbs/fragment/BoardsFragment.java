package com.oterman.njubbs.fragment;

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
import com.oterman.njubbs.activity.board.BoardDetailActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.db.BoardDao;
import com.oterman.njubbs.protocol.FavBoardsProtocol;
import com.oterman.njubbs.protocol.HotBoardProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class BoardsFragment extends BaseFragment implements OnRefreshListener {

	private SwipeRefreshLayout srl;
	private ExpandableListView expLv;
	private BoardsAdapter adapter;
	private HotBoardProtocol hotBoardProtocol;
	private FavBoardsProtocol favBoardProtocol;
	
	private Map<String, List<BoardInfo>> dataMap;
	private List<BoardInfo> hotBoardsList;
	private List<BoardInfo> favBoardsList;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// ��ʼ����һҳ
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
				
				BoardInfo info = adapter.getChild(groupPosition, childPosition);
				
				if(info==null){
					return false;
				}
				
				Intent intent=new Intent(getContext(),BoardDetailActivity.class);
				String boardUrl=null;
				if(info.boardUrl==null){
					boardUrl="bbstdoc?board="+info.boardName;
				}else{
					boardUrl=info.boardUrl;
				}
				intent.putExtra("boardUrl", boardUrl);
				
				startActivity(intent);
				return true;
			}
		});

		srl.addView(expLv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		// ����ˢ�� ������ʱ ������÷���
		srl.setOnRefreshListener(this);
		
		return srl;
	}
	
	//��һ�ν���ˢ��  pagechangeʱ����
	public void firstRefresh(){
		//����
		onRefresh();
		
		srl.post(new Runnable() {
			@Override
			public void run() {
				srl.setRefreshing(true);
			}
		});
	}
	

	@Override
	public LoadingState loadDataFromServer() {
		 
		//��ȡ���Ű���
		if(hotBoardProtocol==null){
			hotBoardProtocol=new HotBoardProtocol();
		}
		hotBoardsList = hotBoardProtocol.loadFromCache(Constants.HOT_BOARD_ULR);
		
		//��ȡ�ղصİ���
		if(favBoardProtocol==null){
			favBoardProtocol=new FavBoardsProtocol();
		}
		String favUrl=Constants.BBSLEFT_URL;
		favBoardsList=favBoardProtocol.loadFromCache(favUrl,getContext());
		
		return hotBoardsList == null ? LoadingState.LOAD_FAILED: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public void onRefresh() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				// ���¼�������
				final boolean result = updateData();
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (result) {
							adapter.notifyDataSetChanged();
							MyToast.toast("ˢ�³ɹ�!");
						} else {
							MyToast.toast("ˢ��ʧ�ܣ���������!");
						}
						
						srl.post(new Runnable() {
							@Override
							public void run() {
							srl.setRefreshing(false);	
							}
						});
						srl.setRefreshing(false);
					}
				});

			}
		});

	}

	/**
	 * ˢ������
	 */
	public boolean updateData() {
		//�ӷ�����ȥ��������
		//��ȡ���Ű���
		if(hotBoardProtocol==null){
			hotBoardProtocol=new HotBoardProtocol();
		}
		List<BoardInfo> list1 = hotBoardProtocol.loadFromServer(Constants.HOT_BOARD_ULR, true);
		
		//��ȡ�ղصİ���
		if(favBoardProtocol==null){
			favBoardProtocol=new FavBoardsProtocol();
		}
		String favUrl=Constants.BBSLEFT_URL;
		List<BoardInfo> list2 = favBoardProtocol.loadFromServer(favUrl, true,getContext());
		
		if(list1!=null&&list1.size()!=0){
			hotBoardsList=list1;
			favBoardsList=list2;
			return true;
		}
		
		return false;
	}

	class BoardsAdapter extends BaseExpandableListAdapter {
		
		BoardDao boardDao=null;

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
			return groupPosition==0?"�ղذ��":"���Ű��";
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
			
			//��������
			BoardInfo boardInfo = getChild(groupPosition, childPosition);
			
			if(groupPosition==0){
				holder.tvPeopleCount.setVisibility(View.INVISIBLE);
				
				if(boardInfo==null||TextUtils.isEmpty(boardInfo.boardName)){
					holder.tvBoard.setText("��ǰδ�ղذ���");
				}else{
					if(boardDao==null){
						boardDao=new BoardDao();
					}
					BoardInfo info = boardDao.getInfoByName(boardInfo.boardName);
					if(info!=null){
						String str=info.boardName+"("+info.chineseName+")";
						holder.tvBoard.setText(str);
					}else{
						holder.tvBoard.setText(boardInfo.boardName);
						
					}
					
				}
				
			}else{
				holder.tvPeopleCount.setVisibility(View.VISIBLE);
//				holder.tvBoard.setText(boardInfo.rankth+". "+boardInfo.boardName+"("+boardInfo.chineseName+")");
				holder.tvBoard.setText(boardInfo.boardName+"("+boardInfo.chineseName+")");
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
