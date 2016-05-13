package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.4 \n " +
				"������־��" +
				"\n 1.ʵ���˵�½ע��" +
				"\n 2.ʵ���˼���ͼƬ";
		tv.setText(str);
		tv.setTextSize(22f);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
