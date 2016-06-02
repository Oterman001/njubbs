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
		llFriends = (LinearLayout) rootView.findViewById(R.id.ll_friends);//����
		llSetting = (LinearLayout) rootView.findViewById(R.id.ll_setting);//����
		llMyTopic = (LinearLayout) rootView.findViewById(R.id.ll_my_topic);//�ҵ�����
		ivMail = (ImageView) rootView.findViewById(R.id.iv_mail);
		
		tvNewMailCount = (TextView) rootView.findViewById(R.id.tv_new_mail_count);		

		
		btnLogin.setOnClickListener(this);//��½��ע����ť
		
		//tvMail.setOnClickListener(this);//վ��
		llMail.setOnClickListener(this);
		llFriends.setOnClickListener(this);//����
		llSetting.setOnClickListener(this);//����
		llMyTopic.setOnClickListener(this);//����
		
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
		tvTotalVisit = (TextView) rootView.findViewById(R.id.tv_user_totalvisit);
		
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
//			btnLogin.setBackgroundColor(0x88ff0000);
			btnLogin.setBackground(getResources().getDrawable(R.drawable.logout_bg_selector));
//			btnLogin.setBackgroundColor(0x88ff0000);
			
		}else{//δ��¼
			llUserContainer.setVisibility(View.INVISIBLE);
			tvUnlogin.setVisibility(View.VISIBLE);
			btnLogin.setText("��½");
//			btnLogin.setBackgroundColor(getResources().getColor(R.color.main_green));
			btnLogin.setBackground(getResources().getDrawable(R.drawable.login_bg_selector));
		}
		
		//������ͼ
		if(userInfo!=null){
			updateUserDetail2();
		}
		
	}
	/*
	 * 		SpannableStringBuilder ssb=new SpannableStringBuilder(title);
			int start=0;
			int end=start+" �ö� ".length();
			
			ssb.setSpan(new AbsoluteSizeSpan(UiUtils.dip2px(15)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.tvTitle.setText(ssb);
	 * 
	*/
	private void updateUserDetail2() {
		String id="id  ��"+userInfo.id;
		SpannableStringBuilder ssb=new SpannableStringBuilder(id);
		int start="id  ��".length();
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
		tvTotalVisit.setText(Html.fromHtml(getResources().getString(R.string.totalVisit2)+userInfo.totalVisit+"��"));
	}
	private void updateUserDetail() {
		tvId.setText(Html.fromHtml(getResources().getString(R.string.id2)+""+userInfo.id+""));
		tvNickName.setText(Html.fromHtml(getResources().getString(R.string.nickname2)+userInfo.nickname));
		tvXingzuo.setText(Html.fromHtml(getResources().getString(R.string.xingzuo2)+userInfo.xingzuo));
		tvJingyan.setText(Html.fromHtml(getResources().getString(R.string.jingyan2)+userInfo.jingyan));
		tvLife.setText(Html.fromHtml(getResources().getString(R.string.life2)+userInfo.life));
		tvBiaoxian.setText(Html.fromHtml(getResources().getString(R.string.biaoxian2)+userInfo.biaoxian));
		tvTotalVisit.setText(Html.fromHtml(getResources().getString(R.string.totalVisit2)+userInfo.totalVisit+"��"));
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
								tvNewMailCount.setVisibility(View.VISIBLE);
								tvNewMailCount.setText(newMailCount+"");
							}
						});
					}else{//û�����ʼ�
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
		case R.id.ll_friends://���
			Intent friendsIntent=new Intent(getContext(),FriendsActivity.class);
			startActivity(friendsIntent);
			break;
		case R.id.ll_setting://����
//			MyToast.toast("��������");
			Intent settingIntent=new Intent(getContext(),SettingActivity.class);
			
			startActivity(settingIntent);
			break;
		case R.id.ll_my_topic://�ҵ�����
			Intent myIntent=new Intent(getContext(),MyTopicActivity.class);
			startActivity(myIntent);
			break;
		default:
			break;
		}
	}

}
