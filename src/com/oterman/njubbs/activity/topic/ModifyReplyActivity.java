package com.oterman.njubbs.activity.topic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.oterman.njubbs.activity.MyActionBarActivity;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.smiley.SelectFaceHelper;
import com.oterman.njubbs.smiley.SelectFaceHelper.OnFaceOprateListener;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;

public class ModifyReplyActivity extends MyActionBarActivity implements
		OnClickListener {

	private EditText etTitle;
	private EditText etContent;
	private String board;
	private WaitDialog dialog;
	private ImageButton ibSmiley;
	private View addFaceToolView;
	private SelectFaceHelper mFaceHelper;
	private ImageButton ibPost;
	boolean isVisbilityFace=false;
	private TopicDetailInfo topicDetailInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_new_topic);

		//btnSend = (Button) this.findViewById(R.id.btn_send);
		
		//actionbar的发送箭头
		ibPost = (ImageButton) actionBarView.findViewById(R.id.btn_post_topic);
		ibPost.setVisibility(View.VISIBLE);
		
		ibPost.setOnClickListener(this);
			
		etTitle = (EditText) this.findViewById(R.id.et_titile);//标题

		etContent = (EditText) this.findViewById(R.id.et_content);
		
		ibSmiley = (ImageButton) this.findViewById(R.id.iv_pic);
		
		ibSmiley.setOnClickListener(faceClick);
		
		addFaceToolView=this.findViewById(R.id.add_tool);

		
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
		
		//回去要修改回帖的数据
		Intent intent = getIntent();
		topicDetailInfo = (TopicDetailInfo) intent.getSerializableExtra("topicDetailInfo");
		
		//初始化
		etTitle.setText("Re:"+topicDetailInfo.title);
		etTitle.setEnabled(false);
		
		SmileyParser sp=SmileyParser.getInstance(getApplicationContext());
		String content=topicDetailInfo.content.replaceAll("<br>","\n");
		etContent.setText(sp.strToSmiley(content));
		
	}

	//点击脸表情，调出所有表情
		View.OnClickListener faceClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null == mFaceHelper) {
					mFaceHelper = new SelectFaceHelper(ModifyReplyActivity.this, addFaceToolView);
					//点击表情时，设置监听
					mFaceHelper.setFaceOpreateListener(mOnFaceOprateListener2);
				}
				if (isVisbilityFace) {
					isVisbilityFace = false;
					addFaceToolView.setVisibility(View.GONE);
				} else {
					isVisbilityFace = true;
					addFaceToolView.setVisibility(View.VISIBLE);
					hideInputManager(ModifyReplyActivity.this);//隐藏软键盘
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
		
		
	
	@Override
	protected String getBarTitle() {
		return "修改回帖";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_post_topic:// 处理发帖
			// 获取数据
			String content = etContent.getText().toString();

			if (TextUtils.isEmpty(content)) {
				MyToast.toast("请输入内容");
				return;
			}

			// 处理发帖逻辑
			handleModifyReply(content);
			// MyToast.toast("发帖："+board);

			break;
			
		default:
			break;
		}

	}

	/**
	 * 处理
	 * 
	 * @param title
	 * @param content
	 */
	private void handleModifyReply( final String content) {

		dialog = new WaitDialog(this);
		dialog.setMessage("努力修改回帖中。。。");
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
					
					//bbspst?board=WorldFootball&amp;file=M.1462286742.A
					
					String replyUrl=topicDetailInfo.replyUrl;
					String file=replyUrl.substring(replyUrl.lastIndexOf("=")+1);
					
					Pattern p=Pattern.compile(".*?board=(.*?)\\&.*file=(.*?)");
					Matcher matcher = p.matcher(replyUrl);
					String board=null;
					if(matcher.find()){
						board=matcher.group(1);
					}
					
					
					params.addBodyParameter("type", 1 + "");
					params.addBodyParameter("file", file);
					params.addBodyParameter("board", board);
					/*
	 发信人: 1Q84 (天天移动的肾形石), 信区: WorldFootball 
	  标 题: Re: 20年一晃而过，写在拜仁被淘汰前。 
	  发信站: 南京大学小百合站 (Tue May 3 22:45:42 2016) 
					 */
					
					StringBuffer header=new StringBuffer();
					header.append("发信人: "+topicDetailInfo.author).append(", 信区: ").append(board);
					header.append("\n").append("标 题: Re: ").append(topicDetailInfo.title);
					Date date=new Date(System.currentTimeMillis());
					
					SimpleDateFormat format=new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.ENGLISH);
					String dateStr=format.format(date);
					
					header.append("\n").append("发信站: 南京大学小百合站 ("+dateStr+")\n\n ");
					
					content.replaceAll("<br>", "\n");
					header.append(content).append("\n\n--  ");
					
					params.addBodyParameter("text", header.toString());
				
					
					//添加cookie
					String cookie = BaseApplication.getCookie();
					if(cookie==null){//自动登陆
						cookie=BaseApplication.autoLogin(ModifyReplyActivity.this,true);
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

}
