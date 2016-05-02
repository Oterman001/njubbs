package com.oterman.njubbs.fragment;


import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.TopicDetailActivity;
import com.oterman.njubbs.bean.TopTenInfo;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
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
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TopTenInfo info = dataList.get(position);
				Toast.makeText(getContext(), info.title+":"+info.contentUrl, 0).show();
				String contentUrl=Constants.getContentUrl(info.contentUrl);
				LogUtil.d("contentUrl:"+contentUrl);
				Intent intent=new Intent(getContext(),TopicDetailActivity.class);
				
				intent.putExtra("contentUrl", contentUrl);
				
				startActivity(intent);
				
			}
		});
		return lv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		TopTenProtocol protocol=new TopTenProtocol();
		dataList = protocol.loadFromCache();
		
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
			
			TopTenInfo info = dataList.get(position);
			
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);
			holder.tvReplyCount.setText(info.replyCount+"");
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
