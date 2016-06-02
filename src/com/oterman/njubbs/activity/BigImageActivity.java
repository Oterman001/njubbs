package com.oterman.njubbs.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.oterman.njubbs.R;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
@SuppressLint("NewApi")
public class BigImageActivity  extends MyActionBarActivity implements OnClickListener {

	private ImageView iv;
	private PhotoViewAttacher attacher;
	private ProgressBar pbBar;
	private WaitDialog dialog;
	private ImageButton ibDownload;
	
	private Bitmap bitmap;
	private String imgurl;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_big_image);
		
		ibDownload = (ImageButton) actionBarView.findViewById(R.id.btn_down_pic);
		ibDownload.setVisibility(View.VISIBLE);
		
		ibDownload.setOnClickListener(this);
		
		pbBar = (ProgressBar) this.findViewById(R.id.pb);
		dialog = new WaitDialog(this);
		dialog.setMessage("正在努力加载大图中....");
		dialog.show();
		
		iv = (ImageView) this.findViewById(R.id.iv_big_img);
		
		attacher = new PhotoViewAttacher(iv);
		
		Intent intent = getIntent();
		imgurl = intent.getStringExtra("imgurl");
		
		
        //显示图片的配置  
//        DisplayImageOptions options = new DisplayImageOptions.Builder()  
//                .showImageOnLoading(R.drawable.product_loading)  
//                .cacheInMemory(true)  
//                .cacheOnDisk(true)  
//                .bitmapConfig(Bitmap.Config.RGB_565)  
//                .build(); 
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		
//		imageLoader.displayImage(imgurl, iv);
		LogUtil.d("图片地址："+imgurl);
		imageLoader.loadImage(imgurl, new SimpleImageLoadingListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				
				
				//pbBar.setVisibility(View.INVISIBLE);
				dialog.dismiss();
				bitmap=loadedImage;
				iv.setImageBitmap(loadedImage);
				attacher.update();
			}
		});
		

	}
	
	@Override
	protected String getBarTitle() {
		return "图片";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_down_pic:
			
			//保存到本地
			if(bitmap!=null){
				handleDownloadPic();
			}
			
			break;

		default:
			break;
		}
		
	}

	/**
	 * 下载图片
	 */
	private void handleDownloadPic() {
		ThreadManager.getInstance().createShortPool().execute(new Runnable() {
			@Override
			public void run() {
				final String basePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/pic";
				String filename=imgurl.substring(imgurl.lastIndexOf("/")+1);
				
				File dir=new File(basePath);
				if(!dir.exists()){
					dir.mkdirs();
				}
				
				final File file=new File(dir,filename);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("成功下载到："+basePath+"/"+file.getName());
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							MyToast.toast("下载失败，请检查网络");
						}
					});
				}
				
			}
		});
	}


}
