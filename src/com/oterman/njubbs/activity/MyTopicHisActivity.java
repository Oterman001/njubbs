package com.oterman.njubbs.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.oterman.njubbs.R;
import com.oterman.njubbs.R.id;
import com.oterman.njubbs.R.layout;
import com.oterman.njubbs.fragment.secondary.MyPostTopicFragment;
import com.oterman.njubbs.fragment.secondary.MyReplyTopicFragment;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 发帖记录页面 包含我的发帖 和我的回帖
 */
public class MyTopicHisActivity extends MyActionBarActivity implements OnPageChangeListener {
	private ViewPager vpPager;
	private MyTopicHisAdapter adapter;
	private TabPageIndicator indicator;
	
	private MyPostTopicFragment postFragment;
	private MyReplyTopicFragment replyFragment;
	
	boolean isUpdated=false;
	
	@Override
	protected String getBarTitle() {
		return "帖子记录";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_topic_his);
		
		vpPager = (ViewPager) findViewById(R.id.vp_pages);
		
		adapter = new MyTopicHisAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapter);
		
		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(vpPager);
		
		indicator.setOnPageChangeListener(this);
		vpPager.setCurrentItem(0);
		
	}
	
	class MyTopicHisAdapter extends FragmentPagerAdapter{
		public MyTopicHisAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0){
				if(postFragment==null){
					postFragment=new MyPostTopicFragment();
				}
				return postFragment;
			}else{
				if(replyFragment==null){
					replyFragment=new MyReplyTopicFragment();
				}
				return replyFragment;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return position==0?"我的发帖":"我的回帖";
		}
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		//被选中
		if(position==1&&isUpdated==false){
			MyReplyTopicFragment fragment = (MyReplyTopicFragment) adapter.getItem(position);
			fragment.showViewFromServer();
			isUpdated=true;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}
	
	
}
