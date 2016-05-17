package com.oterman.njubbs.fragment;

import com.oterman.njubbs.view.LoadingView.LoadingState;

import android.view.View;
import android.widget.TextView;


/**
 * 
String str="njubbs_v0.5 \n " +
		"更新日志：" +
		"\n 1.实现了显示表情" +
		"\n 2.勉强实现了获取收藏的版面";

 str="njubbs_v0.6 \n " +
		"更新日志：" +
		"\n 1.实现了发帖" +
		"\n 2.优化了版面帖子列表";
 *
 */
public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		String str="njubbs_v0.6 \n " +
					"更新日志：" +
					"\n 1.实现了发帖" +
					"\n 2.优化了版面帖子列表";
		tv.setText(str);
		tv.setTextSize(22f);
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}
	

}
