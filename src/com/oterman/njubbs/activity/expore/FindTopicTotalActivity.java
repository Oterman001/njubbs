package com.oterman.njubbs.activity.expore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.activity.topic.TopicReplyActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.QueryTopicProtocol;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;

public class FindTopicTotalActivity extends MyActionBarActivity implements
		OnClickListener {

	private EditText etUser;
	private EditText etTitle;
	private EditText etTitle2;
	private EditText etTitle3;
	private EditText etDay;
	private EditText etDay2;
	private Button btnSearch;
	private QueryTopicProtocol protocol;
	private WaitDialog dialog;
	private Map<String, String> map;
	private ListView lvResult;

	private List<TopicInfo> topicList;
	private LinearLayout llContainer;
	private TopicAdapter adapter;

	@Override
	public void onBackPressed() {
		if (llContainer.getVisibility() == View.VISIBLE) {
			super.onBackPressed();
		}else{
			setBarTitle("搜索帖子");
			llContainer.setVisibility(View.VISIBLE);
			lvResult.setVisibility(View.INVISIBLE);
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_topic_total);

		etUser = (EditText) this.findViewById(R.id.et_search_user);
		etTitle = (EditText) this.findViewById(R.id.et_search_keyword1);
		etTitle2 = (EditText) this.findViewById(R.id.et_search_keyword2);
		etTitle3 = (EditText) this.findViewById(R.id.et_search_no_keyword);
		etDay = (EditText) this.findViewById(R.id.et_search_day1);
		etDay2 = (EditText) this.findViewById(R.id.et_search_day2);

		btnSearch = (Button) this.findViewById(R.id.btn_search);

		llContainer = (LinearLayout) this.findViewById(R.id.ll_container);
		lvResult = (ListView) this.findViewById(R.id.lv_result);

		btnSearch.setOnClickListener(this);

		dialog = new WaitDialog(this);
		map = new HashMap<>();
	}

	@Override
	protected String getBarTitle() {
		return "搜索帖子";
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_search:

			dialog.show();
			dialog.setMessage("正在努力查找。。");
			// 准备参数
			prepareParams();

			ThreadManager.getInstance().createLongPool()
					.execute(new Runnable() {
						@Override
						public void run() {
							if (protocol == null) {
								protocol = new QueryTopicProtocol();
							}
//							topicList = protocol.loadFromServer(map);
							
							List<TopicInfo> tempList = protocol.loadFromServer(map);
							
							if(tempList!=null&&tempList.size()>0){
								if(topicList==null){
									topicList=tempList;
								}else{
									topicList.clear();
									topicList.addAll(tempList);
								}
								
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										creatSuccessView();
									}
								});
								
							}else{
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dialog.dismiss();
										MyToast.toast("没有找到符合条件的帖子");
									}
								});
							}
							
						}
					});

			break;

		default:
			break;
		}
	}
	
	private void creatSuccessView() {
		setBarTitle("搜索结果");
		llContainer.setVisibility(View.INVISIBLE);
		lvResult.setVisibility(View.VISIBLE);
		
		if(adapter==null){
			adapter = new TopicAdapter();

			lvResult.setAdapter(adapter);
			
			lvResult.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
//					TopicInfo topicInfo = topicList.get(position);
//					//MyToast.toast("position:"+position+"	:"+topicInfo.toString());
//					Intent intent=new Intent(getApplicationContext(),TopicDetailActivity.class);
//					intent.putExtra("topicInfo", topicInfo);
//					startActivity(intent);
					
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
		}else{
			adapter.notifyDataSetChanged();
		}
		

		
		
		
	}

	// 准备参数
	private void prepareParams() {

		map.put("flag", "1");

		String user = etUser.getText().toString().trim();

		map.put("user", user);

		String title = etTitle.getText().toString().trim();

		map.put("title", title);

		String title2 = etTitle2.getText().toString().trim();

		map.put("title2", title2);

		String title3 = etTitle3.getText().toString().trim();
		map.put("title3", title3);

		String day = etDay.getText().toString().trim();
		map.put("day", day);

		String day2 = etDay2.getText().toString().trim();
		map.put("day2", day2);

	}

	private class TopicAdapter extends BaseAdapter {
		Random r = new Random();

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
			View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_topten, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) view
						.findViewById(R.id.tv_top_item_title);
				holder.tvBoard = (TextView) view
						.findViewById(R.id.tv_top_item_board);
				holder.tvAuthor = (TextView) view
						.findViewById(R.id.tv_top_item_author);

				// 不需要
				holder.tvRank = (TextView) view
						.findViewById(R.id.tv_top_item_rankth);
				holder.tvReplyCount = (TextView) view
						.findViewById(R.id.tv_top_item_replycount);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			TopicInfo info = topicList.get(position);
			holder.tvTitle.setText(info.title);
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);

			holder.tvReplyCount.setVisibility(View.INVISIBLE);
			holder.tvRank.setVisibility(View.INVISIBLE);

//			Drawable drawable;
//
//			if (r.nextInt(3) % 3 != 0) {
//				drawable = getResources().getDrawable(
//						R.drawable.ic_gender_female);
//			} else {
//				drawable = getResources()
//						.getDrawable(R.drawable.ic_gender_male);
//			}
//
//			// 随机设置左边的图标
//			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//					drawable.getMinimumHeight());
//			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

			return view;
		}

		class ViewHolder {
			TextView tvTitle;
			TextView tvBoard;
			TextView tvAuthor;
			TextView tvReplyCount;
			TextView tvRank;
		}

	}

}
