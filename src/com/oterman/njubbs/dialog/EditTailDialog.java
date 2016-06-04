package com.oterman.njubbs.dialog;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;

/**
 * ����Сβ��
 *
 */
public class EditTailDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private EditText etTail;

	private Context context;
	
	public EditTailDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_edit_tail, null);

		etTail = (EditText) view.findViewById(R.id.et_tail);
		etTail.setHint(Build.MODEL+"");

		//��ȡ�����ֵ
		String tail=SPutils.getFromSP("tail");
		
		if(tail!=null&&!TextUtils.isEmpty(tail)){
			etTail.setText(tail);
		}
		
		builder.setView(view);

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String str = etTail.getText().toString();
				try {
//					if (TextUtils.isEmpty(str)) {
//
//						Field field = dialog.getClass().getSuperclass()
//								.getDeclaredField("mShowing");
//						field.setAccessible(true);
//						field.set(dialog, false); // false -���ܹر�
//						
//						MyToast.toast("��û����");
//						return;
//					}else{
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true); // true���رնԻ���
						// ������Ӻ���
						handleSaveTail();
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}


		});

		builder.setNegativeButton("ȡ��", null);
		builder.setTitle("�༭Сβ��");
		dialog = builder.create();

	}
	
	private void handleSaveTail() {
		//��������
		String tail = etTail.getText().toString().trim();
		SPutils.saveToSP("tail", tail);
		MyToast.toast("����ɹ�");
		
	}


	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
