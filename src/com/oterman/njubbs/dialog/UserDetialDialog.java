package com.oterman.njubbs.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.oterman.njubbs.R;
import com.oterman.njubbs.holders.UserDetailHolder;
import com.oterman.njubbs.utils.MyToast;

public class UserDetialDialog {

	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private UserDetailHolder holder;

	public UserDetialDialog(Context context) {
		builder = new AlertDialog.Builder(context);
		holder = new UserDetailHolder(context);
		builder.setView(holder.getRootView());
		dialog = builder.create();
		
	}
	
	public void updateStatus(String userId){
		holder.updateStatus(userId);
	}
	

	public void show(){
		dialog.show();
	}
	
	public void dismiss(){
		dialog.dismiss();
	}
	
}
