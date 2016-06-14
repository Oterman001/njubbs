package com.oterman.njubbs.fragment.secondary;


import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.activity.mail.MailNewActivity;
import com.oterman.njubbs.activity.topic.TopicDetailActivity;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.holders.OptionsDialogHolder;
import com.oterman.njubbs.holders.OptionsDialogHolder.MyOnclickListener;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.protocol.TopTenProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
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
		//��ʼ����һҳ
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
				//���Ϊ������
				String readedTopics = SPutils.getFromSP("readedTopics");
				String readUrl=info.contentUrl;
				if(TextUtils.isEmpty(readedTopics)){//û�м�¼
					SPutils.saveToSP("readedTopics",readUrl );
				}else{
					if(!readedTopics.contains(readUrl)){//û����
						SPutils.saveToSP("readedTopics", readedTopics+"#"+readUrl);
					}
				}
				adapter.notifyDataSetChanged();
				startActivity(intent);
				
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				LogUtil.d("������Ŷ.."+position);
				
				final TopicInfo topicInfo = dataList.get(position);
				
				AlertDialog.Builder  builder=new AlertDialog.Builder(getActivity());
				
				OptionsDialogHolder holder=new OptionsDialogHolder(getContext(), topicInfo.author,false);
				builder.setTitle("��ѡ�����");
				builder.setView(holder.getRootView());
				builder.setNegativeButton("ȡ��", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				
				final AlertDialog dialog = builder.create();
				//���ü���
				holder.setListener(new MyOnclickListener() {
					@Override
					public void onDelete() {//ɾ�����ӻص�
						handleDeleteTopic(topicInfo, dialog);
					}
					@Override
					public void OnQueryAuthurDetail() {
						handleShowUserDetail(topicInfo, dialog);
					}
					@Override
					public void OnModify() {
						MyToast.toast("�޸�����"+topicInfo.title);
					}
					@Override
					public void OnMailTo() {
						dialog.dismiss();
						Intent intent=new Intent(getContext(),MailNewActivity.class);
						
						if(topicInfo!=null){
							intent.putExtra("receiver",topicInfo.author);
						}
						startActivity(intent);
					}
					@Override
					public void onReplyFloor() {
						
					}
					@Override
					public void onQueryTopicHis() {//��ѯ���ߵķ�����¼
						dialog.dismiss();
						String author = topicInfo.author;
						Intent intent=new Intent(getContext(),MyTopicActivity.class);
						intent.putExtra("author", author);
						startActivity(intent);
					}
				});
				dialog.show();
				return true;
			}

		});
		
		srl.addView(lv);
		srl.setColorSchemeResources(android.R.color.holo_green_light,android.R.color.holo_blue_light);
		//����ˢ�� ������ʱ ������÷���
		srl.setOnRefreshListener(this);
		
		//һ�������Զ�ˢ��
		onRefresh();
		srl.post(new Runnable() {
			
			@Override
			public void run() {
				srl.setRefreshing(true);
			}
		});
		
		return srl;
	}
	
	
	
	protected void handleDeleteTopic(TopicInfo topicInfo, AlertDialog dialog) {
		
	}

	protected void handleShowUserDetail(TopicInfo topicInfo, AlertDialog dialog) {
		dialog.dismiss();
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		AlertDialog dialog2=null;
		UserDetailHolder holder=new UserDetailHolder(getContext());
		//�����û�����
		holder.updateStatus(topicInfo.author);
		
		builder.setView(holder.getRootView());
		dialog2=builder.show();
		holder.setOwnerDialog(dialog2);
	}

	/**
	 * ˢ������
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
				//���¼�������
				final boolean result=updateData();
				
				UiUtils.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(result){
							adapter.notifyDataSetChanged();
							MyToast.toast("ˢ�³ɹ�!");
						}else{
							MyToast.toast("ˢ��ʧ�ܣ���������!");
						}
						srl.post(new Runnable() {
							@Override
							public void run() {
								srl.setRefreshing(false);
							}
						});
						
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
				view=View.inflate(getContext(), R.layout.list_item_topten_new, null);
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
			//����Ƿ������
			String readedTopics = SPutils.getFromSP("readedTopics");
			if(!TextUtils.isEmpty(readedTopics)&&readedTopics.contains(info.contentUrl)){
				holder.tvTitle.setTextColor(0x70000000);
			}else{
				holder.tvTitle.setTextColor(0xff000000);
			}
			holder.tvTitle.setText(info.title);
			
			holder.tvBoard.setText(info.board);
			holder.tvAuthor.setText(info.author);
			holder.tvReplyCount.setText(info.replyCount);
			holder.tvRank.setText(info.rankth);
			Drawable drawable;
			
//			
//			if(r.nextInt(3)%3!=0){
//				drawable=getResources().getDrawable(R.drawable.ic_gender_female);
//			}else{
//				drawable=getResources().getDrawable(R.drawable.ic_gender_male);
//			}
//			
//			//���������ߵ�ͼ��
//			drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
//			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);
//			
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
