package com.oterman.njubbs.holders;

import java.security.acl.Owner;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.oterman.njubbs.R;
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
	
	public void setListener(MyOnclickListener listener) {
		this.listener = listener;
	}

	public OptionsDialogHolder(Context context,String ownerId) {
		rootView = View.inflate(UiUtils.getContext(), R.layout.item_long_click, null);
		this.context=context;
		this.ownerId=ownerId;
		initViews();
	}

	private void initViews() {
		
		btnAuthorDetail = (Button) rootView.findViewById(R.id.btn_author_detail);
		btnMail = (Button) rootView.findViewById(R.id.btn_mail_to_author);
		btnDelete = (Button) rootView.findViewById(R.id.btn_delete_topci);
		btnModify = (Button) rootView.findViewById(R.id.btn_modify_topic);
		
		btnAuthorDetail.setOnClickListener(this);
		btnMail.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnModify.setOnClickListener(this);
		
		updateVisibility();
		
	}

	private void updateVisibility() {
		String logedId = SPutils.getFromSP("id");//登陆作者的id
		
		//比较当前id与登陆作者id的区别
		if(this.ownerId.equals(logedId)){//查看帖子的作者和当前登录的是同一人
			this.btnDelete.setVisibility(View.VISIBLE);
			this.btnModify.setVisibility(View.VISIBLE);
		}else{//不是同一人
			this.btnDelete.setVisibility(View.GONE);
			this.btnModify.setVisibility(View.GONE);
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

		default:
			break;
		}
	}
	
	public interface MyOnclickListener{
		
		public void onDelete();
		public void OnQueryAuthurDetail();
		public void OnModify();
		public void OnMailTo();
	}

}
