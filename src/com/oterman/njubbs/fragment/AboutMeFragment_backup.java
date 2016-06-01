package com.oterman.njubbs.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import com.oterman.njubbs.activity.mail.MailBoxActicity;
import com.oterman.njubbs.activity.mail.MailNewActivity;
import com.oterman.njubbs.bean.UserInfo;
import com.oterman.njubbs.protocol.CheckNewMailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

@SuppressLint("NewApi")
public class AboutMeFragment_backup  extends Fragment implements OnClickListener {

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
	private LinearLayout llMail;
	private LinearLayout llFeedback;
	private LinearLayout llSetting;
	private LinearLayout llMoney;
	private ImageView ivMail;
	
	private int newMailCount;
	
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.d("AboutMeFragment-onResume��������ʼ�" );
		
		checkHasNewMail();
		
		//������ͼ
//		if(userInfo!=null){
			updateViews();
//		}
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		LogUtil.d("�����ҽ��棺onCreateView()ִ����...");
		
		rootView = View.inflate(getContext(), R.layout.frag_about_me, null);
		
		userInfo=BaseApplication.getLogedUser();//��ȡ��¼��user
		
		btnLogin = (Button) rootView.findViewById(R.id.btn_login);
		llMail = (LinearLayout) rootView.findViewById(R.id.ll_mail2);//վ��
		llFeedback = (LinearLayout) rootView.findViewById(R.id.ll_feedback);//����
		llSetting = (LinearLayout) rootView.findViewById(R.id.ll_setting);//����
		llMoney = (LinearLayout) rootView.findViewById(R.id.ll_money);//����
		ivMail = (ImageView) rootView.findViewById(R.id.iv_mail);
		
		
		
		btnLogin.setOnClickListener(this);//��½��ע����ť
		
		//tvMail.setOnClickListener(this);//վ��
		llMail.setOnClickListener(this);
		llFeedback.setOnClickListener(this);//����
		llSetting.setOnClickListener(this);//����
		llMoney.setOnClickListener(this);//����
		
		initUserViews();
		
		
		return rootView;
	}

	/**
	 * ��ʼ���û���Ϣ��ص���ͼ   ������ͼ
	 */
	private void initUserViews() {
		
		tvUnlogin = (TextView) rootView.findViewById(R.id.tv_unlogin);//δ��¼��ͼ
		llUserContainer = (ViewGroup) rootView.findViewById(R.id.user_container);//��½��ͼ
		
		if(BaseApplication.getCookie()!=null&&BaseApplication.getLogedUser()!=null){//�ѵ�¼״̬��½
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			
			btnLogin.setText("ע��");
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//δ��¼
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			
			btnLogin.setText("��½");
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
			LogUtil.d("�������ݣ�"+userInfo.toString());
		}
		updateViews();
		
		
	}
	
	public void updateViews() {
		if(llUserContainer!=null&&tvUnlogin!=null&&BaseApplication.getCookie()!=null){
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			btnLogin.setText("ע��");
			btnLogin.setBackgroundColor(0x88ff0000);
		}
		
		if(BaseApplication.getCookie()!=null){//�ѵ�¼״̬��½
			llUserContainer.setVisibility(View.VISIBLE);
			tvUnlogin.setVisibility(View.INVISIBLE);
			btnLogin.setText("ע��");
			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//δ��¼
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			btnLogin.setText("��½");
			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
		}
		
		if(userInfo!=null){
			tvId.setText(Html.fromHtml(getResources().getString(R.string.id2)+""+userInfo.id+""));
			tvNickName.setText(Html.fromHtml(getResources().getString(R.string.nickname2)+userInfo.nickname));
			tvXingzuo.setText(Html.fromHtml(getResources().getString(R.string.xingzuo2)+userInfo.xingzuo));
			tvJingyan.setText(Html.fromHtml(getResources().getString(R.string.jingyan2)+userInfo.jingyan));
			tvLife.setText(Html.fromHtml(getResources().getString(R.string.life2)+userInfo.life));
			tvBiaoxian.setText(Html.fromHtml(getResources().getString(R.string.biaoxian2)+userInfo.biaoxian));
			tvQianming.setText(Html.fromHtml(getResources().getString(R.string.qianmingdang2)+"����"));
		}
		
	}

	//����Ƿ����µ�վ��
		public void checkHasNewMail() {
			ThreadManager.getInstance().createLongPool().execute(new Runnable() {
				@Override
				public void run() {
					LogUtil.d("����Ƿ������ʼ���");
					CheckNewMailProtocol protocol=new CheckNewMailProtocol();
					String url=Constants.HAS_NEW_MAIL_URL;
					newMailCount = protocol.checkFromServer(url,getContext());
					LogUtil.d("�������"+newMailCount);
					if(newMailCount>0){//�����ʼ�
						//����״̬
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Drawable drawable = getResources().getDrawable(R.drawable.icon_my_message2);
	//							drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
								ivMail.setBackground(drawable);
							}
						});
					}else{//û�����ʼ�
						UiUtils.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Drawable drawable = getResources().getDrawable(R.drawable.icon_my_message);
	//							drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
								ivMail.setBackground(drawable);
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
			if(BaseApplication.getCookie()!=null&&BaseApplication.getLogedUser()!=null){//��ǰΪ��½״̬��ע��
				BaseApplication.setCookie(null);
				MyToast.toast("ע���ɹ�");
				BaseApplication.setLogedUser(null);
				updateViews();
				return;
			}else{
				Intent intent=new Intent(getContext(),LoginActivity.class);
				startActivityForResult(intent, 100);
			}
			break;
			
		case R.id.ll_mail2://�������վ��
			Intent intent2=new Intent(getContext(),MailBoxActicity.class);
			startActivity(intent2);
			break;
		case R.id.ll_feedback://�������վ��
			Intent intent3=new Intent(getContext(),MailNewActivity.class);
			intent3.putExtra("receiver","oterman");
			intent3.putExtra("title","����");
			startActivity(intent3);
			break;
		case R.id.ll_setting://����
			MyToast.toast("��������");
			break;
		case R.id.ll_money://����
			MyToast.toast("�Ͳ�����");
			break;
		default:
			break;
		}
	}

}
