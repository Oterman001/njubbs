package com.oterman.njubbs.fragment.secondary;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BoardDetailActivity;
import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.protocol.HotBoardProtocol;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class HotBoardsFragment extends BaseFragment {



	private List<BoardInfo> dataList;
	private ListView lv;
	private SwipeRefreshLayout srl;
	private HotBoardProtocol protocol;
	private BoardAdapter adapter;
	
	@Override
	public View createSuccessView() {
		srl = new SwipeRefreshLayout(getContext());
		
		lv = new ListView(getContext());
		lv.setDivider(new ColorDrawable(0x55888888));  
		lv.setDividerHeight(1);
		adapter = new BoardAdapter();
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BoardInfo info = dataList.get(position);
				
				Intent intent=new Intent(getContext(),BoardDetailActivity.class);
				
				intent.putExtra("boardUrl", info.boardUrl);
				startActivity(intent);
			}
		});
		
		srl.addView(lv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);
		
		srl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
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
									MyToast.toast("刷新成功!");
								}else{
									MyToast.toast("刷新失败，请检查网络!");
								}
								
								srl.setRefreshing(false);
							}
						});
						
					}


				});
			}
		});
		return srl;
	}
	private boolean updateData() {
		if(protocol==null){
			protocol = new HotBoardProtocol();
		}
		List<BoardInfo> list = protocol.loadFromServer(Constants.HOT_BOARD_ULR,true);
		if(list==null||list.size()==0){
			return false;
		}
		dataList=list;
		return true;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		protocol = new HotBoardProtocol();
		dataList = protocol.loadFromCache(Constants.HOT_BOARD_ULR);
		
		return dataList==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	class BoardAdapter extends BaseAdapter{
		
		Random r=new Random();
		
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
				view=View.inflate(getContext(), R.layout.list_item_board, null);
				holder=new ViewHolder();
				holder.tvBoard=(TextView) view.findViewById(R.id.tv_board);
				holder.tvPeopleCount=(TextView) view.findViewById(R.id.tv_peopleCount);
				view.setTag(holder);
			}else{
				view=convertView;
				holder=(ViewHolder) view.getTag();
			}
			
			BoardInfo info = dataList.get(position);
			
			holder.tvBoard.setText(info.rankth+". "+info.boardName+"("+info.chineseName+")");
			holder.tvPeopleCount.setText(info.peopleCount);
			return view;
		}
		
		class ViewHolder{
			TextView tvBoard;
			TextView tvPeopleCount;
		}
		
	}
}
