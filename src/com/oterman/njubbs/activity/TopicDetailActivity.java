package com.oterman.njubbs.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadMoreListView;
import com.oterman.njubbs.view.LoadMoreListView.OnLoadMoreListener;
import com.oterman.njubbs.view.LoadingView.LoadingState;

@SuppressLint("NewApi")
public class TopicDetailActivity extends BaseActivity  {

	private List<TopicDetailInfo> list;
	private TopicDetailAdapter adapter;

	private TopicInfo topicInfo;
	private TopicDetailProtocol protocol;
	private LoadMoreListView lv;
	private PullToRefreshListView pLv;
	private View view;

	@Override
	public void initViews() {
		topicInfo = (TopicInfo) getIntent().getSerializableExtra("topicInfo");
		getActionBar().setTitle(topicInfo.board+"(�������)");
		//��actionbar���ӵ���¼�  �������뵽��Ӧ�İ���
		final int abTitleId = getResources().getIdentifier("action_bar_title","id", "android");
		findViewById(abTitleId).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), BoardDetailActivity.class);
				
//				intent.putExtra("topicInfo", topicInfo);
				
				intent.putExtra("boardUrl", topicInfo.boardUrl);
				
				startActivity(intent);
				//������
				finish();
			}
		});
		
	}
	//�Զ����
	public View createSuccessView2() {
		
		lv = new LoadMoreListView(getApplicationContext());
		
		View headerView = initHeaderView();
		lv.addHeaderView(headerView);

		lv.setDivider(new ColorDrawable(Color.GRAY));
		lv.setDividerHeight(UiUtils.dip2px(1));
		lv.setDividerHeight(0);
		adapter = new TopicDetailAdapter();
		lv.setAdapter(adapter);
		
		//lv.setOnLoadMoreListener(this);

		return lv;
	}
	
	public View createSuccessView() {
		view = View.inflate(getApplicationContext(), R.layout.topic_plv, null);
		pLv=(PullToRefreshListView) view.findViewById(R.id.pLv);
		
		pLv.setMode(Mode.PULL_FROM_END);//�������ظ���
		
		//����ͷ����
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        View headerView = initHeaderView();
        headerView.setLayoutParams(layoutParams);
        
        ListView lv = pLv.getRefreshableView();
        lv.addHeaderView(headerView);
        
		lv.setDivider(new ColorDrawable(Color.GRAY));
		lv.setDividerHeight(UiUtils.dip2px(1));
		lv.setDividerHeight(0);
		
		adapter = new TopicDetailAdapter();
		pLv.setAdapter(adapter);
		pLv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				pLv.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���...���ݺ���");
				pLv.getLoadingLayoutProxy().setPullLabel("�������ظ���");
				pLv.getLoadingLayoutProxy().setReleaseLabel("���ֿ�ʼ����");
				
				onLoadingMore();
				
			}
		});
		
		return view;
	}

	// ��ʼ��ͷ����
	private View initHeaderView() {
		View view = View.inflate(getApplicationContext(),
				R.layout.topic_detail_header, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tv_topic_titile);
		TextView tvReplyeCount = (TextView) view
				.findViewById(R.id.tv_topic_replycount);
		tvTitle.setText(topicInfo.title);
		tvReplyeCount.setText("��" + topicInfo.replyCount + "���ظ�");

		return view;
	}

	/*
	 * �ӷ������м�������
	 */
	public LoadingState loadDataFromServer() {
		String url = Constants.getContentUrl(topicInfo.contentUrl);
		
		protocol = new TopicDetailProtocol();
		list = protocol.loadFromServer(url,false);
		return list == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	/**
	 * ������һҳ����
	 */
	public void onLoadingMore() {
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			private List<TopicDetailInfo> moreList;

			@Override
			public void run() {
				if(protocol==null){
					protocol = new TopicDetailProtocol();
				}
				
				String loadMoreUrl = list.get(list.size()-1).loadMoreUrl;
				if(loadMoreUrl!=null){
					moreList = protocol.loadFromServer(Constants.getContentUrl(loadMoreUrl), false);
				}
				
				UiUtils.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(moreList!=null&&moreList.size()!=0){
							moreList.remove(0);
							list.addAll(moreList);
							adapter.notifyDataSetChanged();
							MyToast.toast("���سɹ���");
						}else{//û�и���
							MyToast.toast("ŷŶ��û�и�����");
						}
						//������ɣ�֪ͨ�ص�
						pLv.onRefreshComplete();
					}
				});
				
			}
		});
		
	}

	class TopicDetailAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_item_topic_detial, null);

				holder.tvAuthor = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_author);
				holder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_content);
				holder.tvFloorth = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_floorth);
				holder.tvPubTime = (TextView) convertView
						.findViewById(R.id.tv_topic_detail_item_pubtime);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			TopicDetailInfo info = list.get(position);
			holder.tvAuthor.setText(info.author);
			holder.tvContent.setText(info.content);
			holder.tvFloorth.setText("��" + info.floorth + "¥");
			holder.tvPubTime.setText(info.pubTime);

			if (position % 2 == 0) {
				convertView.setBackgroundColor(0xFFEBEBEB);
			} else {
				convertView.setBackgroundColor(0xAAD0D0E0);
			}
			return convertView;
		}

		class ViewHolder {
			public TextView tvContent;
			public TextView tvAuthor;
			public TextView tvPubTime;
			public TextView tvFloorth;
		}

	}

}