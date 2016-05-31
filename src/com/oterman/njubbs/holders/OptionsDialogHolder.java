package com.oterman.njubbs.holders;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.oterman.njubbs.R;
import com.oterman.njubbs.activity.expore.MyTopicActivity;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;
import com.oterman.njubbs.utils.UiUtils;

public class OptionsDialogHolder implements OnClickListener {


	View rootView;
	Context context;
	private Button btnAuthorDetail;
	private Button btnMail;
	private Button btnDelete;
	private Button btnModify;
	private String ownerId;
	
	MyOnclickListener listener;
	private Button btnReply;
	
	private boolean isInsideTopic;
	private Button btnTopicHis;
	
	public void setListener(MyOnclickListener listener) {
		this.listener = listener;
	}

	public OptionsDialogHolder(Context context,String ownerId,boolean isInsideTopic) {
		rootView = View.inflate(UiUtils.getContext(), R.layout.item_long_click, null);
		this.context=context;
		this.ownerId=ownerId;
		this.isInsideTopic=isInsideTopic;
		initViews();
	}

	private void initViews() {
		
		btnAuthorDetail = (Button) rootView.findViewById(R.id.btn_author_detail);
		
		btnMail = (Button) rootView.findViewById(R.id.btn_mail_to_author);//վ��
		btnDelete = (Button) rootView.findViewById(R.id.btn_delete_topci);//ɾ��
		btnModify = (Button) rootView.findViewById(R.id.btn_modify_topic);//�޸�
		btnReply = (Button) rootView.findViewById(R.id.btn_reply_floor);//����
		
		btnTopicHis = (Button) rootView.findViewById(R.id.btn_topic_his);

		btnAuthorDetail.setOnClickListener(this);
		btnMail.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnModify.setOnClickListener(this);
		btnReply.setOnClickListener(this);
		
		btnTopicHis.setOnClickListener(this);
		
		updateVisibility();
		
	}

	private void updateVisibility() {
		String logedId = SPutils.getFromSP("id");//��½���ߵ�id
		
		//�Ƚϵ�ǰid���½����id������
		if(this.ownerId.equals(logedId)){//�鿴���ӵ����ߺ͵�ǰ��¼����ͬһ��
			this.btnDelete.setVisibility(View.VISIBLE);
			this.btnModify.setVisibility(View.VISIBLE);
		}else{//����ͬһ��
			this.btnDelete.setVisibility(View.GONE);
			this.btnModify.setVisibility(View.GONE);
		}
		
		if(isInsideTopic){//�����ڲ����
			this.btnReply.setVisibility(View.VISIBLE);
		}else{
			this.btnReply.setVisibility(View.GONE);
		}
		
	}

	public View getRootView() {
		return rootView;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_author_detail:
			if(listener!=null){
				listener.OnQueryAuthurDetail();
			}
			
			break;
		case R.id.btn_mail_to_author:
			if(listener!=null){
				listener.OnMailTo();
			}
			break;
		case R.id.btn_delete_topci:
			if(listener!=null){
				listener.onDelete();
			}
			break;
		case R.id.btn_modify_topic:
			if(listener!=null){
				listener.OnModify();
			}
			break;
		case R.id.btn_reply_floor:
			if(listener!=null){
				listener.onReplyFloor();
			}
			break;
			
		case R.id.btn_topic_his:
//			MyToast.toast("�����");
			if(listener!=null){
				listener.onQueryTopicHis();
			}
			break;

		default:
			break;
		}
	}
	
	public interface MyOnclickListener{
		
		public void onDelete();
		public void OnQueryAuthurDetail();
		public void OnModify();
		public void OnMailTo();
		public void onReplyFloor();
		
		public void onQueryTopicHis();
		
	}

}
