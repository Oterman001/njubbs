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
		rootView = View.inflate(getContext(), R.layout.frag_about_me, null);
		
		userInfo=BaseApplication.userInfo;
		btnLogin = (Button) rootView.findViewById(R.id.btn_login);
		tvMail = (TextView) rootView.findViewById(R.id.tv_mail);
		
		btnLogin.setOnClickListener(this);
		tvMail.setOnClickListener(this);
		
		
		initUserViews();
		
		if(userInfo!=null){
			updateViews();
		}
		
		return rootView;
	}

	private void initUserViews() {
		tvUnlogin = (TextView) rootView.findViewById(R.id.tv_unlogin);
		llUserContainer = (ViewGroup) rootView.findViewById(R.id.user_container);
		
		if(BaseApplication.isLogin){//ÒÑµÇÂ¼×´Ì¬µÇÂ½
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			
			btnLogin.setText("×¢Ïú");
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//Î´µÇÂ¼
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			
			btnLogin.setText("µÇÂ½");
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
			LogUtil.d("·µ»ØÊý¾Ý£º"+userInfo.toString());
			BaseApplication.isLogin=true;
		}
		updateViews();
		
	}
	
	private void updateViews() {
		if(llUserContainer!=null&&tvUnlogin!=null&&BaseApplication.isLogin){
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			btnLogin.setText("×¢Ïú");
			btnLogin.setBackgroundColor(0x88ff0000);
		}
		
		if(BaseApplication.isLogin){//ÒÑµÇÂ¼×´Ì¬µÇÂ½
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			
			btnLogin.setText("×¢Ïú");
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//Î´µÇÂ¼
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			
			btnLogin.setText("µÇÂ½");
			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
			
		}
		
		if(userInfo!=null){
			tvId.setText(getResources().getString(R.string.id)+""+userInfo.id+"");
			tvNickName.setText(getResources().getString(R.string.nickname)+userInfo.nickname);
			tvXingzuo.setText(getResources().getString(R.string.xingzuo)+userInfo.xingzuo);
			tvJingyan.setText(getResources().getString(R.string.jingyan)+userInfo.jingyan);
			tvLife.setText(getResources().getString(R.string.life)+userInfo.life);
			tvBiaoxian.setText(getResources().getString(R.string.biaoxian)+userInfo.biaoxian);
			tvQianming.setText(getResources().getString(R.string.qianmingdang)+"ÔÝÎÞ");
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if(BaseApplication.isLogin){//µ±Ç°ÎªµÇÂ½×´Ì¬£¬
				BaseApplication.isLogin=false;
			}
			
			Intent intent=new Intent(getContext(),LoginActivity.class);
			
//			startActivity(intent);
			startActivityForResult(intent, 100);
			break;
			
		case R.id.tv_mail:
			Intent intent2=new Intent(getContext(),MailBoxActicity.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
	}

}
