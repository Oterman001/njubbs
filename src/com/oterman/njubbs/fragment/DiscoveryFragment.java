package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.5 \n " +
				"������־��" +
				"\n 1.ʵ������ʾ����" +
				"\n 2.��ǿʵ���˻�ȡ�ղصİ���";
		tv.setText(str);
		tv.setTextSize(22f);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
