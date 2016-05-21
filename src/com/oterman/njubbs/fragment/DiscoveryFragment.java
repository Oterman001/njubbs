package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
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
 *
 */
public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String   str="njubbs_v0.8 \n " +
				"������־��" +
				"\n 1.ʵ���˻���" +
				"\n 2.ʵ�����޸Ļ���";
		tv.setText(str);
		tv.setTextSize(22f);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}
	

}
