package com.oterman.njubbs.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class TopTenFragment extends BaseFragment {

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//��ʼ����һҳ
		showViewFromServer();
	}
	
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		
		tv.setText("ʮ��");
		return tv;
	}

}
