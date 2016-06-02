package com.oterman.njubbs.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.LoginActivity;
import com.oterman.njubbs.activity.SettingActivity;
import com.oterman.njubbs.activity.expore.FriendsActivity;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.activity.mail.MailBoxActicity;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.protocol.CheckNewMailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

@SuppressLint("NewApi")
public class AboutMeFragment  extends Fragment implements OnClickListener {

	private View rootView;
	private Button btnLogin;
	private UserInfo userInfo;
	private TextView tvId;
	private TextView tvNickName;
	private TextView tvXingzuo;
	private TextView tvJingyan;
	private TextView tvLife;
	private TextView tvBiaoxian;
	private TextView tvTotalVisit;
	private TextView tvUnlogin;
	private ViewGroup llUserContainer;
	
	private TextView tvMail;
	private LinearLayout llMail;
	private LinearLayout llFriends;
	private LinearLayout llSetting;
	private LinearLayout llMyTopic;
	
	private ImageView ivMail;
	
	private int newMailCount;
	private TextView tvNewMailCount;
	
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.d("AboutMeFragment-onResume：检查新邮件" );
		
		checkHasNewMail();
		
		//更新视图
//		if(userInfo!=null){
			updateViews();
//		}
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		LogUtil.d("关于我界面：onCreateView()执行了...");
		
		rootView = View.inflate(getContext(), R.layout.frag_about_me, null);
		
		userInfo=BaseApplication.getLogedUser();//获取登录的user
		
		btnLogin = (Button) rootView.findViewById(R.id.btn_login);
		llMail = (LinearLayout) rootView.findViewById(R.id.ll_mail2);//站内
		llFriends = (LinearLayout) rootView.findViewById(R.id.ll_friends);//好友
		llSetting = (LinearLayout) rootView.findViewById(R.id.ll_setting);//设置
		llMyTopic = (LinearLayout) rootView.findViewById(R.id.ll_my_topic);//我的帖子
		ivMail = (ImageView) rootView.findViewById(R.id.iv_mail);
		
		tvNewMailCount = (TextView) rootView.findViewById(R.id.tv_new_mail_count);		

		
		btnLogin.setOnClickListener(this);//登陆、注销按钮
		
		//tvMail.setOnClickListener(this);//站内
		llMail.setOnClickListener(this);
		llFriends.setOnClickListener(this);//反馈
		llSetting.setOnClickListener(this);//反馈
		llMyTopic.setOnClickListener(this);//反馈
		
