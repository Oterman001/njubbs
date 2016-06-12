package com.oterman.njubbs.dialog;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;

/**
 * վ����������
 *
 */
public class MailRemindDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	private Context context;
	private CheckBox cbMailToAt;
	private CheckBox cbMailToLouzhu;
	
	public MailRemindDialog(Context context) {
		this.context=context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_mail_remind, null);
		
		cbMailToAt = (CheckBox) view.findViewById(R.id.cb_mailto_at);
		cbMailToLouzhu = (CheckBox) view.findViewById(R.id.cb_mailto_louzhu);
		
		//��ʼ��  Ĭ��Ϊ����
		String mailToAt = SPutils.getFromSP("mailto_at");
		String mailToLouzhu=SPutils.getFromSP("mailto_louzhu");
		
		if("no".equals(mailToAt)){//������
			cbMailToAt.setChecked(false);
		}else{
			cbMailToAt.setChecked(true);
		}
		
		if("no".equals(mailToLouzhu)){//������
			cbMailToLouzhu.setChecked(false);
		}else{
			cbMailToLouzhu.setChecked(true);
		}
		
		builder.setView(view);

		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//��ȡ��ǰ״̬����������
				if(cbMailToAt.isChecked()){//ѡ��
					SPutils.saveToSP("mailto_at", "yes");
				}else{
					SPutils.saveToSP("mailto_at", "no");
				}
				
				if(cbMailToLouzhu.isChecked()){//ѡ��
					SPutils.saveToSP("mailto_louzhu", "yes");
				}else{
					SPutils.saveToSP("mailto_louzhu", "no");
				}

			}
		});
		builder.setNegativeButton("ȡ��", null);
		builder.setTitle("����վ��������");
		dialog = builder.create();
	}


	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
