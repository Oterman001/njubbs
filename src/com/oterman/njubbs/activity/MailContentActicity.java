package com.oterman.njubbs.activity;

import java.util.Random;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.bean.MailInfo;
import com.oterman.njubbs.protocol.MailContentProtocol;
import com.oterman.njubbs.protocol.MailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.view.LoadingView.LoadingState;

/**
 * ��������
 * 
 */
public class MailContentActicity extends BaseActivity {
	View rootView;
	MailContentProtocol contentProtocol;
	private String contentUrl;
	private MailInfo mailInfo;
	private TextView tvTitle;
	private TextView tvAuthor;
	private TextView tvPostTime;
	private TextView tvContent;
	@Override
	protected CharSequence getBarTitle() {
		return "�ż�����";
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
			tvAuthor.setText("�����ˣ�"+mailInfo.author);
			tvPostTime.setText("ʱ    �䣺"+mailInfo.postTime);
			tvContent.setText(mailInfo.content);
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


}
