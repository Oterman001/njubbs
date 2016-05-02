package com.oterman.njubbs.activity;

import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/*
 *	公共的activity
 *  初始化控件
 *  初始化actionbar
 *  管理所有的activity  在退出的时候，能够一键退出应用程序
 *
 */
public class BaseActivity extends FragmentActivity {

	public static List<BaseActivity> activities=new LinkedList<BaseActivity>();
	private FinishAllReceiver receiver;
	
	public static BaseActivity activity;//定义一个成员变量  当启动一个新的阿成vitity时，判断是否需要加入新的任务栈的标记
	
	@Override
	protected void onResume() {
		super.onResume();
		activity=this;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//往activity集合中添加
		activities.add(this);
		
		//注册广播接收者，必须在结束的时候取消注册
		IntentFilter filter=new IntentFilter("com.oterman.receiver.finishAll");
		
		receiver = new FinishAllReceiver();
		registerReceiver(receiver, filter);
		
		init();//初始化
		initView();// 初始化控件
		initActionBar();// 初始化actionbar
	}
	
	protected void init() {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消注册广播
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
		
		//从集合中移除数据
		activities.remove(this);
		finish();
	}
	/**
	 * 结束所有的activity
	 */
	public void finishAllActivity(){
		//由于集合在遍历的时候，不能移除,所以得备份一下
		List<BaseActivity> backUP=new LinkedList<>(activities);
		for (BaseActivity baseActivity : backUP) {
			baseActivity.finish();
			activities.remove(baseActivity);
		}
		
		//杀死进程
		Process.killProcess(Process.myPid());
		
	}

	/**
	 * 结束程序的另一种方法  定义一个广播接收者，当接收到广播的时候，结束activity
	 */
	class FinishAllReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			
			finish();
		}
	}
	
	/**
	 * 初始化控件
	 */
	protected void initView() {

	}

	/**
	 * 初始化actionbar
	 */
	protected void initActionBar() {
		
	}
}
