package com.oterman.njubbs.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.UiUtils;

/**
 * 处理<img>标签加载图片
 * @author oterman
 *
 */
public class URLImageParser implements Html.ImageGetter {  
    TextView mTextView;  
    
    BitmapUtils bu=new BitmapUtils(UiUtils.getContext());
  
    public URLImageParser(TextView textView) {  
        this.mTextView = textView;  
    }  
    
	public Drawable getDrawable(String source) {  
		
		final URLDrawable drawable =new URLDrawable();
		
        ImageLoader imageLoader = ImageLoader.getInstance();
        //加载默认的图片
        Bitmap defaultBitmap = BitmapFactory.decodeResource(UiUtils.getResource(), R.drawable.product_loading);
        drawable.bitmap=defaultBitmap;
        drawable.setBounds(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());  
        
        
        
        DisplayImageOptions options;  
        options = new DisplayImageOptions.Builder()  
	        // .showImageOnLoading(R.drawable.ic_launcher) //设置图片在下载期间显示的图片  
	        // .showImageForEmptyUri(R.drawable.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片  
	        //.showImageOnFail(R.drawable.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
	        .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
	        .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
	       // .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
	        //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示  
	        //.bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//  
	        //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
	        //设置图片加入缓存前，对bitmap进行设置  
	        //.preProcessor(BitmapProcessor preProcessor)  
	        //.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位  
	        //.displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少  
	       // .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间  
	        .build();//构建完成  
        
        imageLoader.loadImage(source,options,new SimpleImageLoadingListener() {  
        	@Override
        	public void onLoadingCancelled(String imageUri, View view) {
        		super.onLoadingCancelled(imageUri, view);
        	}
        	
        	@Override
        	public void onLoadingStarted(String imageUri, View view) {
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
        
        

        return drawable;  
    }  
} 