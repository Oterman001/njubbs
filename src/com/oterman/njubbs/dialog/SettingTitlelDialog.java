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
 * ���÷�������
 *
 */
public class SettingTitlelDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private EditText etContent;
	private TextView tvDesc;

	private Context context;
	private CheckBox cbCheck;
	
	public SettingTitlelDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_edit_tail, null);

		etContent = (EditText) view.findViewById(R.id.et_content);
		
		etContent.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		
		tvDesc=(TextView) view.findViewById(R.id.tv_desc);
		cbCheck = (CheckBox) view.findViewById(R.id.cb_check);
		
		tvDesc.setText("�������ʱ��������Ĭ������30�����ڵ����ݣ��������ı��ⲻ����Re��");
		etContent.setHint("����������");
		
		//��ȡ�����ֵ
		String bytitle_day=SPutils.getFromSP("bytitle_day");
		
		if(bytitle_day!=null&&!TextUtils.isEmpty(bytitle_day)){
			etContent.setText(bytitle_day);
		}else{
			etContent.setText("30");
		}
		
		//��ʼ��checkbox
		cbCheck.setVisibility(View.VISIBLE);
		
		String bytitle_re=SPutils.getFromSP("bytitle_re");
		if(bytitle_re!=null&&!TextUtils.isEmpty(bytitle_re)){
			if("yes".equals(bytitle_re)){//����re
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
		SPutils.saveToSP("bytitle_day", tail);
		
		if(cbCheck.isChecked()){//ѡ�� ����
			SPutils.saveToSP("bytitle_re", "yes");
		}else{
			SPutils.saveToSP("bytitle_re", "no");
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
