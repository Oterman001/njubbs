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

	public ChosePicDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public ChosePicDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public ChosePicDialog(Context context) {
		super(context);
		init(context);

	}
	
	
	private void init(Context context2) {
		this.context=context2;
		String[] strs = new String[] { "打开图库", "打开相机" };
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
					MyToast.toast("打开相册");
					handleChoseFromGallery();
				} else {
					dismiss();
					MyToast.toast("打开相机");
					handleChoseFromCamera();
				}
			}
	
		});
		
		setContentView(lv);
		this.setTitle("请选择");
		
	}

	private void handleChoseFromCamera() {
		
	}

	/*
	 * 打开图库
	 */
	private void handleChoseFromGallery() {
		Intent intent=new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		if(context instanceof  FragmentActivity){
			((FragmentActivity)context).startActivityForResult(intent, 100);
		}
	}

}