		initUserViews();
		
		
		return rootView;
	}

	/**
	 * 初始化用户信息相关的视图   顶部视图
	 */
	private void initUserViews() {
		
		tvUnlogin = (TextView) rootView.findViewById(R.id.tv_unlogin);//未登录视图
		llUserContainer = (ViewGroup) rootView.findViewById(R.id.user_container);//登陆视图
		
		if(BaseApplication.getCookie()!=null&&BaseApplication.getLogedUser()!=null){//已登录状态登陆
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			
			btnLogin.setText("注销");
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//未登录
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			
			btnLogin.setText("登陆");
			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
			
		}
		
		tvId = (TextView) rootView.findViewById(R.id.tv_user_id);
		tvNickName = (TextView) rootView.findViewById(R.id.tv_user_nickname);
		tvXingzuo = (TextView) rootView.findViewById(R.id.tv_user_xingzuo);
		tvJingyan = (TextView) rootView.findViewById(R.id.tv_user_jingyan);
		tvLife = (TextView) rootView.findViewById(R.id.tv_user_shengmingli);
		tvBiaoxian = (TextView) rootView.findViewById(R.id.tv_user_biaoxianzhi);
		tvTotalVisit = (TextView) rootView.findViewById(R.id.tv_user_totalvisit);
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data!=null){
			userInfo = (UserInfo) data.getSerializableExtra("userInfo");
			LogUtil.d("返回数据："+userInfo.toString());
		}
		updateViews();
		
		
	}
	
	public void updateViews() {
		if(llUserContainer!=null&&tvUnlogin!=null&&BaseApplication.getCookie()!=null){
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			btnLogin.setText("注销");
			btnLogin.setBackgroundColor(0x88ff0000);
		}
		
		if(BaseApplication.getCookie()!=null){//已登录状态登陆
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			btnLogin.setText("注销");
//			btnLogin.setBackgroundColor(0x88ff0000);
			btnLogin.setBackground(getResources().getDrawable(R.drawable.logout_bg_selector));
//			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//未登录
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			btnLogin.setText("登陆");
//			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
			btnLogin.setBackground(getResources().getDrawable(R.drawable.login_bg_selector));
		}
		
		//更新视图
		if(userInfo!=null){
			updateUserDetail2();
		}
		
	}
	/*
	 * 		SpannableStringBuilder ssb=new SpannableStringBuilder(title);
			int start=0;
			int end=start+" 置顶 ".length();
			
			ssb.setSpan(new AbsoluteSizeSpan(UiUtils.dip2px(15)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvTitle.setText(ssb);
	 * 
	*/
	private void updateUserDetail2() {
		String id="id  ："+userInfo.id;
		SpannableStringBuilder ssb=new SpannableStringBuilder(id);
		int start="id  ：".length();
		int end=start+userInfo.id.length();
		ssb.setSpan(new AbsoluteSizeSpan(UiUtils.dip2px(26)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//		ssb.setSpan(new ForegroundColorSpan(0xff8a2be2), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		tvId.setText(ssb);
		
//		tvId.setText(Html.fromHtml(getResources().getString(R.string.id2)+""+userInfo.id+""));
		
		tvNickName.setText(Html.fromHtml(getResources().getString(R.string.nickname2)+userInfo.nickname));
		tvXingzuo.setText(Html.fromHtml(getResources().getString(R.string.xingzuo2)+userInfo.xingzuo));
		tvJingyan.setText(Html.fromHtml(getResources().getString(R.string.jingyan2)+userInfo.jingyan));
		tvLife.setText(Html.fromHtml(getResources().getString(R.string.life2)+userInfo.life));
		tvBiaoxian.setText(Html.fromHtml(getResources().getString(R.string.biaoxian2)+userInfo.biaoxian));
		tvTotalVisit.setText(Html.fromHtml(getResources().getString(R.string.totalVisit2)+userInfo.totalVisit+"次"));
	}
	private void updateUserDetail() {
		tvId.setText(Html.fromHtml(getResources().getString(R.string.id2)+""+userInfo.id+""));
		tvNickName.setText(Html.fromHtml(getResources().getString(R.string.nickname2)+userInfo.nickname));
		tvXingzuo.setText(Html.fromHtml(getResources().getString(R.string.xingzuo2)+userInfo.xingzuo));
		tvJingyan.setText(Html.fromHtml(getResources().getString(R.string.jingyan2)+userInfo.jingyan));
		tvLife.setText(Html.fromHtml(getResources().getString(R.string.life2)+userInfo.life));
		tvBiaoxian.setText(Html.fromHtml(getResources().getString(R.string.biaoxian2)+userInfo.biaoxian));
		tvTotalVisit.setText(Html.fromHtml(getResources().getString(R.string.totalVisit2)+userInfo.totalVisit+"次"));
	}

	//检查是否有新的站内
		public void checkHasNewMail() {
			ThreadManager.getInstance().createLongPool().execute(new Runnable() {
				@Override
				public void run() {
					LogUtil.d("检查是否有新邮件啦");
					CheckNewMailProtocol protocol=new CheckNewMailProtocol();
					String url=Constants.HAS_NEW_MAIL_URL;
					newMailCount = protocol.checkFromServer(url,getContext());
					LogUtil.d("检查结果："+newMailCount);
					if(newMailCount>0){//有新邮件
						//更新状态
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvNewMailCount.setVisibility(View.VISIBLE);
								tvNewMailCount.setText(newMailCount+"");
							}
						});
					}else{//没有新邮件
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tvNewMailCount.setVisibility(View.INVISIBLE);
							}
						});
					}
				}
			});
		}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if(BaseApplication.getCookie()!=null&&BaseApplication.getLogedUser()!=null){//当前为登陆状态，注销
				BaseApplication.setCookie(null);
				MyToast.toast("注销成功");
				BaseApplication.setLogedUser(null);
				updateViews();
				return;
			}else{
				Intent intent=new Intent(getContext(),LoginActivity.class);
				startActivityForResult(intent, 100);
			}
			break;
			
		case R.id.ll_mail2://点击的是站内
			Intent intent2=new Intent(getContext(),MailBoxActicity.class);
			startActivity(intent2);
			break;
		case R.id.ll_friends://点击
			Intent friendsIntent=new Intent(getContext(),FriendsActivity.class);
			startActivity(friendsIntent);
			break;
		case R.id.ll_setting://设置
//			MyToast.toast("不想设置");
			Intent settingIntent=new Intent(getContext(),SettingActivity.class);
			
			startActivity(settingIntent);
			break;
		case R.id.ll_my_topic://我的帖子
			Intent myIntent=new Intent(getContext(),MyTopicActivity.class);
			startActivity(myIntent);
			break;
		default:
			break;
		}
	}

}
