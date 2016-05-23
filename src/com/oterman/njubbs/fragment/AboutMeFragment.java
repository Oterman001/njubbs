package com.oterman.njubbs.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.LoginActivity;
import com.oterman.njubbs.activity.MailBoxActicity;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;

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
	private TextView tvQianming;
	private TextView tvUnlogin;
	private ViewGroup llUserContainer;
	private TextView tvMail;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		LogUtil.d("关于我界面：onCreateView()执行了...");
		
		rootView = View.inflate(getContext(), R.layout.frag_about_me, null);
		
		userInfo=BaseApplication.getLogedUser();//获取登录的user
		
		btnLogin = (Button) rootView.findViewById(R.id.btn_login);
		tvMail = (TextView) rootView.findViewById(R.id.tv_mail);
		
		btnLogin.setOnClickListener(this);//登陆、注销按钮
		tvMail.setOnClickListener(this);//站内
		
		
		initUserViews();
		
		if(userInfo!=null){
			updateViews();
		}
		
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
		tvQianming = (TextView) rootView.findViewById(R.id.tv_user_qianmingdang);
		
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
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//未登录
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			btnLogin.setText("登陆");
			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
		}
		
		if(userInfo!=null){
			tvId.setText(getResources().getString(R.string.id)+""+userInfo.id+"");
			tvNickName.setText(getResources().getString(R.string.nickname)+userInfo.nickname);
			tvXingzuo.setText(getResources().getString(R.string.xingzuo)+userInfo.xingzuo);
			tvJingyan.setText(getResources().getString(R.string.jingyan)+userInfo.jingyan);
			tvLife.setText(getResources().getString(R.string.life)+userInfo.life);
			tvBiaoxian.setText(getResources().getString(R.string.biaoxian)+userInfo.biaoxian);
			tvQianming.setText(getResources().getString(R.string.qianmingdang)+"暂无");
		}
		
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
			
		case R.id.tv_mail://点击的是站内
			Intent intent2=new Intent(getContext(),MailBoxActicity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

}
