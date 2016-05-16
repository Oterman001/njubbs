package com.oterman.njubbs.fragment;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oterman.njubbs.R;
import com.oterman.njubbs.fragment.secondary.TopAllFragment;
import com.oterman.njubbs.fragment.secondary.TopTenFragment;
import com.oterman.njubbs.utils.LogUtil;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 热帖界面 包括十大和各区热点
 * @author oterman
 *
 */
public class HotTopicFragment extends Fragment implements OnPageChangeListener {
	
	TopTenFragment topTenFragment;
	TopAllFragment topAllFragment;

	private View rootView;
	private ViewPager vpPager;
	private HotTopicAdapter adapter;
	private TabPageIndicator indicator;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		LogUtil.d("onActivityCreated");
		
		
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		LogUtil.d("onCreateView");
		rootView = View.inflate(getContext(), R.layout.fragment_hot_topic, null);
		vpPager = (ViewPager) rootView.findViewById(R.id.vp_pages);		
		
		adapter = new HotTopicAdapter(getChildFragmentManager());
		vpPager.setAdapter(adapter);
		
		indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
		indicator.setViewPager(vpPager);
		
		indicator.setOnPageChangeListener(this);
		vpPager.setCurrentItem(0);
		
		return rootView;
	}
	
	class HotTopicAdapter extends FragmentPagerAdapter{

		public HotTopicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0){
				if(topTenFragment==null){
					topTenFragment=new TopTenFragment();
				}
				
				return topTenFragment;
				
			}else{
				if(topAllFragment==null){
					topAllFragment=new TopAllFragment();
				}
				return topAllFragment;
			}
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return position==0?"十大":"各区热门";
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		
	}

	@Override
	public void onPageSelected(int position) {
		LogUtil.d("onPageSelected:"+position);
		if(position==0){
			if(topTenFragment!=null){
				topTenFragment.showViewFromServer();
			}
		}else{
			if(topAllFragment!=null){
				topAllFragment.showViewFromServer();
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

}
