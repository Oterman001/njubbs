package com.oterman.njubbs.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.oterman.njubbs.R;
import com.oterman.njubbs.dialog.EditTailDialog;
import com.oterman.njubbs.dialog.SettingByAuthorDialog;
import com.oterman.njubbs.dialog.SettingTitlelDialog;
import com.oterman.njubbs.utils.MyToast;
@SuppressLint("NewApi")
public class SettingActivity  extends MyActionBarActivity implements OnClickListener {


	private LinearLayout llTail;
	private LinearLayout llTitleSearch;
	private LinearLayout llAuthorSearch;
	private LinearLayout llAuthorTips;
	private LinearLayout llAboutme;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setting);
		
		llTail = (LinearLayout) this.findViewById(R.id.ll_post_tail);
		llTitleSearch = (LinearLayout) this.findViewById(R.id.ll_title_search);
		llAuthorSearch = (LinearLayout) this.findViewById(R.id.ll_author_search);
		llAuthorTips= (LinearLayout) this.findViewById(R.id.ll_author_tips);
		llAboutme = (LinearLayout) this.findViewById(R.id.ll_about_me);

		
		llTail.setOnClickListener(this);
		llTitleSearch.setOnClickListener(this);
		llAuthorTips.setOnClickListener(this);
		llAboutme.setOnClickListener(this);
		llAuthorSearch.setOnClickListener(this);
	}
	
	@Override
	protected String getBarTitle() {
		return "����";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_post_tail:
			EditTailDialog dialog=new EditTailDialog(this);
			dialog.show();
//			MyToast.toast("Сβ��");
			break;
		case R.id.ll_author_search:
			SettingByAuthorDialog authorDialog=new SettingByAuthorDialog(this);
			authorDialog.show();
			break;
		case R.id.ll_title_search:
			SettingTitlelDialog titleDialog=new SettingTitlelDialog(this);
			titleDialog.show();
			break;
		case R.id.ll_about_me:
			MyToast.toast("�Ҿ����ң���һ�����̻�!");
			break;
		case R.id.ll_author_tips:
			//ʹ�ü���
			AlertDialog.Builder builder=new Builder(this);
			
			builder.setTitle("С����");
			builder.setMessage("1.��ʮ���б�ҳ�棬���������о�ϲ!\n2.�ڰ��������б�ҳ�棬������ϲ����!\n3.�����������б�ҳ�棬�������ྪϲ��\n4.ĳЩҳ����Ҫ�ֶ�����ˢ��Ŷ��");
			builder.setPositiveButton("ȷ��", null);
			
			builder.show();
			
			break;

		default:
			break;
		}
		
	}



}
