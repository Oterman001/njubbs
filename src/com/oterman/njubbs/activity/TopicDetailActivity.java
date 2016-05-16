package com.oterman.njubbs.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.BitmapUtils;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadMoreListView;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;


@SuppressLint("NewApi")
public class TopicDetailActivity extends BaseActivity  {

	private List<TopicDetailInfo> list;
	private TopicDetailAdapter adapter;

	private TopicInfo topicInfo;
	private TopicDetailProtocol protocol;
	private LoadMoreListView lv;
	private PullToRefreshListView pLv;
	private View view;
	ActionBar actionBar;

	
	protected void initActionBar() {
		actionBar=getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		View view=View.inflate(getApplicationContext(), R.layout.actionbar_custom_backtitle, null);
		
		View back = view.findViewById(R.id.btn_back);
        if (back == null) {
            throw new IllegalArgumentException(
                    "can not find R.id.btn_back in customView");
        }
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        
        ImageButton btnNewTopic = (ImageButton) view.findViewById(R.id.btn_new_topic);
		btnNewTopic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyToast.toast("����");
			}
		});
		
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);
    }
	
	@Override
	public void initViews() {
		//��ʾ���ؼ�ͷ
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		initActionBar();
		
		//�Զ���actionbar
		actionBar=getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		View view=View.inflate(getApplicationContext(), R.layout.actionbar_custom_backtitle, null);
		
		View back = view.findViewById(R.id.btn_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        
        TextView tvTitle=(TextView) view.findViewById(R.id.tv_actionbar_title);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, params);
		
        
		
		topicInfo = (TopicInfo) getIntent().getSerializableExtra("topicInfo");
		tvTitle.setText(topicInfo.board+"(�������)");
		tvTitle.setTextSize(22);
		

		
		//��actionbar��ӵ���¼�  �������뵽��Ӧ�İ���
		tvTitle.setClickable(true);
		tvTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), BoardDetailActivity.class);
				
				intent.putExtra("boardUrl", topicInfo.boardUrl);
				
				startActivity(intent);
				//������
				finish();
			}
		});
		
	}
	public View createSuccessView() {
		view = View.inflate(getApplicationContext(), R.layout.topic_plv, null);
		pLv=(PullToRefreshListView) view.findViewById(R.id.pLv);
		
		pLv.setMode(Mode.PULL_FROM_END);//�������ظ���
		
		//���ͷ����
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
		
		if(topicInfo.replyCount==null){
			tvReplyeCount.setText("��" + list.size() + "���ظ�");
		}else{
			String str = topicInfo.replyCount;
			if(str.contains("/")){
				str=str.split("/")[0];
			}
			
			tvReplyeCount.setText("��" + str + "���ظ�");
		}
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
		SmileyParser sp=SmileyParser.getInstance(getApplicationContext());
		
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
			//mmlover(mmlover)
			String author=info.author;
			//author=author.replaceFirst("\\(","\n(" );
			holder.tvAuthor.setText(author);
			
			BitmapUtils bu=new BitmapUtils(getApplicationContext());
			
			holder.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// ���ÿɹ���
			holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());// ���ó����ӿ��Դ���ҳ
			
			Spanned spanned = Html.fromHtml(info.content,new URLImageParser(holder.tvContent),new MyTagHandler(getApplicationContext()));
			holder.tvContent.setText(sp.strToSmiley(spanned));
			holder.tvContent.invalidate();
			
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

