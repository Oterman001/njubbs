package com.oterman.njubbs.activity;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oterman.njubbs.R;
import com.oterman.njubbs.view.WaitDialog;
@SuppressLint("NewApi")
public class BigImageActivity  extends MyActionBarActivity {

	private ImageView iv;
	private PhotoViewAttacher attacher;
	private ProgressBar pbBar;
	private WaitDialog dialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_big_image);
		
		pbBar = (ProgressBar) this.findViewById(R.id.pb);
		dialog = new WaitDialog(this);
		dialog.setMessage("正在努力加载大图中....");
		dialog.show();
		
		iv = (ImageView) this.findViewById(R.id.iv_big_img);
		
		
		attacher = new PhotoViewAttacher(iv);
		
		Intent intent = getIntent();
		String imgurl = intent.getStringExtra("imgurl");
		
		
        //显示图片的配置  
//        DisplayImageOptions options = new DisplayImageOptions.Builder()  
//                .showImageOnLoading(R.drawable.product_loading)  
//                .cacheInMemory(true)  
//                .cacheOnDisk(true)  
//                .bitmapConfig(Bitmap.Config.RGB_565)  
//                .build(); 
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		
//		imageLoader.displayImage(imgurl, iv);
		
		imageLoader.loadImage(imgurl, new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				//pbBar.setVisibility(View.INVISIBLE);
				dialog.dismiss();
				iv.setImageBitmap(loadedImage);
				attacher.update();
			}
		});
		
	}
	
	@Override
	protected String getBarTitle() {
		return "图片";
	}


}
