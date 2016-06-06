package com.oterman.njubbs.dialog;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;

/**
 * ��������������
 *
 */
public class SettingByAuthorDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private EditText etContent;
	private TextView tvDesc;

	private Context context;
	private CheckBox cbCheck;
	
	public SettingByAuthorDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_edit_tail, null);

		etContent = (EditText) view.findViewById(R.id.et_content);
		
		etContent.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		
		tvDesc=(TextView) view.findViewById(R.id.tv_desc);
		cbCheck = (CheckBox) view.findViewById(R.id.cb_check);
		
		tvDesc.setText("�������ʱ��������Ĭ������999�����ڵ����ݣ��������ı��ⲻ����Re��");
		etContent.setHint("����������");
		
		//��ȡ�����ֵ
		String byauthor_day=SPutils.getFromSP("byauthor_day");
		
		if(byauthor_day!=null&&!TextUtils.isEmpty(byauthor_day)){
			etContent.setText(byauthor_day);
		}else{
			etContent.setText("999");
		}
		
		//��ʼ��checkbox
		cbCheck.setVisibility(View.VISIBLE);
		
		String byauthor_re=SPutils.getFromSP("byauthor_re");
		if(byauthor_re!=null&&!TextUtils.isEmpty(byauthor_re)){
			if("yes".equals(byauthor_re)){//����re
				cbCheck.setChecked(true);
			}else{
				cbCheck.setChecked(false);
			}
		}else{//û������  Ĭ�ϲ�����
			cbCheck.setChecked(false);
		}
		
		builder.setView(view);

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String str = etContent.getText().toString();
				try {
					if (TextUtils.isEmpty(str)||!TextUtils.isDigitsOnly(str)) {

						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false); // false -���ܹر�
						
						MyToast.toast("��Ҫ����һ������Ŷ");
						return;
					}else{
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true); // true���رնԻ���
						// ������Ӻ���
						handleSaveData();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.setTitle("����������");
		dialog = builder.create();
	}
	
	private void handleSaveData() {
		//��������
		String tail = etContent.getText().toString().trim();
		SPutils.saveToSP("byauthor_day", tail);
		
		if(cbCheck.isChecked()){//ѡ�� ����
			SPutils.saveToSP("byauthor_re", "yes");
		}else{
			SPutils.saveToSP("byauthor_re", "no");
		}
		MyToast.toast("����ɹ�");
	}


	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
