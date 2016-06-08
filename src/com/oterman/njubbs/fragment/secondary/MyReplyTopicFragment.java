package com.oterman.njubbs.fragment.secondary;

import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.activity.topic.TopicReplyActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.protocol.MyTopicHistProtocol;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;

/**
 * �ҵĻ���
 * Ҫ���л��� һ������ʾ���� �Զ�ˢ��
 */
public class MyReplyTopicFragment extends BaseFragment implements
		OnRefreshListener {

	private List<TopicInfo> dataList;
	private ListView lv;
	private SwipeRefreshLayout srl;
	private MyPostTopicAdatper adapter;
	private MyTopicHistProtocol protocol;
	private TextView tvState;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		LogUtil.d("onActivityCreated  ִ����������");
		//showViewFromServer(); 
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// ��ʼ����һҳ
	}

	@Override
	public LoadingState loadDataFromServer() {
		if (protocol == null) {
			protocol = new MyTopicHistProtocol();
		}
		// �ػ����л�ȡ �����޷���ȡ ���Զ�������ȡ
		dataList = protocol.loadFromCache(getContext(), true, false);

		return dataList == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public View createSuccessView() {
		View rootView = View.inflate(getContext(),
				R.layout.activity_my_topic, null);
		srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_refresh);
		tvState = (TextView) rootView.findViewById(R.id.tv_my_state);
		lv = (ListView) rootView.findViewById(R.id.lv_my_topics);

		lv.setDivider(new ColorDrawable(0x55888888));
		lv.setDividerHeight(1);

		lv.setScrollbarFadingEnabled(true);
		lv.setVerticalScrollBarEnabled(true);
		
		
		if(dataList==null||dataList.size()==0){
			tvState.setVisibility(View.VISIBLE);
			lv.setVisibility(View.INVISIBLE);
		}else{//չʾlistview
			tvState.setVisibility(View.INVISIBLE);
			adapter=new MyPostTopicAdatper();
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//�жϵ�����Ƿ�Ϊ����
					TopicInfo topicInfo = dataList.get(position);
					
					if(topicInfo.title.contains("Re")){//Ϊ����
						Intent intent=new Intent(getContext(),TopicReplyActivity.class);
						intent.putExtra("topicInfo", topicInfo);
						startActivity(intent);
					}else{
						Intent intent=new Intent(getContext(),TopicDetailActivity.class);
						intent.putExtra("topicInfo", topicInfo);
						startActivity(intent);
					}
				}
			});
		}
		
		srl.setOnRefreshListener(this);
		srl.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_blue_light);
		
		//�������ζ�ʱ��ˢ��
		if(!BaseApplication.myReplyUpdate){
			//һ�������Զ�ˢ
			onRefresh();
			srl.post(new Runnable() {
				@Override
				public void run() {
					srl.setRefreshing(true);
				}
			});
		}

		
		return rootView;
	}

	class MyPostTopicAdatper extends BaseAdapter {
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
				
				//����Ҫ
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
			
			holder.tvReplyCount.setVisibility(View.INVISIBLE);
			holder.tvRank.setVisibility(View.INVISIBLE);
			
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

	@Override
	public void onRefresh() {
		//ˢ������
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				if(protocol==null){
					protocol= new MyTopicHistProtocol();
				}
				 
				final List<TopicInfo> tempList = protocol.loadFromServer(getContext(), true, false);
				
				if(tempList!=null){
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dataList.clear();
							dataList.addAll(tempList);
							adapter.notifyDataSetChanged();
							srl.post(new Runnable() {
								@Override
								public void run() {
									srl.setRefreshing(false);
								}
							});
							srl.setRefreshing(false);
							MyToast.toast("ˢ�³ɹ�");
						}
					});
				}else{
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							srl.post(new Runnable() {
								@Override
								public void run() {
									srl.setRefreshing(false);
								}
							});
							srl.setRefreshing(false);
							MyToast.toast("ˢ��ʧ��");
						}
					});
				}
			}
		});
	}

}
