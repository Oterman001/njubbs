package com.oterman.njubbs.activity.expore;

import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.activity.topic.TopicReplyActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.QueryTopicProtocol;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;

@SuppressLint("NewApi")
public  class FindTopicActivity extends FragmentActivity {

	protected ActionBar actionBar;
	protected View actionBarView;
	private ListView lvTopics;
	private List<TopicInfo> topicList;
	private TopicAdapter adapter;
	private EditText etTitle;
	private ImageButton ibSearch;
	private TextView tvState;
	private WaitDialog waitDialog;

	private QueryTopicProtocol protocol;
	
	private int count=0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 更改状态栏的颜色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources()
					.getColor(R.color.green));
		}

		//处理actionBar
		initActionBar();
		
		setContentView(R.layout.activity_find_topic);
		tvState = (TextView) this.findViewById(R.id.tv_state);
		
		//initViews();

	}

	
	private void initSuccessViews() {
		tvState.setVisibility(View.INVISIBLE);
		if(lvTopics==null){
			lvTopics = (ListView) this.findViewById(R.id.lv_topics);
		}
		
		if(adapter==null){
			adapter = new TopicAdapter();
			lvTopics.setAdapter(adapter);
			lvTopics.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//判断点击的是否为回帖
					TopicInfo topicInfo = topicList.get(position);
					
					if(topicInfo.title.contains("Re")){//为回帖
//						MyToast.toast("回帖"+topicInfo.title+topicInfo.contentUrl);
						Intent intent=new Intent(getApplicationContext(),TopicReplyActivity.class);
						intent.putExtra("topicInfo", topicInfo);
						startActivity(intent);
					}else{
						Intent intent=new Intent(getApplicationContext(),TopicDetailActivity.class);
						intent.putExtra("topicInfo", topicInfo);
						startActivity(intent);
					}
					

				}
			});
		}

	}

	//联网获取查询结果
	private void queryFromServer() {
		//检查输入是否为空
		String str=etTitle.getText().toString().trim();
		if(TextUtils.isEmpty(str)){
			MyToast.toast("没有输入，让我查啥捏");
			return ;
		}
		if(waitDialog==null){
			waitDialog = new WaitDialog(this);
			waitDialog.setMessage("正在努力查询。。");
		}
		
		waitDialog.show();
		//联网
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {

			@Override
			public void run() {
				if(protocol==null){
					protocol = new QueryTopicProtocol();
				}
				
				String title=etTitle.getText().toString().trim();
				
				final List<TopicInfo> tempList = protocol.loadFromServer(title);
				
				if(topicList!=null){
					topicList.clear();
					if(tempList!=null){
						topicList.addAll(tempList);
					}
				}else{
					topicList=tempList;
				}
				
				if(topicList!=null){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							waitDialog.dismiss();
							if(adapter==null){
								initSuccessViews();
							}else{
								tvState.setVisibility(View.INVISIBLE);
								adapter.notifyDataSetChanged();
							}
							if(topicList.size()==0){
								tvState.setVisibility(View.VISIBLE);
								tvState.setText("没找到符合条件的帖子！");
							}
							
						}
					});
				}else{//结果为空
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							waitDialog.dismiss();
							tvState.setVisibility(View.VISIBLE);
							tvState.setText("没找到符合条件的帖子！");
						}
					});
				}
				
			}
		});
		
	}
	
	
	private void initActionBar() {
		// 自定义actionbar
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	
		actionBarView = View.inflate(getApplicationContext(),R.layout.actionbar_custom_search, null);
		
		View back = actionBarView.findViewById(R.id.btn_back2);
		etTitle = (EditText) actionBarView.findViewById(R.id.et_board);
		etTitle.setHint("查找帖子");
		etTitle.setImeOptions(EditorInfo.IME_ACTION_SEND);  //回车
		
		ibSearch = (ImageButton) actionBarView.findViewById(R.id.ib_search);
		etTitle.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId==EditorInfo.IME_ACTION_SEND||(event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) {
					LogUtil.d("onEditorAction 执行了");
					
					if(count%2==0){
						queryFromServer();
					}
					count++;
					
					return true;
				}
				return false;
			}
		});
		
		ibSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				queryFromServer();
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(actionBarView, params);
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
			//性别标识
//			Drawable drawable;
//			if(r.nextInt(3)%3!=0){
//				drawable=getResources().getDrawable(R.drawable.ic_gender_female);
//			}else{
//				drawable=getResources().getDrawable(R.drawable.ic_gender_male);
//			}
//			
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
