package com.oterman.njubbs.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_topic);

		btnSend = (Button) this.findViewById(R.id.btn_send);

		etTitle = (EditText) this.findViewById(R.id.et_titile);

		etContent = (EditText) this.findViewById(R.id.et_content);

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

		default:
			break;
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
			@Override
			public void run() {

				try {
					HttpUtils httpUtils = new HttpUtils();
					//����������Ϊgbk
					RequestParams params = new RequestParams("gbk");
					
					//������������
					String title2=URLEncoder.encode(title, "gbk");
					params.addBodyParameter("title", title);
					
					params.addBodyParameter("pid", 0 + "");
					params.addBodyParameter("reid", 0 + "");
					params.addBodyParameter("signature", 1 + "");
					params.addBodyParameter("autocr", "on");
					
					String content2=URLEncoder.encode(content, "gbk");
					params.addBodyParameter("text", content);
				
//					String str="title=%B2%E2%CA%D4&pid=0&reid=0&signature=1&autocr=on&text=%D6%D0%CE%C4%B7%A2%CC%FB%A3%AChaha%0D%0A";
					//String str="title="+title2+"&pid=0&reid=0&signature=1&autocr=on&text="+content2;
					//HttpEntity bodyEntity=new StringEntity(str, "gbk");
					//params.setBodyEntity(bodyEntity);
					
					//���cookie
					String cookie = BaseApplication.cookie;
					if(cookie==null){//�Զ���½
						
//						runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								//MyToast.toast("�����Զ���½");
//							}
//						});
					
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
