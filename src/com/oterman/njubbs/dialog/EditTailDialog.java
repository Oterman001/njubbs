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
 * 发帖小尾巴
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

		//读取保存的值
		String tail=SPutils.getFromSP("tail");
		
		if(tail!=null&&!TextUtils.isEmpty(tail)){
			etTail.setText(tail);
		}
		
		builder.setView(view);

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String str = etTail.getText().toString();
				try {
//					if (TextUtils.isEmpty(str)) {
//
//						Field field = dialog.getClass().getSuperclass()
//								.getDeclaredField("mShowing");
//						field.setAccessible(true);
//						field.set(dialog, false); // false -不能关闭
//						
//						MyToast.toast("还没填呢");
//						return;
//					}else{
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, true); // true　关闭对话框
						// 处理添加好友
						handleSaveTail();
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}


		});

		builder.setNegativeButton("取消", null);
		builder.setTitle("编辑小尾巴");
		dialog = builder.create();

	}
	
	private void handleSaveTail() {
		//保存起来
		String tail = etTail.getText().toString().trim();
		SPutils.saveToSP("tail", tail);
		MyToast.toast("保存成功");
		
	}


	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}


}
