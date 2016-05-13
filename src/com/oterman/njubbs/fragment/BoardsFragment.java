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
import com.oterman.njubbs.fragment.secondary.FavoriteFragment;
import com.oterman.njubbs.fragment.secondary.HotBoardsFragment;
import com.oterman.njubbs.fragment.secondary.TopAllFragment;
import com.oterman.njubbs.fragment.secondary.TopTenFragment;
import com.oterman.njubbs.utils.LogUtil;
import com.viewpagerindicator.TabPageIndicator;

/**
 * 热帖界面 包括十大和各区热点
 * @author oterman
 *
 */
public class BoardsFragment extends Fragment implements OnPageChangeListener {
	
	HotBoardsFragment hotBoardsFragment;
	FavoriteFragment favoriteFragment;

	private View rootView;
	private ViewPager vpPager;
	private BoardsAdapter adapter;
	private TabPageIndicator indicator;

	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}
	
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		rootView = View.inflate(getContext(), R.layout.fragment_boards, null);
		vpPager = (ViewPager) rootView.findViewById(R.id.vp_pages);		
		
		//fragment中嵌套fragment  必须使用getChildFragmentManager
		adapter = new BoardsAdapter(getChildFragmentManager());
		vpPager.setAdapter(adapter);
		
		indicator = (TabPageIndicator) rootView.findViewById(R.id.indicator);
		indicator.setViewPager(vpPager);
		
		indicator.setOnPageChangeListener(this);
		vpPager.setCurrentItem(0);

		
		return rootView;
	}
	
	class BoardsAdapter extends FragmentPagerAdapter{

		public BoardsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0){
				if(favoriteFragment==null){
					favoriteFragment=new FavoriteFragment();
				}
				//favoriteFragment.showViewFromServer();

				return favoriteFragment;
				
			}else{
				if(hotBoardsFragment==null){
					hotBoardsFragment=new HotBoardsFragment();
				}
				return hotBoardsFragment;
			}
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return position==0?"我的收藏":"热门版块";
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
			if(favoriteFragment==null){
				favoriteFragment=new FavoriteFragment();
			}
			favoriteFragment.showViewFromServer();
		}else{
			if(hotBoardsFragment==null){
				hotBoardsFragment=new HotBoardsFragment();
			}
			hotBoardsFragment.showViewFromServer();
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		
	}

}
