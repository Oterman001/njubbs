package com.oterman.njubbs.activity.expore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;

public class ColleageContentActivity extends MyActionBarActivity implements OnKeyListener{

	
	private WebView wvContent;
//	private ProgressDialog dialog; 
	private ProgressBar pb; 

	@Override
	protected String getBarTitle() {
		String name=getIntent().getStringExtra("name");
		return name;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//showDialog(0);
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview);
		
		String url=getIntent().getStringExtra("url");
		
		wvContent = (WebView) this.findViewById(R.id.wv_content);
		
		pb = (ProgressBar) this.findViewById(R.id.pb_progress);
		pb.setVisibility(View.VISIBLE);
		WebSettings settings = wvContent.getSettings();
		
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setUseWideViewPort(true);
		
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setLoadWithOverviewMode(true);
		
		wvContent.loadUrl(url);
		
		wvContent.setOnKeyListener(this);
		
		wvContent.setWebViewClient(new MyWebViewClient());
		
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
	
//    @Override  
//    protected Dialog onCreateDialog(int id) {  
//        //ʵ�����������Ի���  
//        dialog=new ProgressDialog(this);  
//        /*//���Բ���ʾ���� 
//        dialog.setTitle("���ڼ��أ����Ժ�");*/  
//        dialog.setIndeterminate(true);  
//        dialog.setMessage("���ڼ��أ����Ժ�");  
//        dialog.setCancelable(true);  
//        return dialog;  
//    }  
    
	class MyWebViewClient extends WebViewClient{
        @Override  
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
        @Override  
        public void onPageFinished(WebView view, String url) {  
        	//  dialog.dismiss();  
        	 pb.setVisibility(View.INVISIBLE);
        }  
        @Override  
        public void onReceivedError(WebView view, int errorCode,  
                String description, String failingUrl) {  
            super.onReceivedError(view, errorCode, description, failingUrl);  
//            dialog.dismiss();  
            pb.setVisibility(View.INVISIBLE);
        } 
	}
}
