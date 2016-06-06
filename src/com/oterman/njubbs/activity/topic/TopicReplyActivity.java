package com.oterman.njubbs.activity.topic;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.BaseActivity;
import com.oterman.njubbs.activity.board.BoardDetailActivity;
import com.oterman.njubbs.bean.TopicDetailInfo;
import com.oterman.njubbs.bean.TopicInfo;
import com.oterman.njubbs.dialog.WaitDialog;
import com.oterman.njubbs.protocol.TopicReDetailProtocol;
import com.oterman.njubbs.utils.Constants;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SmileyParser;
import com.oterman.njubbs.utils.ThreadManager;
import com.oterman.njubbs.view.LoadingView.LoadingState;
import com.oterman.njubbs.view.MyTagHandler;
import com.oterman.njubbs.view.URLImageParser;
/**
 * ����ĳ����������ͼ
 */

@SuppressLint("NewApi")
public class TopicReplyActivity  extends BaseActivity implements OnClickListener {
	
	private TopicInfo originTopicInfo;
	private TopicDetailInfo topicDetailInfo;
	
	private TextView tvAuthor;
	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvPubtime;
	private Button btnReadall;
	
	@Override
	public void initViews() {
		originTopicInfo = (TopicInfo) getIntent().getSerializableExtra("topicInfo");
		tvBarTitle.setText(originTopicInfo.board + "(�������)");

		// ��actionbar��ӵ���¼� �������뵽��Ӧ�İ���
		tvBarTitle.setClickable(true);
		tvBarTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						BoardDetailActivity.class);
				intent.putExtra("boardUrl", originTopicInfo.boardUrl);
				startActivity(intent);
				// ������
				finish();
			}
		});
		
	}

	@Override
	public LoadingState loadDataFromServer() {
		TopicReDetailProtocol protocol=new TopicReDetailProtocol();
		String url=Constants.getTopicReplyUrl(originTopicInfo.contentUrl);
		
		topicDetailInfo = protocol.loadDataFromServer(url);
		
		return topicDetailInfo==null? LoadingState.LOAD_FAILED:LoadingState.LOAD_SUCCESS;
	}

	@Override
	public View createSuccessView() {
		
		View rootView=View.inflate(this, R.layout.activity_topic_reply, null);
		
		tvAuthor = (TextView) rootView.findViewById(R.id.tv_topic_re_author);
		tvTitle = (TextView) rootView.findViewById(R.id.tv_topic_re_title);
		
		tvContent = (TextView) rootView.findViewById(R.id.tv_topic_re_content);
		tvPubtime = (TextView) rootView.findViewById(R.id.tv_topic_re_pubtime);
		
		btnReadall = (Button) rootView.findViewById(R.id.btn_read_all);
		
		btnReadall.setOnClickListener(this);
		
		tvAuthor.setText(originTopicInfo.author);
		tvTitle.setText(originTopicInfo.title);
		
//		tvContent.setText(topicDetailInfo.content);
		//�����ӿɵ��
		tvContent.setAutoLinkMask(Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);

		tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());// ���ÿɹ���
		tvContent.setMovementMethod(LinkMovementMethod.getInstance());// ���ó����ӿ��Դ���ҳ

		Spanned spanned = Html.fromHtml(topicDetailInfo.content, new URLImageParser(tvContent),
				new MyTagHandler(getApplicationContext()));
		SmileyParser sp = SmileyParser.getInstance(getApplicationContext());
		tvContent.setText(sp.strToSmiley(spanned));
		tvContent.invalidate();
		
		
		tvPubtime.setText(topicDetailInfo.pubTime);
		
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_read_all:
//			MyToast.toast(topicDetailInfo.rootUrl);
			final String url="http://bbs.nju.edu.cn/"+topicDetailInfo.rootUrl;
			final WaitDialog dialog=new WaitDialog(this);
			
			dialog.setMessage("���ڻ�ȡ");
			
			dialog.show();
			
			ThreadManager.getInstance().createLongPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						Document doc = Jsoup.connect(url).get();
						
						String html = doc.html().replaceAll("\n", "");
						
						int start = html.indexOf("url=")+4;
						int end=html.indexOf("A\">")+1;
						String url = html.substring(start, end);
						url=url.replaceAll("&amp;", "&");
//						url="http://bbs.nju.edu.cn/"+url;
						originTopicInfo.contentUrl=url;
						
						originTopicInfo.title=originTopicInfo.title.replaceAll("Re:", "");
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								//��ת����������
								Intent intent=new Intent(getApplicationContext(),TopicDetailActivity.class);
								intent.putExtra("topicInfo", originTopicInfo);
								startActivity(intent);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			
			break;
	
		default:
			break;
		}
		
	}


}
