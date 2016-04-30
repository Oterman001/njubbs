package com.oterman.njubbs.fragment;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {

	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		tv.setText("发现更多");
		return tv;
	}

}
