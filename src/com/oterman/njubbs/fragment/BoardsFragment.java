package com.oterman.njubbs.fragment;

import android.view.View;
import android.widget.TextView;

public class BoardsFragment extends BaseFragment {


	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		
		tv.setText("»»√≈∞ÂøÈ");
		return tv;
	}

}
