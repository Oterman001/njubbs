package com.oterman.njubbs.fragment.secondary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
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
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public class FavoriteFragment extends BaseFragment {

	private List<String> dataList;
	private ListView lv;
	private SwipeRefreshLayout srl;
	FavBoardAdapter adapter;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//必须初始化，不然界面没更新
		showViewFromServer();
	}
	
	@Override
	public View createSuccessView() {
		//srl = new SwipeRefreshLayout(getContext());
		
		lv = new ListView(getContext());
		lv.setDivider(new ColorDrawable(0x55888888));  
		lv.setDividerHeight(1);
		adapter = new FavBoardAdapter();
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String boardName= dataList.get(position);
				
				Intent intent=new Intent(getContext(),BoardDetailActivity.class);
				
				String boardUrl="bbstdoc?board="+boardName;
				intent.putExtra("boardUrl", boardUrl);
				startActivity(intent);
			}
		});
		
		//srl.addView(lv);
		return lv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		String strs = SPutils.getFromSP("favBoards");
		String[] boards = strs.split("#");
		dataList=new ArrayList<>(Arrays.asList(boards));
		
		return dataList==null||dataList.size()==0?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	class FavBoardAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			if(dataList.size()==0)
				return 1;
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			if(dataList.size()==0) return null;
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv=new TextView(getContext());
			
			tv.setTextSize(40);
			if(dataList.size()==0){
				tv.setText("当前未收藏版面");
			}else{
				tv.setText(dataList.get(position));
			}
			
			return tv;
		}
	}
}
