package com.oterman.njubbs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
public class DiscoveryFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv=new TextView(getActivity());
		
		String str="�Ұ���Ŷ������Ŷ";
		tv.setText(str);
		
		return tv;
	}

}
