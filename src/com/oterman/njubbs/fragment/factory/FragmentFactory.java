package com.oterman.njubbs.fragment.factory;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

import com.oterman.njubbs.fragment.AboutMeFragment;
import com.oterman.njubbs.fragment.BoardsFragment;
import com.oterman.njubbs.fragment.DiscoveryFragment;
import com.oterman.njubbs.fragment.HotTopicFragment;

public class FragmentFactory {
	
	//缓存fragment
	public static Map<Integer, Fragment> map=new HashMap<>();
	
	public static Fragment creatFragment(int position) {
		Fragment fragment =map.get(position);
        if (fragment == null) {  //如果再集合中没有取出来 需要重新创建
            if (position == 0) {
            	fragment = new HotTopicFragment();
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
