package com.oterman.njubbs.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.WaitDialog;

public class NewTopicActivity extends MyActionBarActivity implements
		OnClickListener {

	private Button btnSend;
	private EditText etTitle;
	private EditText etContent;
	private String board;
	private WaitDialog dialog;
	private String boardUrl;
	private Button btnChosePic;
	private ImageView ivPic;
	private String picturePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_topic);

		btnSend = (Button) this.findViewById(R.id.btn_send);
		btnChosePic = (Button) this.findViewById(R.id.btn_chose_pic);

		btnChosePic.setOnClickListener(this);
		etTitle = (EditText) this.findViewById(R.id.et_titile);

		etContent = (EditText) this.findViewById(R.id.et_content);
		
		ivPic = (ImageView) this.findViewById(R.id.iv_pic);

		btnSend.setOnClickListener(this);

		Intent intent = getIntent();

		board = intent.getStringExtra("board");
		boardUrl = intent.getStringExtra("boardUrl");
	}

	@Override
	protected String getBarTitle() {
		return "����";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send:// ������
			// ��ȡ����
			String title = etTitle.getText().toString();
			String content = etContent.getText().toString();

			if (TextUtils.isEmpty(title)) {
				MyToast.toast("���������");
				return;
			}

			if (TextUtils.isEmpty(content)) {
				MyToast.toast("����������");
				return;
			}

			// �������߼�
			handleNewTopic(title, content);

			// MyToast.toast("������"+board);

			break;
			
		case R.id.btn_chose_pic:
			MyToast.toast("�����ѡͼ");
			
			Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, 100);
			break;
		default:
			break;
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 if(requestCode == 100 && resultCode == RESULT_OK && null != data) {
		        Uri selectedImage = data.getData();
		        String[] filePathColumn = { MediaStore.Images.Media.DATA };
		 
		        Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
		        cursor.moveToFirst();
		 
		        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		        picturePath = cursor.getString(columnIndex);
		        cursor.close();
		        
		        LogUtil.d("ͼƬ·����"+picturePath);
		        
		        ImageLoader imageLoader = ImageLoader.getInstance();
		        
		        imageLoader.displayImage("file://"+picturePath, ivPic);
		        
		 }
		
	}

	/**
	 * �������߼�
	 * 
	 * @param title
	 * @param content
	 */
	private void handleNewTopic(final String title, final String content) {

		dialog = new WaitDialog(this);
		dialog.setMessage("Ŭ���ķ����С�����");
		dialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			
			@Override
			public void run() {

				try {
					//�ϴ�ͼƬ��
					if(picturePath!=null){
						uploadPic();
					}
					
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					
					//����������Ϊgbk
					RequestParams params = new RequestParams("gbk");
					
					//������������
					String title2=URLEncoder.encode(title, "gbk");
					params.addBodyParameter("title", title);
					
					params.addBodyParameter("pid", 0 + "");
					params.addBodyParameter("reid", 0 + "");
					params.addBodyParameter("signature", 1 + "");
					params.addBodyParameter("autocr", "on");
					
//					params.addBodyParameter(key, file)
//					params.addBodyParameter(key, file, fileName, mimeType, charset)
					
					String content2=URLEncoder.encode(content, "gbk");
					params.addBodyParameter("text", content);
				
					
					//���cookie
					String cookie = BaseApplication.cookie;
					if(cookie==null){//�Զ���½
						
						BaseApplication.autoLogin();
						cookie=BaseApplication.cookie;
						if(cookie!=null){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									MyToast.toast("�Զ���½�ɹ���");
								}
							});
						}
					}
					
					params.addHeader("Cookie", cookie);
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,Constants.getNewTopicUrl(board), params);
					
					String result = StreamToStr(stream);
					LogUtil.d("���������" + result);
					
					if(result.contains("�Ҵҹ���")){
						MyToast.toast("����ʧ�ܣ�");
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("�����ɹ���");
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

			//�ϴ�ͼƬ
			private void uploadPic() {
				String url=Constants.getUploadUrl();
				
				if(httpUtils==null){
					httpUtils=new HttpUtils();
				}
				try {
					
					File file=new File(picturePath);
					
					RequestParams rp=new RequestParams();
					
					String cookie = BaseApplication.cookie;
					if(cookie==null){//�Զ���½
						
						BaseApplication.autoLogin();
						cookie=BaseApplication.cookie;
						if(cookie!=null){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									MyToast.toast("�Զ���½�ɹ���");
								}
							});
						}
					}
					
					rp.addHeader("Cookie", cookie);
					
					rp.addBodyParameter("file", file,"image/jpeg");
					
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST, url, rp);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("�ϴ�ͼƬ�����"+result);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		});

	}

	private String StreamToStr(ResponseStream responseStream)
			throws UnsupportedEncodingException, IOException {
		InputStream is = responseStream.getBaseStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is, "gbk"));

		String line = null;
		StringBuffer sb = new StringBuffer();

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
}
