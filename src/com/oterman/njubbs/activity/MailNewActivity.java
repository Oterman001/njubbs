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
 * 发站内信
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
		
		//actionbar的发送箭头
		ibPost = (ImageButton) actionBarView.findViewById(R.id.btn_post_topic);
		ibPost.setVisibility(View.VISIBLE);
		
		ibPost.setOnClickListener(this);
			
		etTitle = (EditText) this.findViewById(R.id.et_titile);//标题
		etReceiver=(EditText) this.findViewById(R.id.et_mailto);//收件人
		etContent = (EditText) this.findViewById(R.id.et_content);
		
		
		
		ibSmiley = (ImageButton) this.findViewById(R.id.iv_pic);
		ibSmiley.setOnClickListener(faceClick);
		
		addFaceToolView=this.findViewById(R.id.add_tool);
		
		//获取接收人
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

	// 隐藏软键盘
		public void hideInputManager(Context ct) {
			try {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity) ct)
						.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {
				Log.e("", "hideInputManager Catch error,skip it!", e);
			}
		}
		
		
		//表情点击的监听事件
		OnFaceOprateListener mOnFaceOprateListener2 = new OnFaceOprateListener() {
			@Override
			public void onFaceSelected(SpannableString spanEmojiStr) {
				if (null != spanEmojiStr) {
					//在光标处插入表情
					String oriText=etContent.getText().toString();//原始文字
					
					
					int index=Math.max(etContent.getSelectionStart(),0);//获取光标处位置，没有光标，返回-1
					
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
		return "写站内信";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_post_topic:// 处理回信
			// 获取数据
			String content = etContent.getText().toString().trim();
			String title=etTitle.getText().toString().trim();
			String receiver=etReceiver.getText().toString().trim();
					
			if (TextUtils.isEmpty(content)) {
				MyToast.toast("请输入内容");
				return;
			}
			if (TextUtils.isEmpty(receiver)) {
				MyToast.toast("请输入收信人");
				return;
			}
			if (TextUtils.isEmpty(title)) {
				MyToast.toast("请输入标题");
				return;
			}

			// 处理发帖逻辑
			handleNewMail(content,title,receiver);
			// MyToast.toast("发帖："+board);

			break;
			
		default:
			break;
		}

	}
	private void handleNewMail( final String content, final String title, final String receiver) {
		dialog = new WaitDialog(this);
		dialog.setMessage("努力发信中。。。");
		dialog.show();
		
		ThreadManager.getInstance().createLongPool().execute(new Runnable() {
			HttpUtils httpUtils = null;
			@Override
			public void run() {
				//回帖逻辑
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
					
					//添加cookie
					String cookie = BaseApplication.getCookie();
					if(cookie==null){//自动登陆
						cookie=BaseApplication.autoLogin();
					}
					
					rp.addHeader("Cookie",cookie);
					
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST, Constants.REPLY_MAIL_URL, rp);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("发站内结果："+result);
					
					if(result.contains("信件已寄给")){//成功
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("发信成功！");
								
								etContent.setText("");
							}
						});
					}else {//回信失败
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("自动登陆失败，请手动登录");
								//跳转到登陆界面
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
							MyToast.toast("发信失败！"+e.getMessage());
						}
					});
				}
				
			}
		});
		
	}

	/**
	 * 处理
	 * 
	 * @param title
	 * @param content
	 */
	private void handleReplyMail2( final String content) {

		dialog = new WaitDialog(this);
		dialog.setMessage("努力回信中。。。");
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
					
				
					String modifyUrl=Constants.getModifyReplyUrl();
					ResponseStream stream = httpUtils.sendSync(HttpMethod.POST,modifyUrl, params);
					
					String result = BaseApplication.StreamToStr(stream);
					LogUtil.d("修改回帖结果：" + result);
					
					if(result.contains("发文间隔过密")){
						
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								MyToast.toast("修改成功！");
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
								MyToast.toast("修改成功！");
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

	//点击脸表情，调出所有表情
	View.OnClickListener faceClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (null == mFaceHelper) {
				mFaceHelper = new SelectFaceHelper(MailNewActivity.this, addFaceToolView);
				//点击表情时，设置监听
				mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener2);
			}
			if (isVisbilityFace) {
				isVisbilityFace = false;
				addFaceToolView.setVisibility(View.GONE);
			} else {
				isVisbilityFace = true;
				addFaceToolView.setVisibility(View.VISIBLE);
				hideInputManager(MailNewActivity.this);//隐藏软键盘
			}
		}
	};

}
