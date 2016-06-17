package com.oterman.njubbs.activity.mail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.oterman.njubbs.BaseApplication;
import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.MailContentProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.LogUtil;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;
import com.umeng.analytics.MobclickAgent;

/**
 * 版面详情
 * 
 */
public class MailContentActicity extends BaseActivity implements OnClickListener {
	
	View rootView;
	MailContentProtocol contentProtocol;
	private String contentUrl;
	private MailInfo mailInfo;
	private TextView tvTitle;
	private TextView tvAuthor;
	private TextView tvPostTime;
	private TextView tvContent;
	private ImageButton ibReply;
	private ImageButton ibDelete;
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected CharSequence getBarTitle() {
		return "信件详情";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ibReply = (ImageButton) actionBarView.findViewById(R.id.btn_mail_reply);
		ibReply.setVisibility(View.VISIBLE);
		
		ibDelete = (ImageButton) actionBarView.findViewById(R.id.btn_mail_delete);
		ibDelete.setVisibility(View.VISIBLE);
		
		ibReply.setOnClickListener(this);
		ibDelete.setOnClickListener(this);
		
	}
	
	@Override
	public View createSuccessView() {
		
		rootView = View.inflate(this, R.layout.activity_mail_content, null);
		
		tvTitle = (TextView) rootView.findViewById(R.id.tv_mail_title);
		tvAuthor = (TextView) rootView.findViewById(R.id.tv_mail_author);
		tvPostTime = (TextView) rootView.findViewById(R.id.tv_mail_posttime);
		tvContent = (TextView) rootView.findViewById(R.id.tv_mail_content);
		
		if(mailInfo!=null){
			tvTitle.setText(mailInfo.title);
			tvAuthor.setText("发信人："+mailInfo.author);
			tvPostTime.setText("时    间："+mailInfo.postTime);
			
			//超链接可点击
			tvContent.setAutoLinkMask(Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);
			tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
			tvContent.setMovementMethod(LinkMovementMethod.getInstance());// 设置超链接可以打开网页
			
			Spanned spanned = Html.fromHtml(mailInfo.content, 
					new URLImageParser(tvContent),
					new MyTagHandler(getApplicationContext()));
			
			SmileyParser smileyParser = SmileyParser.getInstance(getApplicationContext());
			
			tvContent.setText(smileyParser.strToSmiley(spanned));
			
		}
		
		return rootView;
	}
	
	public LoadingState loadDataFromServer() {
		
		Intent intent = getIntent();
		contentUrl = intent.getStringExtra("contentUrl");
		
		if(contentProtocol==null){
			contentProtocol = new MailContentProtocol();
		}
		String url=Constants.getMailContentUrl(contentUrl);
	    mailInfo = contentProtocol.loadFromServer(url,false,MailContentActicity.this);

		return mailInfo == null ? LoadingState.LOAD_FAILED
				: LoadingState.LOAD_SUCCESS;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_mail_reply:
			Intent intent=new Intent(getApplicationContext(),MailReplyActivity.class);
			
			intent.putExtra("mailInfo", mailInfo);
			
			startActivity(intent);
			break;

		case R.id.btn_mail_delete:
			AlertDialog.Builder builder=new AlertDialog.Builder(this);
			
			builder.setTitle("亲！");
			builder.setMessage("确定要删除吗？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//MyToast.toast("删除："+mailInfo.delUrl);
					final WaitDialog waitDialog=new WaitDialog(MailContentActicity.this);
					
					waitDialog.setMessage("正在删除...");
					waitDialog.show();
					
					ThreadManager.getInstance().createLongPool().execute(new Runnable() {
						HttpUtils httpUtils=null;
						@Override
						public void run() {
							try {
								if(httpUtils==null){
									httpUtils=new HttpUtils();
								}
								RequestParams rp=new RequestParams();
								String cookie=BaseApplication.getCookie();
								
								if(cookie==null){
									cookie=BaseApplication.autoLogin(MailContentActicity.this,true);
								}
								rp.addHeader("Cookie", cookie);
								String url=Constants.getMailDelUrl(mailInfo.delUrl);
								
								ResponseStream stream = httpUtils.sendSync(HttpMethod.GET, url,rp);
								
								String result = BaseApplication.StreamToStr(stream);
								LogUtil.d("删除站内结果："+result);
								
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										MyToast.toast("删除成功！");
										waitDialog.dismiss();
										
										setResult(111);
										finish();
									}
								});
								
							} catch (final Exception e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										MyToast.toast("删除失败！"+e.getMessage());
										waitDialog.dismiss();
									}
								});
							}
							
						}
					});
				}
			});
			
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
			
		default:
			break;
		}
	}


}
