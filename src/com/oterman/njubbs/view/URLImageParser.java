package com.oterman.njubbs.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.UiUtils;

public class URLImageParser implements Html.ImageGetter {  
    TextView mTextView;  
    
    BitmapUtils bu=new BitmapUtils(UiUtils.getContext());
  
    public URLImageParser(TextView textView) {  
        this.mTextView = textView;  
    }  
    
	public Drawable getDrawable(String source) {  
		final URLDrawable drawable =new URLDrawable();
//		bu.getBitmapFromMemCache(null, null);
//		bu.display(null, null);
		
        ImageLoader imageLoader = ImageLoader.getInstance();
        
        
        Bitmap defaultBitmap = BitmapFactory.decodeResource(UiUtils.getResource(), R.drawable.product_loading);
        
        drawable.bitmap=defaultBitmap;
        drawable.setBounds(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());  
       // mTextView.invalidate();  
      //  mTextView.setText(mTextView.getText()); // 解决图文重叠
        
        imageLoader.loadImage(source,new SimpleImageLoadingListener() {  
        	@Override
        	public void onLoadingCancelled(String imageUri, View view) {
        		// TODO Auto-generated method stub
        		super.onLoadingCancelled(imageUri, view);
        	}
        	
        	@Override
        	public void onLoadingStarted(String imageUri, View view) {
        		// TODO Auto-generated method stub
        		super.onLoadingStarted(imageUri, view);
        	}
        	
            @Override  
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {  
            	
            	LogUtil.d("加载图片了。。。"+imageUri);
            	drawable.bitmap = loadedImage;  
            	drawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());  
                mTextView.invalidate();  
                mTextView.setText(mTextView.getText()); // 解决图文重叠 
                
            }  
        });  
        
        
        
//        imageLoader.loadImage(source, new ImageLoadingListener() {
//			
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//                drawable= UiUtils.getResource().getDrawable(R.drawable.moren);  
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
//                mTextView.invalidate();  
//                mTextView.setText(mTextView.getText()); // 解决图文重叠  
//			}
//			
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//                drawable= UiUtils.getResource().getDrawable(R.drawable.shibai);  
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
//                mTextView.invalidate();  
//                mTextView.setText(mTextView.getText()); // 解决图文重叠  
//			}
//			
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap loadedImage) {
//				URLDrawable  urlDrawable=null;
//				if(drawable instanceof URLDrawable){
//					urlDrawable=(URLDrawable) drawable;
//				}else{
//					urlDrawable=new URLDrawable();
//				}
//				
//				urlDrawable.bitmap = loadedImage;  
//                drawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());  
//                
//                drawable=urlDrawable;
//                
//                mTextView.invalidate();  
//                mTextView.setText(mTextView.getText()); // 解决图文重叠  
//                
//			}
//			
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				
//			}
//		});
        

        return drawable;  
    }  
} 