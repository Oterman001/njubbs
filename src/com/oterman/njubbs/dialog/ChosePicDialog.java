package com.oterman.njubbs.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.oterman.njubbs.utils.MyToast;

public class ChosePicDialog extends Dialog {

	private Context context;
	private ListView lv;
	private int requestCode;
	
	public ChosePicDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public ChosePicDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public ChosePicDialog(int requestCode,Context context) {
		super(context);
		init(context);
		this.requestCode=requestCode;
	}

	private void init(Context context2) {
		this.context = context2;
		String[] strs = new String[] { "��ͼ��", "�����" };
		lv = new ListView(context);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
				android.R.layout.simple_list_item_1, strs);

		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					cancel();
					MyToast.toast("�����");
					handleChoseFromGallery(requestCode);
				} else {
					dismiss();
					MyToast.toast("�����");
					handleChoseFromCamera();
				}
			}

		});

		setContentView(lv);
		this.setTitle("��ѡ��");

	}

	private void handleChoseFromCamera() {

	}

	/*
	 * ��ͼ��
	 */
	private void handleChoseFromGallery(int requestCode) {
//		Intent intent = new Intent();
//		intent.setType("image/*");
//		intent.setAction(Intent.ACTION_GET_CONTENT);
		
		Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		if (context instanceof FragmentActivity) {
			((FragmentActivity) context).startActivityForResult(intent, requestCode);
		}
		
	}

}
