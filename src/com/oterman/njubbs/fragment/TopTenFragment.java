package com.oterman.njubbs.fragment;


import java.util.List;
import java.util.Random;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.R.drawable;
import com.oterman.njubbs.bean.TopTenInfo;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class TopTenFragment extends BaseFragment {


	private List<TopTenInfo> dataList;
	private ListView lv;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//初始化第一页
		showViewFromServer();
	}
	
	@Override
	public View createSuccessView() {
		lv = new ListView(getContext());
		
		
		lv.setAdapter(new TopTenAdatper());
		return lv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		TopTenProtocol protocol=new TopTenProtocol();
		dataList = protocol.loadFromCache();
		
		return dataList==null?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	class TopTenAdatper extends BaseAdapter{

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
			
			TopTenInfo info = dataList.get(position);
			
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);
			holder.tvReplyCount.setText(info.replyCount+"");
			holder.tvRank.setText(info.rankth);
			Drawable drawable;
			
			Random r=new Random();
			if(r.nextInt(3)%3!=0){
				drawable=getResources().getDrawable(R.drawable.ic_gender_female);
			}else{
				drawable=getResources().getDrawable(R.drawable.ic_gender_male);
			}
			
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
