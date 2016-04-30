package com.oterman.njubbs.fragment.factory;

import java.util.HashMap;
import java.util.Map;

import com.oterman.njubbs.fragment.AboutMeFragment;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.fragment.BoardsFragment;
import com.oterman.njubbs.fragment.DiscoveryFragment;
import com.oterman.njubbs.fragment.TopTenFragment;

public class FragmentFactory {
	
	//缓存fragment
	public static Map<Integer, BaseFragment> map=new HashMap<>();
	
	public static BaseFragment creatFragment(int position) {
		BaseFragment fragment =map.get(position);
        if (fragment == null) {  //如果再集合中没有取出来 需要重新创建
            if (position == 0) {
            	fragment = new TopTenFragment();
            } else if (position == 1) {
                fragment = new BoardsFragment();
            } else if (position == 2) {
                fragment = new DiscoveryFragment();
            } else if (position == 3) {
                fragment = new AboutMeFragment();
            } 
            if (fragment != null) {
                map.put(position, fragment);// 把创建好的Fragment存放到集合中缓存起来
            }
        }
        return fragment;
	}

}
