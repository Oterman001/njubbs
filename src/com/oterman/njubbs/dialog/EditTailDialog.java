package com.oterman.njubbs.dialog;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.oterman.njubbs.R;
import com.oterman.njubbs.utils.MyToast;
import com.oterman.njubbs.utils.SPutils;

/**
[1;37m¹þ¹þ¹þ[m  #000000
[1;31m¹þ¹þ¹þ[m  #E00000
[1;32m¹þ¹þ¹þ[m  #008000
[1;33m¹þ¹þ¹þ[m  #808000
[1;34m¹þ¹þ¹þ[m  #0000FF
[1;35m¹þ¹þ¹þ[m  #D000D0
[1;36m¹þ¹þ¹þ[m  #33A0A0

 * 
 */
public class EditTailDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private EditText etTail;

	private Context context;
	private RadioGroup rgColors;

	public EditTailDialog(Context context) {
		this.context = context;
		builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.dialog_edit_tail, null);

		etTail = (EditText) view.findViewById(R.id.et_content);
		etTail.setText(Build.MODEL + "");

		rgColors = (RadioGroup) view.findViewById(R.id.rg_colors);
		rgColors.setVisibility(View.VISIBLE);
		
		String tailColor = SPutils.getFromSP("tail_color");
		if(TextUtils.isEmpty(tailColor)){//Ä¬ÈÏÑÕÉ«  ×ÏÉ«
			rgColors.check(R.id.color_6);
			etTail.setTextColor(0xFFD000D0);
		}else{
			switch (tailColor.trim()) {
			case "color_1":
				etTail.setTextColor(0xFF000000);//°×É«
				rgColors.check(R.id.color_1);
				break;
			case "color_2":
				etTail.setTextColor(0xFFE00000);
				rgColors.check(R.id.color_2);
				break;
			case "color_3":
				etTail.setTextColor(0xFF008000);
				rgColors.check(R.id.color_3);
				break;
			case "color_4":
				etTail.setTextColor(0xFF808000);
				rgColors.check(R.id.color_4);
				break;
			case "color_5":
				etTail.setTextColor(0xFF0000FF);
				rgColors.check(R.id.color_5);
				break;
			case "color_6":
				etTail.setTextColor(0xFFD000D0);
				rgColors.check(R.id.color_6);
				break;
			case "color_7":
				etTail.setTextColor(0xFF33A0A0);
				rgColors.check(R.id.color_7);
				break;

			default:
				break;
			}
		}
		
		rgColors.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.color_1:
					etTail.setTextColor(0xFF000000);//°×É«
					break;
				case R.id.color_2:
					etTail.setTextColor(0xFFE00000);
					break;
				case R.id.color_3:
					etTail.setTextColor(0xFF008000);
					break;
				case R.id.color_4:
					etTail.setTextColor(0xFF808000);
					break;
				case R.id.color_5:
					etTail.setTextColor(0xFF0000FF);
					break;
				case R.id.color_6:
					etTail.setTextColor(0xFFD000D0);
					break;
				case R.id.color_7:
					etTail.setTextColor(0xFF33A0A0);
					break;

				default:
					break;
				}
			}
		});

		// ¶ÁÈ¡±£´æµÄÖµ
		String tail = SPutils.getFromSP("tail");

		if (tail != null && !TextUtils.isEmpty(tail)) {
			etTail.setText(tail);
		}

		builder.setView(view);

		builder.setPositiveButton("È·ÈÏ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String str = etTail.getText().toString();
				try {
					 if (TextUtils.isEmpty(str)) {
					
					 Field field = dialog.getClass().getSuperclass()
					 .getDeclaredField("mShowing");
					 field.setAccessible(true);
					 field.set(dialog, false); // false -²»ÄÜ¹Ø±Õ
					
					 MyToast.toast("»¹Ã»ÌîÄØ");
					 return;
					 }else{
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true); // true¡¡¹Ø±Õ¶Ô»°¿ò
					handleSaveTail();
					//±£´æÑÕÉ«
					handleSaveTailColor();
					 }
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		builder.setNegativeButton("È¡Ïû", null);
		builder.setTitle("±à¼­Ð¡Î²°Í");
		dialog = builder.create();
	}
	
	//±£´æÑÕÉ«
	protected void handleSaveTailColor() {
		int id = rgColors.getCheckedRadioButtonId();
		switch (id) {
		case R.id.color_1:
			SPutils.saveToSP("tail_color", "color_1");
			break;
		case R.id.color_2:
			SPutils.saveToSP("tail_color", "color_2");
			break;
		case R.id.color_3:
			SPutils.saveToSP("tail_color", "color_3");
			break;
		case R.id.color_4:
			SPutils.saveToSP("tail_color", "color_4");
			break;
		case R.id.color_5:
			SPutils.saveToSP("tail_color", "color_5");
			break;
		case R.id.color_6:
			SPutils.saveToSP("tail_color", "color_6");
			break;
		case R.id.color_7:
			SPutils.saveToSP("tail_color", "color_7");
			break;
		default:
			break;
		}
	}

	private void handleSaveTail() {
		// ±£´æÆðÀ´
		String tail = etTail.getText().toString().trim();
		SPutils.saveToSP("tail", tail);
		MyToast.toast("±£´æ³É¹¦");

	}

	public void show() {
		builder.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

}
