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
 *	������activity
 *  ��ʼ���ؼ�
 *  ��ʼ��actionbar
 *  �������е�activity  ���˳���ʱ���ܹ�һ���˳�Ӧ�ó���
 *
 */
public class BaseActivity extends FragmentActivity {

	public static List<BaseActivity> activities=new LinkedList<BaseActivity>();
	private FinishAllReceiver receiver;
	
	public static BaseActivity activity;//����һ����Ա����  ������һ���µİ���vitityʱ���ж��Ƿ���Ҫ�����µ�����ջ�ı��
	
	@Override
	protected void onResume() {
		super.onResume();
		activity=this;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��activity���������
		activities.add(this);
		
		//ע��㲥�����ߣ������ڽ�����ʱ��ȡ��ע��
		IntentFilter filter=new IntentFilter("com.oterman.receiver.finishAll");
		
		receiver = new FinishAllReceiver();
		registerReceiver(receiver, filter);
		
		init();//��ʼ��
		initView();// ��ʼ���ؼ�
		initActionBar();// ��ʼ��actionbar
	}
	
	protected void init() {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//ȡ��ע��㲥
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
		
		//�Ӽ������Ƴ�����
		activities.remove(this);
		finish();
	}
	/**
	 * �������е�activity
	 */
	public void finishAllActivity(){
		//���ڼ����ڱ�����ʱ�򣬲����Ƴ�,���Եñ���һ��
		List<BaseActivity> backUP=new LinkedList<>(activities);
		for (BaseActivity baseActivity : backUP) {
			baseActivity.finish();
			activities.remove(baseActivity);
		}
		
		//ɱ������
		Process.killProcess(Process.myPid());
		
	}

	/**
	 * �����������һ�ַ���  ����һ���㲥�����ߣ������յ��㲥��ʱ�򣬽���activity
	 */
	class FinishAllReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			
			finish();
		}
	}
	
	/**
	 * ��ʼ���ؼ�
	 */
	protected void initView() {

	}

	/**
	 * ��ʼ��actionbar
	 */
	protected void initActionBar() {
		
	}
}
