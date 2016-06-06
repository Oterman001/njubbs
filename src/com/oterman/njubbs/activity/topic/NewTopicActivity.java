package com.oterman.njubbs.activity.topic;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.MyActionBarActivity;
import com.oterman.njubbs.activity.board.BoardDetailActivity;
import com.oterman.njubbs.dialog.ChosePicDialog;
import com.oterman.njubbs.dialog.ShowChosedPicDialog;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.smiley.SelectFaceHelper;
import com.oterman.njubbs.smiley.SelectFaceHelper.OnFaceOprateListener;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.NetUtils;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.ThreadManager;

public class NewTopicActivity extends MyActionBarActivity implements
		OnClickListener {

	private EditText etTitle;
	private EditText etContent;
	private String board;
	private WaitDialog dialog;
	private String boardUrl;
	private ImageButton ibSmiley;
	private View addFaceToolView;
	private SelectFaceHelper mFaceHelper;
	private ImageButton ibPost;
	boolean isVisbilityFace=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_new_topic);

		//btnSend = (Button) this.findViewById(R.id.btn_send);
		
		//actionbar的发送箭头
		ibPost = (ImageButton) actionBarView.findViewById(R.id.btn_post_topic);
		ibPost.setVisibility(View.VISIBLE);
		
		ibPost.setOnClickListener(this);
			
		etTitle = (EditText) this.findViewById(R.id.et_titile);

		etContent = (EditText) this.findViewById(R.id.et_content);
		
		tvTail = (TextView) this.findViewById(R.id.tv_tail);
		
		//初始化数据
		tvTail.setText(SPutils.getTailNoColor());
		
		ibSmiley = (ImageButton) this.findViewById(R.id.iv_pic);
		
		ibChosePic = (ImageButton) this.findViewById(R.id.iv_chose_pic);
		ibChosePic.setOnClickListener(this);
		
		ibSmiley.setOnClickListener(faceClick);
		
		addFaceToolView=this.findViewById(R.id.add_tool);

		//btnSend.setOnClickListener(this);
		
		etContent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isVisbilityFace = false;
				addFaceToolView.setVisibility(View.GONE);
				return false;
			}
		});	
		
		etTitle.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isVisbilityFace = false;
				addFaceToolView.setVisibility(View.GONE);
				return false;
			}
		});	

		Intent intent = getIntent();

		board = intent.getStringExtra("board");
		boardUrl = intent.getStringExtra("boardUrl");
	}

	//点击脸表情，调出所有表情
		View.OnClickListener faceClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(NewTopicActivity.this, addFaceToolView);
					//点击表情时，设置监听
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener2);
				}
				if (isVisbilityFace) {
					isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
					isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(NewTopicActivity.this);//隐藏软键盘
				}
			}
		};
		
		// 隐藏软键盘
		public void hideInputManager(Context ct) {
			try {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) ct)
						.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				Log.e("", "hideInputManager Catch error,skip it!", e);
			}
		}
		
		
		
		/*
		public static void showSoftKeyboard(View view) {
			((InputMethodManager) BaseApplication.context().getSystemService(
			Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
			InputMethodManager.SHOW_FORCED);
		}
		
		public static void toogleSoftKeyboard(View view) {
			((InputMethodManager) BaseApplication.context().getSystemService(
			Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
			InputMethodManager.HIDE_NOT_ALWAYS);
		}
		 */
		
		//表情点击的监听事件
		OnFaceOprateListener mOnFaceOprateListener2 = new OnFaceOprateListener() {
			@Override
			public void onFaceSelected(SpannableString spanEmojiStr) {
				if (null != spanEmojiStr) {
					etContent.append(spanEmojiStr);
				}
			}

			@Override
			public void onFaceDeleted() {
				int selection = etContent.getSelectionStart();
				String text = etContent.getText().toString();
				if (selection > 0) {
					String text2 = text.substring(selection - 1);
					if ("]".equals(text2)) {
						int start = text.lastIndexOf("[");
						int end = selection;
						etContent.getText().delete(start, end);
						return;
					}
					etContent.getText().delete(selection - 1, selection);
				}
			}

		};
		private TextView tvTail;
		private ImageButton ibChosePic;
		private ChosePicDialog picDialog;
		private ShowChosedPicDialog showChosedPicDialog;
		
		
	
	@Override
	protected String getBarTitle() {
		return "发帖";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_post_topic:// 处理发帖
			// 获取数据
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();
			if (TextUtils.isEmpty(title)) {
				MyToast.toast("请输入标题");
				return;
			}
			if (TextUtils.isEmpty(content)) {
				MyToast.toast("请输入内容");
				return;
			}
			// 处理发帖逻辑
			handleNewTopic(title, content);

			break;
		case R.id.iv_chose_pic:
			picDialog = new ChosePicDialog(this);
			picDialog.show();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode==RESULT_OK){
			if(requestCode==100){//图库选择
				//content://media/external/images/media/660109
		        Uri selectedImage = data.getData();
		        String[] filePathColumn = { MediaStore.Images.Media.DATA };
		 
		        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
		        String picturePath=null;
		        if(cursor!=null){
		        	cursor.moveToFirst();
			        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					
			         picturePath = cursor.getString(columnIndex);
					
		        }else{
		        	 picturePath = selectedImage.getPath();
		        	//根据path处理文件
		        }
		        

				//Uri uri = data.getData();
				//LogUtil.d("选择图片："+uri);
				if(showChosedPicDialog!=null){
					showChosedPicDialog.dismiss();
				}
				
				try {
					AlertDialog.Builder builder=new AlertDialog.Builder(this);
					ImageView iv=new ImageView(this);
					final Bitmap bitmap=parseUriToBm(picturePath);
					//将bitmap存到本地
					
					iv.setImageBitmap(bitmap);
					iv.setPadding(6, 6, 6, 6);
					builder.setTitle("选择图片");
					builder.setView(iv);
					builder.setNegativeButton("重新选择",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_PICK,
									android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//							Intent intent=new Intent();
//							intent.setType("image/*");
//							intent.setAction(Intent.ACTION_GET_CONTENT);
							
							startActivityForResult(intent, 100);
						}
					});
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							MyToast.toast("正在上传。。");
							handleUploadPic2(bitmap);
							
							}
						});
