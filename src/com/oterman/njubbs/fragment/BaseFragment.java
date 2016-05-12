package com.oterman.njubbs.fragment;

import java.util.Random;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oterman.njubbs.view.LoadingView;
import com.oterman.njubbs.view.LoadingView.LoadingState;

public abstract class BaseFragment extends Fragment {
	
	LoadingView loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(loadingView==null){
			loadingView=new LoadingView(getContext()){

				@Override
				protected LoadingState loadDataFromServer() {
					return BaseFragment.this.loadDataFromServer();
				}

				@Override
				protected View createSuccessView() {
					return BaseFragment.this.createSuccessView();
				}
				
			};
		}
		
		return loadingView;
	}

	public abstract View createSuccessView();

	public LoadingState loadDataFromServer() {
		Random r=new Random();
		
		int result=r.nextInt(2)+2;
		return result==2?LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}
	
	public void showViewFromServer(){
		if(loadingView!=null){
			loadingView.showViewFromServer();
		}
	}


}
