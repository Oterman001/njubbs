package com.oterman.njubbs.holders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.mail.MailNewActivity;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.dialog.AddFriendDialog;
import com.oterman.njubbs.protocol.UserProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

public class UserDetailHolder implements OnClickListener {

	private TextView tvId;
	private TextView tvNickname;
	private TextView tvXingzuo;
	private TextView tvJingyan;
	private TextView tvBiaoxian;
	private TextView tvLife;
	private TextView tvTotalVisit;
	private TextView tvTotalPost;
	private TextView tvLastVisitTime;
	private TextView tvLastVisitIp;
	private TextView tvOnline;

	View rootView;
	private Button btnSendMail;
	private UserInfo info;
	Context context;
	private Button btnAddFriend;
	private String id;
	AlertDialog  ownerDialog;

	public UserDetailHolder(Context context) {
		rootView = View.inflate(UiUtils.getContext(), R.layout.user_detail_info, null);
		this.context=context;
		initViews();
	}
	public UserDetailHolder(Context context,boolean hideAddBtn) {
		rootView = View.inflate(UiUtils.getContext(), R.layout.user_detail_info, null);
		this.context=context;
		initViews();
		if(hideAddBtn){
			btnAddFriend.setVisibility(View.INVISIBLE);
		}
	}

	private void initViews() {
		tvId = (TextView) rootView.findViewById(R.id.tv_user_detail_id);
		tvNickname = (TextView) rootView
				.findViewById(R.id.tv_user_detail_nickname);
		tvXingzuo = (TextView) rootView
				.findViewById(R.id.tv_user_detail_xingzuo);

		tvJingyan = (TextView) rootView
				.findViewById(R.id.tv_user_detail_jingyan);
		tvBiaoxian = (TextView) rootView
				.findViewById(R.id.tv_user_detail_biaoxian);
		tvLife = (TextView) rootView.findViewById(R.id.tv_user_detail_life);

		tvTotalVisit = (TextView) rootView
				.findViewById(R.id.tv_user_detail_totalvisit);
		tvTotalPost = (TextView) rootView
				.findViewById(R.id.tv_user_detail_totalpost);
		tvLastVisitTime = (TextView) rootView
				.findViewById(R.id.tv_user_detail_lastvisittime);

		tvLastVisitIp = (TextView) rootView
				.findViewById(R.id.tv_user_detail_lastvisitip);
		tvOnline = (TextView) rootView.findViewById(R.id.tv_user_detail_online);
		
		btnSendMail = (Button)rootView.findViewById(R.id.btn_send_mail);
		btnAddFriend = (Button) rootView.findViewById(R.id.btn_add_friend);
		
		btnSendMail.setOnClickListener(this);
		btnAddFriend.setOnClickListener(this);
		
	}

	public View getRootView() {
		return rootView;
	}

	public void updateStatus(final String userId) {
		id = userId;
		// 新开线程，联网更新
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {

				UserProtocol protocol = new UserProtocol();
				
				info = protocol.getUserInfoFromServer(userId);
				
				// 根据ip获取地址
				if (info!=null&&info.lastVistiIP != null) {
					HttpUtils httpUtils = new HttpUtils();
					httpUtils.send(HttpMethod.GET,
							Constants.getQueryIpUrl(info.lastVistiIP),
							new RequestCallBack<String>() {
								@Override
								public void onSuccess(
										ResponseInfo<String> responseInfo) {
									String result = responseInfo.result;
									if (result.contains("中国")) {
										result = result.replaceAll("中国", "")
												.trim();
									}
									
									result=result.replaceAll("(\\d|\\.)*", "").trim();
									
									
									LogUtil.d(info.lastVistiIP + ":" + result);

									tvLastVisitIp.setText(Html.fromHtml(UiUtils
											.getString(R.string.lastVistiIP)
											+ info.lastVistiIP
											+ "("
											+ result
											+ ")"));

								}

								@Override
								public void onFailure(HttpException error,
										String msg) {

								}
							});
				}else{
					
				}

				// 更新界面
				UiUtils.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (info != null) {
							String html = "<font color='purple' size='22px'>"
									+ info.id
									+ "</font><font size='16px' color='grey'>详细信息</font>";

							// tvId.setText(info.id+"详细信息");
							tvId.setText(Html.fromHtml(html));

							if (info.nickname != null) {
								tvNickname.setText(Html.fromHtml(UiUtils
										.getString(R.string.nickname)
										+ info.nickname));
							}
							if (info.xingzuo != null) {
								tvXingzuo.setText(Html.fromHtml(UiUtils
										.getString(R.string.xingzuo)
										+ info.xingzuo));
							}
							if (info.jingyan != null) {
								tvJingyan.setText(Html.fromHtml(UiUtils
										.getString(R.string.jingyan)
										+ info.jingyan));
							}
							if (info.biaoxian != null)
								tvBiaoxian.setText(Html.fromHtml(UiUtils
										.getString(R.string.biaoxian)
										+ info.biaoxian));

							if (info.life != null)
								tvLife.setText(Html.fromHtml(UiUtils
										.getString(R.string.life) + info.life));

							if (info.totalVisit != null)
								tvTotalVisit.setText(Html.fromHtml(UiUtils
										.getString(R.string.totalVisit)
										+ info.totalVisit + "次"));

							if (info.totalPub != null)
								tvTotalPost.setText(Html.fromHtml(UiUtils
										.getString(R.string.totalPub)
										+ info.totalPub + "篇"));

							if (info.lastVisitTime != null)
								tvLastVisitTime.setText(Html.fromHtml(UiUtils
										.getString(R.string.lastVisitTime)
										+ info.lastVisitTime));

							if (info.lastVistiIP != null)
								tvLastVisitIp.setText(Html.fromHtml(UiUtils
										.getString(R.string.lastVistiIP)
										+ info.lastVistiIP));

							if (info.isOnline) {
								tvOnline.setText(Html
										.fromHtml("<font color='grey'>是否在线：</font>"
												+ "<font color='green'>是</font>"));
							} else {
								tvOnline.setText(Html
										.fromHtml("<font color='grey'>是否在线：</font>"
												+ "<font color='red'>否</font>"));
							}
							//MyToast.toast("加载成功。");
						}else{
							MyToast.toast("加载失败，该用户不存在！");
						}

					}
				});

			}

		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send_mail:
			//发送站内信
			Intent intent=new Intent(context,MailNewActivity.class);
			if(info!=null){
				intent.putExtra("receiver",info.id);
			}
			context.startActivity(intent);
			
			break;
			
		case R.id.btn_add_friend://添加好友
			//弹出对话框
			if(ownerDialog!=null){
				ownerDialog.dismiss();
			}
			AddFriendDialog dialog=new AddFriendDialog(context);
			dialog.setAddId(id);
			dialog.show();
			break;
		default:
			break;
		}
	}

	public void setOwnerDialog(AlertDialog dialog){
		this.ownerDialog=dialog;
	}
}
