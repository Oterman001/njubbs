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
 * ����<img>��ǩ����ͼƬ
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
        //����Ĭ�ϵ�ͼƬ
        Bitmap defaultBitmap = BitmapFactory.decodeResource(UiUtils.getResource(), R.drawable.product_loading);
        drawable.bitmap=defaultBitmap;
        drawable.setBounds(0, 0, defaultBitmap.getWidth(), defaultBitmap.getHeight());  
        
        
        
        DisplayImageOptions options;  
        options = new DisplayImageOptions.Builder()  
	        // .showImageOnLoading(R.drawable.ic_launcher) //����ͼƬ�������ڼ���ʾ��ͼƬ  
	        // .showImageForEmptyUri(R.drawable.ic_launcher)//����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ  
	        //.showImageOnFail(R.drawable.ic_launcher)  //����ͼƬ����/��������д���ʱ����ʾ��ͼƬ
	        .cacheInMemory(true)//�������ص�ͼƬ�Ƿ񻺴����ڴ���  
	        .cacheOnDisc(true)//�������ص�ͼƬ�Ƿ񻺴���SD����  
	       // .considerExifParams(true)  //�Ƿ���JPEGͼ��EXIF��������ת����ת��
	        //.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//����ͼƬ����εı��뷽ʽ��ʾ  
	        //.bitmapConfig(Bitmap.Config.RGB_565)//����ͼƬ�Ľ�������//  
	        //.delayBeforeLoading(int delayInMillis)//int delayInMillisΪ�����õ�����ǰ���ӳ�ʱ��
	        //����ͼƬ���뻺��ǰ����bitmap��������  
	        //.preProcessor(BitmapProcessor preProcessor)  
	        //.resetViewBeforeLoading(true)//����ͼƬ������ǰ�Ƿ����ã���λ  
	        //.displayer(new RoundedBitmapDisplayer(20))//�Ƿ�����ΪԲ�ǣ�����Ϊ����  
	       // .displayer(new FadeInBitmapDisplayer(100))//�Ƿ�ͼƬ���غú���Ķ���ʱ��  
	        .build();//�������  
        
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
            	LogUtil.d("����ͼƬ�ˡ�����"+imageUri);
            	drawable.bitmap = loadedImage;  
            	drawable.setBounds(0, 0, loadedImage.getWidth(), loadedImage.getHeight());  
                mTextView.invalidate();  
                mTextView.setText(mTextView.getText()); // ���ͼ���ص� 
                
            }  
        });  
        
        

        return drawable;  
    }  
} 