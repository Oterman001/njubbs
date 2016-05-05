package com.oterman.njubbs.fragment.secondary;

import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class TopAllFragment  extends BaseFragment{

	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getContext());
		tv.setText("各区热门");
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
