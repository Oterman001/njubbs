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
import com.oterman.njubbs.activity.expore.ColleagesActivity;
import com.oterman.njubbs.activity.expore.FriendsActivity;


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
	private LinearLayout llFriends;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = View.inflate(getContext(), R.layout.fragment_explore, null);
		llColleages = (LinearLayout) view.findViewById(R.id.ll_colleages);
		
		llFriends = (LinearLayout) view.findViewById(R.id.ll_friends);
		
		llColleages.setOnClickListener(this);
		llFriends.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_colleages:
			Intent intent=new Intent(getContext(),ColleagesActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_friends:
			Intent intent2=new Intent(getContext(),FriendsActivity.class);
			startActivity(intent2);
			break;

		default:
			break;
		}
		
	}
	
	


}
