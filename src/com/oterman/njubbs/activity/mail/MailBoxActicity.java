package com.oterman.njubbs.activity.mail;

import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.holders.OptionsDialogHolder;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.holders.OptionsDialogHolder.MyOnclickListener;
import com.oterman.njubbs.protocol.MailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MySwipeRefreshLayout;
import com.umeng.analytics.MobclickAgent;

/**
 * 版面详情
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected CharSequence getBarTitle() {
		return "站内信";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 111) {
			// dataList.remove(mailInfo);
			// adapter.notifyDataSetChanged();
			updateData();

		}
	}

	public LoadingState loadDataFromServer() {
		if (protocol == null) {
			protocol = new MailProtocol();
		}

		dataList = protocol.loadFromServer(Constants.BBS_MAIL_URL, false,
				MailBoxActicity.this);

		return dataList == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
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

		View headerView = View.inflate(getApplicationContext(),
				R.layout.mailbox_header, null);
		tvState = (TextView) headerView.findViewById(R.id.tv_mailbox_state);

		String state = getMailStatStr();

		tvState.setText(Html.fromHtml(state));

		lv.addHeaderView(headerView);

		sr.setViewGroup(plv.getRefreshableView());

		adapter = new MessageAdapter();

		plv.setAdapter(adapter);
		plv.setMode(Mode.PULL_FROM_END);// 设置模式为从底部加载更多

		// 设置条目之间的分割线
		lv = plv.getRefreshableView();
		lv.setDivider(new ColorDrawable(0x77888888));
		lv.setDividerHeight(1);

		// 点击站内
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 1) {
					mailInfo = dataList.get(position - 2);
					Intent intent = new Intent(getApplicationContext(),
							MailContentActicity.class);
					intent.putExtra("contentUrl", mailInfo.contentUrl);
					startActivityForResult(intent, 100);
					mailInfo.hasRead = true;
					adapter.notifyDataSetChanged();
				}
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > 1) {
					mailInfo = dataList.get(position - 2);
					// 弹出对话框
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MailBoxActicity.this);
					OptionsDialogHolder holder = new OptionsDialogHolder(
							MailBoxActicity.this, mailInfo.author, false, true);
					builder.setTitle("请选择操作");
					builder.setView(holder.getRootView());
					builder.setNegativeButton("取消",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
					
					final AlertDialog dialog = builder.create();
					holder.setListener(new MyOnclickListener() {
						@Override
						public void onReplyFloor() {
						}

						@Override
						public void onQueryTopicHis() {
//							MyToast.toast("查询" + mailInfo.author + "的发帖记录");
							dialog.dismiss();
							String author = mailInfo.author;
							Intent intent=new Intent(MailBoxActicity.this,MyTopicActivity.class);
							intent.putExtra("author", author);
							startActivity(intent);
						}

						@Override
						public void onDelete() {
							MyToast.toast("删除站内");
							handleDeleteMail(mailInfo);
						}

						@Override
						public void OnQueryAuthurDetail() {
//							MyToast.toast("查询" + mailInfo.author + "详细信息");
							dialog.dismiss();
							AlertDialog.Builder builder=new AlertDialog.Builder(MailBoxActicity.this);
							AlertDialog dialog2=null;
							UserDetailHolder holder=new UserDetailHolder(MailBoxActicity.this);
							//更新用户详情
							holder.updateStatus(mailInfo.author);
							
							builder.setView(holder.getRootView());
							dialog2=builder.show();
							holder.setOwnerDialog(dialog2);
						}

						@Override
						public void OnModify() {

						}

						@Override
						public void OnMailTo() {

						}
					});
					
					dialog.show();

				}

				return true;
			}

		});

		// 设置上拉加载更多刷新
		plv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				plv.getLoadingLayoutProxy().setRefreshingLabel("正在加载...嘿咻嘿咻");
				plv.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
				plv.getLoadingLayoutProxy().setReleaseLabel("松手开始加载");

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
											false, MailBoxActicity.this);
								}
								// 加载完后 更新主页面
								UiUtils.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (moreList != null
												&& moreList.size() != 0) {
											dataList.addAll(moreList);
											adapter.notifyDataSetChanged();
											MyToast.toast("加载成功！");
										} else {// 没有更多
											MyToast.toast("欧哦，没有更多了");
										}
										// 加载完成，通知回掉
										plv.onRefreshComplete();
									}
								});
							}

						});
			}
		});

		sr.addView(rootView);

		// 下拉刷新
		sr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			public void onRefresh() {
				updateData();
			}
		});

		sr.setColorSchemeResources(android.R.color.holo_green_light,
				android.R.color.holo_blue_light);

		return sr;
	}

	private void handleDeleteMail(MailInfo info) {
		
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		
		builder.setTitle("亲！");
		builder.setMessage("确定要删除吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				//MyToast.toast("删除："+mailInfo.delUrl);
				final WaitDialog waitDialog=new WaitDialog(MailBoxActicity.this);
				
				waitDialog.setMessage("正在删除...");
				waitDialog.show();
				
				ThreadManager.getInstance().createLongPool().execute(new Runnable() {
					HttpUtils httpUtils=null;
					@Override
					public void run() {
						try {
							if(httpUtils==null){
								httpUtils=new HttpUtils();
							}
							RequestParams rp=new RequestParams();
							String cookie=BaseApplication.getCookie();
							
							if(cookie==null){
								cookie=BaseApplication.autoLogin(MailBoxActicity.this,true);
							}
							rp.addHeader("Cookie", cookie);
							String url=Constants.getMailDelUrl(mailInfo.delUrl);
							
							ResponseStream stream = httpUtils.sendSync(HttpMethod.GET, url,rp);
							
							String result = BaseApplication.StreamToStr(stream);
							LogUtil.d("删除站内结果："+result);
							
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									MyToast.toast("删除成功！");
									waitDialog.dismiss();
									
									setResult(111);
									finish();
								}
							});
							
						} catch (final Exception e) {
							e.printStackTrace();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									MyToast.toast("删除失败！"+e.getMessage());
									waitDialog.dismiss();
								}
							});
						}
						
					}
				});
			}
		});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	
	private void updateData() {
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				if (protocol == null) {
					protocol = new MailProtocol();
				}
				List<MailInfo> list = protocol.loadFromServer(
						Constants.BBS_MAIL_URL, false, MailBoxActicity.this);

				if (list != null) {
					dataList = list;
					runOnUiThread(new Runnable() {
						public void run() {
							sr.setRefreshing(false);
							// MyToast.toast("刷新成功!");
							tvState.setText(Html.fromHtml(getMailStatStr()));

							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		});
	}

	private String getMailStatStr() {
		String state = "信箱总数：<font color='purple'>" + MailInfo.totalCount
				+ "</font>  封,  信箱容量：<font color='purple'>"
				+ MailInfo.totalSpace + "</font>  K<br>"
				+ "已使用量：<font color='purple'>" + MailInfo.usedSpace
				+ "</font>  K,  剩余容量：<font color='purple'>"
				+ MailInfo.getAvaiSpace() + "</font>  K";
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

			// 标记是否读
			String title = info.title;
			if (!info.hasRead) {// 未读
				title = "  新  " + title;
				SpannableStringBuilder ssb = new SpannableStringBuilder(title);
				int start = 0;
				int end = start + "  新  ".length();
				ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.tvTitle.setText(ssb);

			} else {// 已读
				holder.tvTitle.setText(info.title);
			}

			holder.tvAuthor.setText(info.author);
			holder.tvPubTime.setText(info.postTime);
			Drawable drawable;

			// if (r.nextInt(2) % 2 != 0) {
			// drawable = getResources().getDrawable(
			// R.drawable.ic_gender_female);
			// } else {
			// drawable = getResources()
			// .getDrawable(R.drawable.ic_gender_male);
			// }
			//
			// // 随机设置左边的图标
			// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
			// drawable.getMinimumHeight());
			//
			// holder.tvAuthor.setCompoundDrawables(drawable, null, null, null);

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
			// 跳转到发站内信页面
			Intent intent = new Intent(getApplicationContext(),
					MailNewActivity.class);

			startActivity(intent);
			break;

		default:
			break;
		}

	}

}
