package com.oterman.njubbs.activity.expore;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;

public class ColleageContentActivity extends MyActionBarActivity implements OnKeyListener{

	
	private WebView wvContent;

	@Override
	protected String getBarTitle() {
		String name=getIntent().getStringExtra("name");
		return name;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview);
		
		String url=getIntent().getStringExtra("url");
		
		wvContent = (WebView) this.findViewById(R.id.wv_content);
		
		WebSettings settings = wvContent.getSettings();
		
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);
		wvContent.loadUrl(url);
		
		wvContent.setOnKeyListener(this);
		
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		if(event.getAction()==KeyEvent.ACTION_DOWN){
			if(keyCode==KeyEvent.KEYCODE_BACK&&wvContent.canGoBack()){
				wvContent.goBack();
				return true;
			}
		}
		return false;
	}
}
