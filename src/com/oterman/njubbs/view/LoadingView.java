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
 *  �Զ���view  ��չ��framelayout
 *  ���ݲ�ͬ��״̬����ʾ��ͬ ��view��
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
		// ������
		if (loadingView == null) {
			loadingView = createLoadingView();
			this.addView(loadingView, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		// ����ʧ��
		if (loadFailedView == null) {
			loadFailedView = createLoadFaildView();
			this.addView(loadFailedView, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		updateViewVisibilty();// ����״̬�Ĳ�ͬ�����¸���view�Ŀɼ���
	}
	

	// ����״̬�Ĳ�ͬ�����¸���view�Ŀɼ���
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

	// �ӷ�������ȡ���ݣ�����view��ʾ
	public  void showViewFromServer() {
		currentState = LoadingState.LOADING;
		// �¿��̣߳���ʾ�ӷ�������ȡ
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				SystemClock.sleep(500);
				LogUtil.i("1 ��ǰ�̣߳�"+Thread.currentThread().getName());
				// ��ȡ���
				final LoadingState result = loadDataFromServer();
				LogUtil.i("2.���ؽ����" + result);
				
				// ���ݷ������ļ�ֵ��� ������ҳ��
				UiUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						LogUtil.i("3.��ǰ�̣߳�"+Thread.currentThread().getName());
						currentState = result;
						updateViewVisibilty();
					}
				});
			}
		});
		
		updateViewVisibilty();

	}

	//��������ʧ����ͼ
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

	//���سɹ���ͼ
	private View createLoadingView() {
		
		return View.inflate(getContext(), R.layout.loadpage_loading, null);
	}

	/**
	 * �ӷ�������������  ��ȡ���ؽ��
	 */
	protected abstract LoadingState loadDataFromServer();

	/**
	 * �������سɹ�����ͼ
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
