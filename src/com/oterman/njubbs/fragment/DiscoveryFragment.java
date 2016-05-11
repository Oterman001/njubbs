package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;

public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.3  更新日志：" +
				"\n 1.更优雅的实现了版面帖子加载下一页以及帖子内容加载下一页；" +
				"\n 2.实现了各区热点，重新设计了布局。";
		tv.setText(str);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}

}
