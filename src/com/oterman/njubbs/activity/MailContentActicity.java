package com.oterman.njubbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.protocol.MailContentProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;

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
	@Override
	protected CharSequence getBarTitle() {
		return "信件详情";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ibReply = (ImageButton) actionBarView.findViewById(R.id.btn_mail_reply);
		
		ibReply.setVisibility(View.VISIBLE);
		
		ibReply.setOnClickListener(this);
		
		
	}
	
	@Override
	public View createSuccessView() {
		
		rootView = View.inflate(getApplicationContext(), R.layout.activity_mail_content, null);
		
		tvTitle = (TextView) rootView.findViewById(R.id.tv_mail_title);
		tvAuthor = (TextView) rootView.findViewById(R.id.tv_mail_author);
		tvPostTime = (TextView) rootView.findViewById(R.id.tv_mail_posttime);
		tvContent = (TextView) rootView.findViewById(R.id.tv_mail_content);
		
		if(mailInfo!=null){
			tvTitle.setText(mailInfo.title);
			tvAuthor.setText("发信人："+mailInfo.author);
			tvPostTime.setText("时    间："+mailInfo.postTime);
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
	    mailInfo = contentProtocol.loadFromServer(url,false);
	  

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

		default:
			break;
		}
	}


}
