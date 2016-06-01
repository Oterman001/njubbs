package com.oterman.njubbs.activity.expore;

import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.DownloadManager.Query;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.R.color;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.QueryTopicProtocol;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;

@SuppressLint("NewApi")
public  class MyTopicActivity extends BaseActivity {

	private List<TopicInfo> topicList;
	private TopicAdapter adapter;
	private ListView lv;
	private TextView tvState;
	private String author;
	private Intent intent;

	@Override
	protected CharSequence getBarTitle() {
		return "发帖记录";
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		intent=getIntent();
		author = intent.getStringExtra("author");
	}
	
	//加载数据
	@Override
	public LoadingState loadDataFromServer() {
		intent = getIntent();
		author = intent.getStringExtra("author");
		
		if(author==null||TextUtils.isEmpty(author)){
			author=SPutils.getFromSP("id");
			if(author==null||TextUtils.isEmpty(author)){
				BaseApplication.autoLogin(MyTopicActivity.this, true);
				finish();
			}
		}
		
		QueryTopicProtocol protocol=new QueryTopicProtocol();
		
		topicList = protocol.queryByAuthor(author);
		
		return LoadingState.LOAD_SUCCESS;
	}


	@Override
	public View createSuccessView() {
		View rootView=View.inflate(getApplicationContext(), R.layout.activity_my_topic, null);
		
		tvState = (TextView) rootView.findViewById(R.id.tv_my_state);
		lv = (ListView) rootView.findViewById(R.id.lv_my_topics);
		
		lv.setDivider(new ColorDrawable(0x55888888));  
		lv.setDividerHeight(1);
		
		lv.setScrollbarFadingEnabled(true);
		lv.setVerticalScrollBarEnabled(true);
		if(topicList==null||topicList.size()==0){
			tvState.setVisibility(View.VISIBLE);
			lv.setVisibility(View.INVISIBLE);
		}else{//展示listview
			tvState.setVisibility(View.INVISIBLE);
			adapter=new TopicAdapter();
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TopicInfo topicInfo = topicList.get(position);
					//MyToast.toast("position:"+position+"	:"+topicInfo.toString());
					Intent intent=new Intent(getApplicationContext(),TopicDetailActivity.class);
					intent.putExtra("topicInfo", topicInfo);
					startActivity(intent);
				}
			});
		}
		
		return rootView;
	}


	private class TopicAdapter extends BaseAdapter{
		Random r=new Random();
		@Override
		public int getCount() {
			return topicList.size();
		}
	
		@Override
		public Object getItem(int position) {
			return topicList.get(position);
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
				view=View.inflate(getApplicationContext(), R.layout.list_item_topten, null);
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
			
			TopicInfo info = topicList.get(position);
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);
			
			holder.tvReplyCount.setVisibility(View.INVISIBLE);
			holder.tvRank.setVisibility(View.INVISIBLE);
			
//			Drawable drawable;
//			if(r.nextInt(3)%3!=0){
//				drawable=getResources().getDrawable(R.drawable.ic_gender_female);
//			}else{
//				drawable=getResources().getDrawable(R.drawable.ic_gender_male);
//			}
//			//随机设置左边的图标
//			drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
//			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);
			
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
