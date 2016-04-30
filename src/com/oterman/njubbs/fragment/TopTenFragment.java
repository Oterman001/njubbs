package com.oterman.njubbs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class TopTenFragment extends BaseFragment {

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//初始化第一页
		showViewFromServer();
	}
	
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		
		tv.setText("十大");
		return tv;
	}

}
