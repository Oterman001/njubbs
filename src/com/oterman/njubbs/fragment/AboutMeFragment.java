package com.oterman.njubbs.fragment;

import android.view.View;
import android.widget.TextView;

public class AboutMeFragment extends BaseFragment {

	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		tv.setText("ÎÒµÄÖ÷Ò³");
		return tv;
	}

}
