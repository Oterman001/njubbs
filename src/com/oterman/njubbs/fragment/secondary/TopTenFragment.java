package com.oterman.njubbs.fragment.secondary;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.TopicDetailActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class TopTenFragment extends BaseFragment implements OnRefreshListener {


	private List<TopicInfo> dataList;
	private ListView lv;
	private SwipeRefreshLayout srl;
	private TopTenAdatper adapter;
	private TopTenProtocol protocol;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//初始化第一页
		showViewFromServer();
	}
	
	@Override
	public View createSuccessView() {
		
		srl = new SwipeRefreshLayout(getContext());
		
		
		lv = new ListView(getContext());
		lv.setDivider(new ColorDrawable(0x55888888));  
		lv.setDividerHeight(1);
		adapter = new TopTenAdatper();
		
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TopicInfo info = dataList.get(position);
				Intent intent=new Intent(getContext(),TopicDetailActivity.class);
				
				intent.putExtra("topicInfo", info);
				startActivity(intent);
				
			}
		});
		srl.addView(lv);
		
		srl.setColorSchemeResources(android.R.color.holo_green_light,
									android.R.color.holo_blue_light);
		//下拉刷新 当下拉时 会出发该方法
		srl.setOnRefreshListener(this);

		
		onRefresh();
		
		return srl;
	}
	
	
	/**
	 * 刷新数据
	 */
	public boolean updateData(){
		if(protocol==null){
			protocol = new TopTenProtocol();
		}
		List<TopicInfo> list = protocol.loadFromServer(Constants.TOP_TEN_URL,true);
		if(list==null||list.size()==0){
			return false;
		}
		dataList=list;
		return true;
	}
	
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

	@Override
	public LoadingState loadDataFromServer() {
		if(protocol==null){
			protocol = new TopTenProtocol();
		}
		dataList = protocol.loadFromCache(Constants.TOP_TEN_URL);
		
		return dataList==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	class TopTenAdatper extends BaseAdapter{
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
				view=View.inflate(getContext(), R.layout.list_item_topten, null);
				holder=new ViewHolder();
				holder.tvTitle=(TextView) view.findViewById(R.id.tv_top_item_title);
				holder.tvBoard=(TextView) view.findViewById(R.id.tv_top_item_board);
				holder.tvAuthor=(TextView) view.findViewById(R.id.tv_top_item_author);
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
			holder.tvReplyCount.setText(info.replyCount);
			holder.tvRank.setText(info.rankth);
			Drawable drawable;
			
			
			if(r.nextInt(3)%3!=0){
				drawable=getResources().getDrawable(R.drawable.ic_gender_female);
			}else{
				drawable=getResources().getDrawable(R.drawable.ic_gender_male);
			}
			
			//随机设置左边的图标
			drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);
			
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
