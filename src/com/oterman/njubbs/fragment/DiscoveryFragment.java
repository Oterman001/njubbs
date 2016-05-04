package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {

	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.2 ������־��" +
				"\n1.���뻺�棬ʵ������ˢ��" +
				"\n2.ʵ�������Ű��";
		tv.setText(str);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
