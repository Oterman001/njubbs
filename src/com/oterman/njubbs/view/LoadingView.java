package com.oterman.njubbs.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;

/**
 *  自定义view  扩展了framelayout
 *  根据不同的状态，显示不同 的view；
 * 
 */

public abstract class LoadingView extends FrameLayout{

	public static final int STATE_LOADING = 1;
	public static final int STATE_FAILED = 2;
	public static final int STATE_SUCCESS = 3;

	public LoadingState currentState = LoadingState.LOADING;

	private View loadingView;
	private View loadFailedView;
	private View loadSuccessView;
	private Button btnFailed;
	
	
	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoadingView(Context context) {
		super(context);
		init();
	}
	private void init() {
		// 加载中
		if (loadingView == null) {
			loadingView = createLoadingView();
			this.addView(loadingView, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		// 加载失败
		if (loadFailedView == null) {
			loadFailedView = createLoadFaildView();
			this.addView(loadFailedView, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		updateViewVisibilty();// 根据状态的不同，更新各个view的可见性
	}
	

	// 根据状态的不同，更新各个view的可见性
	private void updateViewVisibilty() {
		
		loadingView.setVisibility(currentState == LoadingState.LOADING ? View.VISIBLE: View.INVISIBLE);

		loadFailedView.setVisibility(currentState == LoadingState.LOAD_FAILED ? View.VISIBLE: View.INVISIBLE);

		if (currentState == LoadingState.LOAD_SUCCESS && loadSuccessView == null) {
			loadSuccessView = createSuccessView();
			this.addView(loadSuccessView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		
		if (loadSuccessView != null) {
			loadSuccessView.setVisibility(currentState == LoadingState.LOAD_SUCCESS ? View.VISIBLE: View.INVISIBLE);
		}

	}

	// 从服务器获取数据，更新view显示
	public  void showViewFromServer() {
		currentState = LoadingState.LOADING;
		// 新开线程，表示从服务器获取
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(500);
				LogUtil.i("1 当前线程："+Thread.currentThread().getName());
				// 获取结果
				final LoadingState result = loadDataFromServer();
				LogUtil.i("2.加载结果：" + result);
				
				// 根据服务器的价值结果 更新主页面
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LogUtil.i("3.当前线程："+Thread.currentThread().getName());
						currentState = result;
						updateViewVisibilty();
					}
				});
			}
		});
		
		updateViewVisibilty();

	}

	//创建加载失败视图
	private View createLoadFaildView() {

		View view = View.inflate(getContext(), R.layout.loadpage_error, null);
		btnFailed = (Button) view.findViewById(R.id.btn_load_failed);
		btnFailed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showViewFromServer();
			}
		});
		return view;
	}

	//加载成功视图
	private View createLoadingView() {
		
		return View.inflate(getContext(), R.layout.loadpage_loading, null);
	}

	/**
	 * 从服务器加载数据  获取加载结果
	 */
	protected abstract LoadingState loadDataFromServer();

	/**
	 * 创建加载成功的视图
	 */
	protected abstract View createSuccessView();
	
	public  enum LoadingState{
		LOADING(1),LOAD_FAILED(2), LOAD_SUCCESS(3);
		
		int  value;
		LoadingState(int value) {
			this.value=value;
		}
	}
}
