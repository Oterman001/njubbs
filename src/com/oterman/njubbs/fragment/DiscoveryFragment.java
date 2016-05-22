package com.oterman.njubbs.fragment;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.oterman.njubbs.view.LoadingView.LoadingState;


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
public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		
		String str="楼主 mmlover(大蘑菇)";
		tv.setTextSize(22f);
		SpannableStringBuilder ssb=new SpannableStringBuilder(str);
		int start=0;
		int end=start+"楼主".length();
		
		ssb.setSpan(new BackgroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		tv.setText(ssb);
		
		
		return tv;
	}
	
	@Override
	public LoadingState loadDataFromServer() {
		return LoadingState.LOAD_SUCCESS;
	}
	

}
