package com.oterman.njubbs.fragment.factory;

import java.util.HashMap;
import java.util.Map;

import com.oterman.njubbs.fragment.AboutMeFragment;
import com.oterman.njubbs.fragment.BaseFragment;
import com.oterman.njubbs.fragment.BoardsFragment;
import com.oterman.njubbs.fragment.DiscoveryFragment;
import com.oterman.njubbs.fragment.TopTenFragment;

public class FragmentFactory {
	
	//����fragment
	public static Map<Integer, BaseFragment> map=new HashMap<>();
	
	public static BaseFragment creatFragment(int position) {
		BaseFragment fragment =map.get(position);
        if (fragment == null) {  //����ټ�����û��ȡ���� ��Ҫ���´���
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
                map.put(position, fragment);// �Ѵ����õ�Fragment��ŵ������л�������
            }
        }
        return fragment;
	}

}
