package com.oterman.njubbs.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.protocol.TopicDetailProtocol;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.LoadingView;
import com.oterman.njubbs.view.LoadingView.LoadingState;

@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {

	LoadingView loadingView;
	ActionBar actionBar;
	protected TextView tvBarTitle;
	protected View actionBarView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 更改状态栏的颜色
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = this.getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources()
					.getColor(R.color.green));
		}
		
		initActionBar();
		
		initViews();
		
		if (loadingView == null) {
			loadingView = new LoadingView(getApplicationContext()) {
				@Override
				protected LoadingState loadDataFromServer() {
					return BaseActivity.this.loadDataFromServer();
				}

				@Override
				protected View createSuccessView() {
					return BaseActivity.this.createSuccessView();
				}
			};
		}

		loadingView.showViewFromServer();
		
		setContentView(loadingView);
	}

	private void initActionBar() {
		// 自定义actionbar
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		actionBarView = View.inflate(getApplicationContext(),
				R.layout.actionbar_custom_backtitle, null);

		View back = actionBarView.findViewById(R.id.btn_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		tvBarTitle = (TextView) actionBarView.findViewById(R.id.tv_actionbar_title);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(actionBarView, params);

		tvBarTitle.setText(getBarTitle());
		tvBarTitle.setTextSize(22);
	}
	
	
	/**
	 * actionbar的名称
	 * @return
	 */
	protected CharSequence getBarTitle() {
		return "小百合";
	}

	/**
	 * 初始化view
	 */
	public void initViews() {
		
	}
	

	/**
	 * 加载数据成功后 创建视图
	 * @return
	 */
	public abstract View createSuccessView();

	/*
	 * 从服务器中加载数据
	 */
	public abstract LoadingState loadDataFromServer();


}
