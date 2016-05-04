package com.oterman.njubbs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.oterman.njubbs.R;

/**
 * 加载更多视图
 * @author oterman
 *
 */
public class LoadMoreListView extends ListView implements OnScrollListener {
	private View mFooterView;

	private int mFooterHeight;
	OnLoadMoreListener onLoadMoreListener;
	
	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public LoadMoreListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public LoadMoreListView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		mFooterView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
		this.addFooterView(mFooterView);
		
		mFooterView.measure(0, 0);
		mFooterHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterHeight, 0, 0);//隐藏起来
		
		//设置滑动监听
		this.setOnScrollListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (getLastVisiblePosition()==getCount()-1) {//滑到底了
			this.mFooterView.setPadding(0, 0, 0, 0);//显示
			setSelection(getCount()-1);//显示位置
			//加载更多
			if(this.onLoadMoreListener!=null){
				onLoadMoreListener.onLoadingMore();
			}
		}else{
			this.mFooterView.setPadding(0, -mFooterHeight, 0, 0);//隐藏
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	public void OnLoaingMoreCompelete(){
		this.mFooterView.setPadding(0, -mFooterHeight, 0, 0);//隐藏
	}

	
	public interface OnLoadMoreListener{
		public void onLoadingMore();
	}

}