//					builder.setCancelable(false);
					AlertDialog diglog2 = builder.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	protected void handleUploadPic2(final Bitmap bitmap) {
		final WaitDialog waitDialog=new WaitDialog(NewTopicActivity.this);
		waitDialog.setMessage("正在努力上传。。");
		waitDialog.show();
		
		//将bitmap缓存到本地
		String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/photo/";
		File dirFile=new File(dirPath);
		if(!dirFile.exists())dirFile.mkdirs();
		//njubbskdsadjkfa.jpg
		Date date=new Date(System.currentTimeMillis());
		SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmss");
		String date2 = sdf.format(date);
		
		String filename="nju_bbs"+date2+".jpg";
		saveBitmapToLocal(bitmap,filename);
		
		File file=new File(dirFile, filename);
		
		NetUtils.uploadFile2(this, waitDialog,file);
		
	}
	
	//处理上传图片
	protected void handleUploadPic(final Bitmap bitmap) {
		final WaitDialog waitDialog=new WaitDialog(NewTopicActivity.this);
		waitDialog.setMessage("正在努力上传。。");
		waitDialog.show();
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils=new HttpUtils();
				httpUtils.configTimeout(100000);
//				httpUtils.configResponseTextCharset("gbk");
				RequestParams rp=new RequestParams();
				rp.setContentType("multipart/form-data");
				
				//将bitmap缓存到本地
				String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/photo/";
				File dirFile=new File(dirPath);
				if(!dirFile.exists())dirFile.mkdirs();
				
				String filename="njubbs_upload"+SystemClock.elapsedRealtime()+".jpg";
				saveBitmapToLocal(bitmap,filename);
				
				File file=new File(dirFile, filename);
				
				rp.addBodyParameter("up", file);
				
				rp.addHeader("Accept-Encoding", "identity");
				String cookie=BaseApplication.getCookie();
				if(cookie==null){
					cookie=BaseApplication.autoLogin(getApplicationContext(), true);
				}
				
				rp.addHeader("Cookie", cookie);
				rp.addBodyParameter("exp", "xixi");
				rp.addBodyParameter("ptext", "text");
				rp.addBodyParameter("board", "Pictures");
				
				try {
					String url=Constants.getUploadUrl();
					
//					httpUtils.send(HttpMethod.POST, url, rp, new RequestCallBack<String>() {
//
//						@Override
//						public void onSuccess(ResponseInfo<String> responseInfo) {
//							String result=responseInfo.result;
//							LogUtil.d("jiegou:"+result);
//							
//						}
//						@Override
//						public void onFailure(HttpException error, String msg) {
//							System.err.println(msg);
//							error.printStackTrace();
//						}
//					});
					
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST, url, rp);
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("上传结果："+result);
					
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(waitDialog!=null){
								waitDialog.dismiss();
							}
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(waitDialog!=null){
								waitDialog.dismiss();
								MyToast.toast("上传失败");
							}
						}
					});
				}
			}
		});
	}
	
	//将bitmap缓存到本地
	protected void saveBitmapToLocal(Bitmap bitmap, String filename) {
		String dirPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/njubbs/photo/";
		File dirFile=new File(dirPath);
		if(!dirFile.exists())
			dirFile.mkdirs();
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(dirPath+filename);
			bitmap.compress(CompressFormat.JPEG, 90, fos);
			
			fos.flush();
			fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private Bitmap parseUriToBm(String url) {
		// file:///storage/emulated/0/DCIM/Camera/IMG_20160604_065624.jpg
		//content://media/external/images/media/523090
		
//		String url = uri.toString();
//		LogUtil.d("uri:"+uri);
//		//url = url.substring(url.indexOf("/") + 2);
//		url=url.substring(url.indexOf("a")+1);
		
		
		System.out.println("选中图片地址：" + url);

		// 解析图片时需要使用到的参数都封装在这个对象里了
		Options opt = new Options();
		// 不为像素申请内存，只获取图片宽高
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, opt);
		// 拿到图片宽高
		int imageWidth = opt.outWidth;
		int imageHeight = opt.outHeight;

		Display dp = getWindowManager()
				.getDefaultDisplay();
		// 拿到屏幕宽高
		int screenWidth = dp.getWidth() / 2;
		int screenHeight = dp.getHeight() / 2;

		// 计算缩放比例
		int scale = 1;
		int scaleWidth = imageWidth / screenWidth;
		int scaleHeight = imageHeight / screenHeight;
		if (scaleWidth >= scaleHeight && scaleWidth >= 1) {
			scale = scaleWidth;
		} else if (scaleWidth < scaleHeight && scaleHeight >= 1) {
			scale = scaleHeight;
		}

		// 设置缩放比例
		opt.inSampleSize = scale;
		opt.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(url, opt);
		return bitmap;
	}

	private void handleNewTopic(final String title, final String content) {

		dialog = new WaitDialog(this);
		dialog.setMessage("努力的发帖中。。。");
		dialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			
			@Override
			public void run() {

				try {
					
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					
					//服务器编码为gbk
					RequestParams params = new RequestParams("gbk");
					
					//处理中文问题
					String title2=URLEncoder.encode(title, "gbk");
					params.addBodyParameter("title", title);
					
					params.addBodyParameter("pid", 0 + "");
					params.addBodyParameter("reid", 0 + "");
					params.addBodyParameter("signature", 1 + "");
					params.addBodyParameter("autocr", "on");
					
					
					String content2=content+SPutils.getTail();
					params.addBodyParameter("text", content2);
				
					
					//添加cookie
					String cookie = BaseApplication.getCookie();
					if(cookie==null){//自动登陆
						
						cookie=BaseApplication.autoLogin(NewTopicActivity.this,true);
						
						if(cookie!=null){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									MyToast.toast("自动登陆成功！");
								}
							});
						}
					}
					
					params.addHeader("Cookie", cookie);
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,Constants.getNewTopicUrl(board), params);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("发帖结果：" + result);
					
					if(result.contains("匆匆过客")){
						//登陆失败，手动登录
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("自动登陆失败，请登录！");
								// 跳转到登陆页面
//								Intent intent = new Intent(
//										NewTopicActivity.this,
//										LoginActivity.class);
//								startActivity(intent);
							}
						});
						
					}else if(result.contains("发文间隔过密")){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("发文间隔过密，稍后重试！");
//								Intent intent=new Intent(getApplicationContext(),BoardDetailActivity.class);
//								intent.putExtra("boardUrl", boardUrl);
//								startActivity(intent);
								//finish();
							}
						});
						
					} else{//成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("发帖成功！");
								Intent intent=new Intent(getApplicationContext(),BoardDetailActivity.class);
								
								intent.putExtra("boardUrl", boardUrl);
								startActivity(intent);
								
								finish();
							}
						});
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});

	}


}

		
	

