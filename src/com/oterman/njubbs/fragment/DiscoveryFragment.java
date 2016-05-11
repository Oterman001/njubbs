package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.3  ������־��" +
				"\n 1.�����ŵ�ʵ���˰������Ӽ�����һҳ�Լ��������ݼ�����һҳ��" +
				"\n 2.ʵ���˸����ȵ㣬��������˲��֡�";
		tv.setText(str);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
