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
 * 站内提醒设置
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
		
		//初始化  默认为发送
		String mailToAt = SPutils.getFromSP("mailto_at");
		String mailToLouzhu=SPutils.getFromSP("mailto_louzhu");
		
		if("no".equals(mailToAt)){//不发送
			cbMailToAt.setChecked(false);
		}else{
			cbMailToAt.setChecked(true);
		}
		
		if("no".equals(mailToLouzhu)){//不发送
			cbMailToLouzhu.setChecked(false);
		}else{
			cbMailToLouzhu.setChecked(true);
		}
		
		builder.setView(view);

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//获取当前状态，保存起来
				if(cbMailToAt.isChecked()){//选中
					SPutils.saveToSP("mailto_at", "yes");
				}else{
					SPutils.saveToSP("mailto_at", "no");
				}
				
				if(cbMailToLouzhu.isChecked()){//选中
					SPutils.saveToSP("mailto_louzhu", "yes");
				}else{
					SPutils.saveToSP("mailto_louzhu", "no");
				}

			}
		});
		builder.setNegativeButton("取消", null);
		builder.setTitle("设置站内信提醒");
		dialog = builder.create();
	}


	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
