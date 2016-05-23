package com.oterman.njubbs.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 str="njubbs_v0.7 \n " +
		"更新日志：" +
		"\n 1.实现了长按帖子删除功能" +
		"\n 2.实现了发帖时添加表情功能";
 str="njubbs_v0.8 \n " +
		"更新日志：" +
		"\n 1.实现了回帖" +
		"\n 2.实现了修改回帖";
 str="njubbs_v0.9 \n " +
		"更新日志：" +
		"\n 1.站内信的查看" +
		"\n 2.站内信的发送，回复，删除";
 *
 */
public class DiscoveryFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView tv=new TextView(getActivity());
		
		String str="我爱你哦。晚安了哦";
		tv.setText(str);
		
		return tv;
	}

}
