package com.oterman.njubbs.activity;

import java.util.List;
import java.util.Random;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.protocol.MailProtocol;
import com.oterman.njubbs.protocol.MailProtocol;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MySwipeRefreshLayout;
import com.oterman.njubbs.view.WaitDialog;

/**
 * ��������
 * 
 */
public class MailBoxActicity extends BaseActivity implements OnClickListener {

	// TopicInfo topicInfo;
	private List<MailInfo> dataList;
	private ListView lv;
	private PullToRefreshListView plv;
	private View rootView;
	private MessageAdapter adapter;
	private MailProtocol protocol;
	private MySwipeRefreshLayout sr;
	private ImageButton ibNewMail;
	private MailInfo mailInfo;
	private TextView tvState;

	@Override
	protected CharSequence getBarTitle() {
		return "վ����";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 111) {
//			dataList.remove(mailInfo);
//			adapter.notifyDataSetChanged();
			updateData();

		}
	}

	public LoadingState loadDataFromServer() {
		if (protocol == null) {
			protocol = new MailProtocol();
		}
	
		dataList = protocol.loadFromServer(Constants.BBS_MAIL_URL, false);
	
		return dataList == null ? LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	@Override
	public View createSuccessView() {

		ibNewMail = (ImageButton) actionBarView
				.findViewById(R.id.btn_new_topic);

		ibNewMail.setVisibility(View.VISIBLE);

		ibNewMail.setOnClickListener(this);

		sr = new MySwipeRefreshLayout(getApplicationContext());

		rootView = View.inflate(getApplicationContext(),
				R.layout.activity_message_main, null);
		plv = (PullToRefreshListView) rootView.findViewById(R.id.pLv_message);
		
		lv = plv.getRefreshableView();
		
		View headerView=View.inflate(getApplicationContext(), R.layout.mailbox_header, null);
		tvState = (TextView) headerView.findViewById(R.id.tv_mailbox_state);
		
		String state = getMailStatStr();
				
		tvState.setText(Html.fromHtml(state));
		
		lv.addHeaderView(headerView);

		sr.setViewGroup(plv.getRefreshableView());

		adapter = new MessageAdapter();

		plv.setAdapter(adapter);
		plv.setMode(Mode.PULL_FROM_END);// ����ģʽΪ�ӵײ����ظ���

		// ������Ŀ֮��ķָ���
		lv = plv.getRefreshableView();
		lv.setDivider(new ColorDrawable(0x77888888));
		lv.setDividerHeight(1);

		// ���վ��
		plv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				mailInfo = dataList.get(position - 2);
				Intent intent = new Intent(getApplicationContext(),MailContentActicity.class);
				intent.putExtra("contentUrl", mailInfo.contentUrl);
				startActivityForResult(intent, 100);
				mailInfo.hasRead=true;
				adapter.notifyDataSetChanged();

			}
		});

		// �����������ظ���ˢ��
		plv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				plv.getLoadingLayoutProxy().setRefreshingLabel("���ڼ���...���ݺ���");
				plv.getLoadingLayoutProxy().setPullLabel("�������ظ���");
				plv.getLoadingLayoutProxy().setReleaseLabel("���ֿ�ʼ����");

				ThreadManager.getInstance().createLongPool()
						.execute(new Runnable() {

							private List<MailInfo> moreList;

							@Override
							public void run() {
								if (protocol == null) {
									protocol = new MailProtocol();
								}

								String moreUrl = dataList.get(dataList.size() - 1).loadMoreUrl;
								moreUrl = Constants.getMailMoreUrl(moreUrl);
								if (moreUrl != null) {
									moreList = protocol.loadFromServer(moreUrl,
											false);
								}
								// ������� ������ҳ��
								UiUtils.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (moreList != null
												&& moreList.size() != 0) {
											dataList.addAll(moreList);
											adapter.notifyDataSetChanged();
											MyToast.toast("���سɹ���");
										} else {// û�и���
											MyToast.toast("ŷŶ��û�и�����");
										}
										// ������ɣ�֪ͨ�ص�
										plv.onRefreshComplete();
									}
								});
							}

						});
			}
		});

		sr.addView(rootView);

		// ����ˢ��
		sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			public void onRefresh() {
				updateData();
			}
		});

		sr.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);

		return sr;
	}
	
	private void updateData() {
		ThreadManager.getInstance().createLongPool()
				.execute(new Runnable() {
					@Override
					public void run() {
						if (protocol == null) {
							protocol = new MailProtocol();
						}
						 List<MailInfo> list = protocol.loadFromServer(
								Constants.BBS_MAIL_URL, false);
						 
						 if(list!=null){
							 dataList=list;
								runOnUiThread(new Runnable() {
									public void run() {
										sr.setRefreshing(false);
										//MyToast.toast("ˢ�³ɹ�!");
										tvState.setText(Html.fromHtml(getMailStatStr()));
										
										adapter.notifyDataSetChanged();
									}
								});
						 }
					}
				});
	}

	private String getMailStatStr() {
		String state="����������<font color='purple'>"+MailInfo.totalCount+
					  "</font>  ��,  ����������<font color='purple'>"+MailInfo.totalSpace+"</font>  K<br>"+
					  "��ʹ������<font color='purple'>"+MailInfo.usedSpace+
					  "</font>  K,  ʣ��������<font color='purple'>"+MailInfo.getAvaiSpace()+"</font>  K";
		return state;
	}

	class MessageAdapter extends BaseAdapter {
		Random r = new Random();

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
			View view = null;
			ViewHolder holder = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_mail_box, null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) view
						.findViewById(R.id.tv_mail_title);
				holder.tvAuthor = (TextView) view
						.findViewById(R.id.tv_mail_author);
				holder.tvPubTime = (TextView) view
						.findViewById(R.id.tv_mail_posttime);

				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			MailInfo info = dataList.get(position);

			//����Ƿ��
			String title=info.title;
			if(!info.hasRead){//δ��
				title="  ��  "+title;
				SpannableStringBuilder ssb=new SpannableStringBuilder(title);
				int start=0;
				int end=start+"  ��  ".length();
				ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.tvTitle.setText(ssb);
				
			}else{//�Ѷ�
				holder.tvTitle.setText(info.title);
			}
			
			holder.tvAuthor.setText(info.author);
			holder.tvPubTime.setText(info.postTime);
			Drawable drawable;

			if (r.nextInt(2) % 2 != 0) {
				drawable = getResources().getDrawable(
						R.drawable.ic_gender_female);
			} else {
				drawable = getResources()
						.getDrawable(R.drawable.ic_gender_male);
			}

			// ���������ߵ�ͼ��
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());

			holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

			return view;
		}

		class ViewHolder {
			TextView tvTitle;
			TextView tvAuthor;
			TextView tvPubTime;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_new_topic:
			// ��ת����վ����ҳ��
			Intent intent = new Intent(getApplicationContext(),
					MailNewActivity.class);

			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
