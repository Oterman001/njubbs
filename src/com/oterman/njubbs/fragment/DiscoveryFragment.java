package com.oterman.njubbs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.expore.AddFriendActivity;
import com.oterman.njubbs.activity.expore.ColleagesActivity;
import com.oterman.njubbs.activity.expore.FindBoardActivity;
import com.oterman.njubbs.activity.expore.FindTopicActivity;
import com.oterman.njubbs.activity.expore.FriendsActivity;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.activity.mail.MailNewActivity;
import com.oterman.njubbs.utils.MyToast;


/**
 * 
String str="njubbs_v0.5 \n " +
		"������־��" +
		"\n 1.ʵ������ʾ����" +
		"\n 2.��ǿʵ���˻�ȡ�ղصİ���";

 str="njubbs_v0.6 \n " +
		"������־��" +
		"\n 1.ʵ���˷���" +
		"\n 2.�Ż��˰��������б�";
 str="njubbs_v0.7 \n " +
		"������־��" +
		"\n 1.ʵ���˳�������ɾ������" +
		"\n 2.ʵ���˷���ʱ��ӱ��鹦��";
 str="njubbs_v0.8 \n " +
		"������־��" +
		"\n 1.ʵ���˻���" +
		"\n 2.ʵ�����޸Ļ���";
 str="njubbs_v0.9 \n " +
		"������־��" +
		"\n 1.վ���ŵĲ鿴" +
		"\n 2.վ���ŵķ��ͣ��ظ���ɾ��";
 *
 */
public class DiscoveryFragment extends Fragment implements OnClickListener {
	

	private LinearLayout llColleages;
	private LinearLayout llFindUser;
	private LinearLayout llFindBoard;
	private LinearLayout llFindTopic;
	
//	private LinearLayout llFriends;
//	private LinearLayout llMyTopic;
	
	private LinearLayout llMoney;
	private LinearLayout llFeedback;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = View.inflate(getContext(), R.layout.fragment_explore, null);
		llColleages = (LinearLayout) view.findViewById(R.id.ll_colleages);
		llMoney = (LinearLayout) view.findViewById(R.id.ll_money);
		llFindUser=(LinearLayout) view.findViewById(R.id.ll_find_user);
		llFindBoard=(LinearLayout) view.findViewById(R.id.ll_find_board);
		llFindTopic=(LinearLayout) view.findViewById(R.id.ll_find_topic);
		llFeedback=(LinearLayout) view.findViewById(R.id.ll_feedback);
		
		//���ü���
		llColleages.setOnClickListener(this);
		llMoney.setOnClickListener(this);
		llFindUser.setOnClickListener(this);
		llFindBoard.setOnClickListener(this);
		llFindTopic.setOnClickListener(this);
		llFeedback.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_colleages:
			Intent intent=new Intent(getContext(),ColleagesActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_money://����
			MyToast.toast("�������");
//			Intent intent2=new Intent(getContext(),FriendsActivity.class);
//			startActivity(intent2);
			break;
		case R.id.ll_find_user:
			Intent findIntent=new Intent(getContext(),AddFriendActivity.class);
			startActivity(findIntent);
			break;
			
		case R.id.ll_find_board:
			Intent findBoardIntent=new Intent(getContext(),FindBoardActivity.class);
			startActivity(findBoardIntent);
			break;
		case R.id.ll_find_topic:
			Intent findTopicIntent=new Intent(getContext(),FindTopicActivity.class);
			startActivity(findTopicIntent);
			break;
		case R.id.ll_feedback://����
//			Intent myIntent=new Intent(getContext(),MyTopicActivity.class);
//			startActivity(myIntent);
			Intent intent3=new Intent(getContext(),MailNewActivity.class);
			intent3.putExtra("receiver","oterman");
			intent3.putExtra("title","����");
			startActivity(intent3);
			break;

		default:
			break;
		}
		
	}
	
	


}
