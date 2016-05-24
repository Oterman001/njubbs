package com.oterman.njubbs.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.smiley.SelectFaceHelper;
import com.oterman.njubbs.smiley.SelectFaceHelper.OnFaceOprateListener;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.utils.UiUtils;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;
import com.oterman.njubbs.view.WaitDialog;
/**
 * ��վ����
 * @author oterman
 *
 */
public class MailNewActivity extends MyActionBarActivity implements
		OnClickListener {

	private EditText etTitle;
	private EditText etReceiver;
	private EditText etContent;
	private WaitDialog dialog;
	private ImageButton ibSmiley;
	private View addFaceToolView;
	private SelectFaceHelper mFaceHelper;
	private ImageButton ibPost;
	boolean isVisbilityFace=false;
	private MailInfo mailInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mail_new);
		
		//actionbar�ķ��ͼ�ͷ
		ibPost = (ImageButton) actionBarView.findViewById(R.id.btn_post_topic);
		ibPost.setVisibility(View.VISIBLE);
		
		ibPost.setOnClickListener(this);
			
		etTitle = (EditText) this.findViewById(R.id.et_titile);//����
		etReceiver=(EditText) this.findViewById(R.id.et_mailto);//�ռ���
		etContent = (EditText) this.findViewById(R.id.et_content);
		
		
		
		ibSmiley = (ImageButton) this.findViewById(R.id.iv_pic);
		ibSmiley.setOnClickListener(faceClick);
		
		addFaceToolView=this.findViewById(R.id.add_tool);
		
		//��ȡ������
		Intent intent = getIntent();
		String receiver=intent.getStringExtra("receiver");

		if(!TextUtils.isEmpty(receiver)){
			etReceiver.setText(receiver);
		}
		
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
		
		
	}

	// ���������
		public void hideInputManager(Context ct) {
			try {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) ct)
						.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				Log.e("", "hideInputManager Catch error,skip it!", e);
			}
		}
		
		
		//�������ļ����¼�
		OnFaceOprateListener mOnFaceOprateListener2 = new OnFaceOprateListener() {
			@Override
			public void onFaceSelected(SpannableString spanEmojiStr) {
				if (null != spanEmojiStr) {
					//�ڹ�괦�������
					String oriText=etContent.getText().toString();//ԭʼ����
					
					
					int index=Math.max(etContent.getSelectionStart(),0);//��ȡ��괦λ�ã�û�й�꣬����-1
					
					StringBuffer sb=new StringBuffer(oriText);
					sb.insert(index, spanEmojiStr);
					String string = sb.toString().replaceAll("\n", "<br>");
					
					Spanned spanned = Html.fromHtml(string);
					CharSequence text = SmileyParser.getInstance(getApplicationContext()).strToSmiley(spanned);
					etContent.setText(text);
					
					
					etContent.setSelection(index+spanEmojiStr.length());
//					etContent.append(spanEmojiStr);
				}
			}

			@Override
			public void onFaceDeleted() {
				int selection = etContent.getSelectionStart();
				String text = etContent.getText().toString();
				if (selection > 0) {
					String text2 = text.substring(selection - 1,selection);
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
		
		
	
	@Override
	protected String getBarTitle() {
		return "дվ����";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_post_topic:// �������
			// ��ȡ����
			String content = etContent.getText().toString().trim();
			String title=etTitle.getText().toString().trim();
			String receiver=etReceiver.getText().toString().trim();
					
			if (TextUtils.isEmpty(content)) {
				MyToast.toast("����������");
				return;
			}
			if (TextUtils.isEmpty(receiver)) {
				MyToast.toast("������������");
				return;
			}
			if (TextUtils.isEmpty(title)) {
				MyToast.toast("���������");
				return;
			}

			// �������߼�
			handleNewMail(content,title,receiver);
			// MyToast.toast("������"+board);

			break;
			
		default:
			break;
		}

	}
	private void handleNewMail( final String content, final String title, final String receiver) {
		dialog = new WaitDialog(this);
		dialog.setMessage("Ŭ�������С�����");
		dialog.show();
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			@Override
			public void run() {
				//�����߼�
				try {
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					RequestParams rp=new RequestParams("gbk");
					
					rp.addQueryStringParameter("pid", "0");
					rp.addQueryStringParameter("userid","");
					
					rp.addBodyParameter("signature", "1");
					rp.addBodyParameter("userid", receiver);
					rp.addBodyParameter("title", title);
					rp.addBodyParameter("text", content);
					
					//���cookie
					String cookie = BaseApplication.getCookie();
					if(cookie==null){//�Զ���½
						cookie=BaseApplication.autoLogin();
					}
					
					rp.addHeader("Cookie",cookie);
					
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST, Constants.REPLY_MAIL_URL, rp);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("��վ�ڽ����"+result);
					
					if(result.contains("�ż��Ѽĸ�")){//�ɹ�
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("���ųɹ���");
								
								etContent.setText("");
							}
						});
					}else {//����ʧ��
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("�Զ���½ʧ�ܣ����ֶ���¼");
								//��ת����½����
								Intent intent=new Intent(UiUtils.getContext(),LoginActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								UiUtils.getContext().startActivity(intent);
							}
						});
					}
					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (dialog != null) {
								dialog.dismiss();
							}
							MyToast.toast("����ʧ�ܣ�"+e.getMessage());
						}
					});
				}
				
			}
		});
		
	}

	/**
	 * ����
	 * 
	 * @param title
	 * @param content
	 */
	private void handleReplyMail2( final String content) {

		dialog = new WaitDialog(this);
		dialog.setMessage("Ŭ�������С�����");
		dialog.show();

		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			
			@Override
			public void run() {

				try {
					
					if(httpUtils==null){
						httpUtils=new HttpUtils();
					}
					
					//����������Ϊgbk
					RequestParams params = new RequestParams("gbk");
					
				
					String modifyUrl=Constants.getModifyReplyUrl();
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,modifyUrl, params);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("�޸Ļ��������" + result);
					
					if(result.contains("���ļ������")){
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("�޸ĳɹ���");
								//finish();
							}
						});
					}else{
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("�޸ĳɹ���");
//								Intent intent=new Intent(getApplicationContext(),BoardDetailActivity.class);
//								
//								intent.putExtra("boardUrl", boardUrl);
//								startActivity(intent);
								
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

	//��������飬�������б���
	View.OnClickListener faceClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (null == mFaceHelper) {
				mFaceHelper = new SelectFaceHelper(MailNewActivity.this, addFaceToolView);
				//�������ʱ�����ü���
				mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener2);
			}
			if (isVisbilityFace) {
				isVisbilityFace = false;
				addFaceToolView.setVisibility(View.GONE);
			} else {
				isVisbilityFace = true;
				addFaceToolView.setVisibility(View.VISIBLE);
				hideInputManager(MailNewActivity.this);//���������
			}
		}
	};

}
