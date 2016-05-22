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
		"������־��" +
		"\n 1.ʵ������ʾ����" +
		"\n 2.��ǿʵ���˻�ȡ�ղصİ���";

 str="njubbs_v0.6 \n " +
		"������־��" +
		"\n 1.ʵ���˷���" +
		"\n 2.�Ż��˰��������б�";
 str="njubbs_v0.7 \n " +
		"������־��" +
		"\n 1.ʵ���˳�������ɾ������" +
		"\n 2.ʵ���˷���ʱ��ӱ��鹦��";
 str="njubbs_v0.8 \n " +
		"������־��" +
		"\n 1.ʵ���˻���" +
		"\n 2.ʵ�����޸Ļ���";
 str="njubbs_v0.9 \n " +
		"������־��" +
		"\n 1.վ���ŵĲ鿴" +
		"\n 2.վ���ŵķ��ͣ��ظ���ɾ��";
 *
 */
public class DiscoveryFragment extends BaseFragment {
	@Override
	public View createSuccessView() {
		TextView tv=new TextView(getActivity());
		
		String str="¥�� mmlover(��Ģ��)";
		tv.setTextSize(22f);
		SpannableStringBuilder ssb=new SpannableStringBuilder(str);
		int start=0;
		int end=start+"¥��".length();
		
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
