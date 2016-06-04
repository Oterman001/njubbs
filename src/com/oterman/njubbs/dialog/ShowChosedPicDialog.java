package com.oterman.njubbs.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.MyToast;

public class ShowChosedPicDialog extends Dialog implements OnClickListener {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	private Context context;
	private View rootView;
	private ImageView ivPic;
	private Button btnRechose;
	private Button btnChoseOk;
	
	public ShowChosedPicDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	private void init(Context context2) {
		
	}

	public ShowChosedPicDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public ShowChosedPicDialog(Context context) {
		super(context);
		init(context);
	}

	public ShowChosedPicDialog(Context context, Uri imgeUri) {
		super(context);
		init(context);
		
		this.context = context;
		builder = new AlertDialog.Builder(context);
		rootView = View.inflate(context, R.layout.dialog_show_chosed_pic, null);

		ivPic = (ImageView) rootView.findViewById(R.id.iv_pic);
		btnRechose = (Button) rootView.findViewById(R.id.btn_rechose);
		btnChoseOk = (Button) rootView.findViewById(R.id.btn_chose_ok);

		btnRechose.setOnClickListener(this);
		btnChoseOk.setOnClickListener(this);

		parseUriToBm(imgeUri);
		
		builder.setTitle("ѡ��ͼƬ");

		builder.setView(rootView);
		dialog = builder.create();

	}

	/**
	 * �������
	 * 
	 * @param imgeUri
	 */
	private void parseUriToBm(Uri uri) {
		// file:///storage/emulated/0/DCIM/Camera/IMG_20160604_065624.jpg
		String url = uri.toString();
		url = url.substring(url.indexOf("/") + 2);

		System.out.println("ѡ��ͼƬ��ַ��" + url);

		// ����ͼƬʱ��Ҫʹ�õ��Ĳ�������װ�������������
		Options opt = new Options();
		// ��Ϊ���������ڴ棬ֻ��ȡͼƬ���
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, opt);
		// �õ�ͼƬ���
		int imageWidth = opt.outWidth;
		int imageHeight = opt.outHeight;

		Display dp = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		// �õ���Ļ���
		int screenWidth = dp.getWidth() / 2;
		int screenHeight = dp.getHeight() / 2;

		// �������ű���
		int scale = 1;
		int scaleWidth = imageWidth / screenWidth;
		int scaleHeight = imageHeight / screenHeight;
		if (scaleWidth >= scaleHeight && scaleWidth >= 1) {
			scale = scaleWidth;
		} else if (scaleWidth < scaleHeight && scaleHeight >= 1) {
			scale = scaleHeight;
		}

		// �������ű���
		opt.inSampleSize = scale;
		opt.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(url, opt);

		ivPic.setImageBitmap(bitmap);
	}

	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_chose_ok:
			// �ϴ�
			MyToast.toast("ѡ��ɹ��������ϴ�");
			dialog.cancel();
			break;
		case R.id.btn_rechose:
			this.dismiss();
			Intent intent=new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			
			if(context instanceof  FragmentActivity){
				((FragmentActivity)context).startActivityForResult(intent, 100);
			}
			
			break;

		default:
			break;
		}
	}

}
