package com.oterman.njubbs.view;

import com.oterman.njubbs.dialog.WaitDialog;

public interface DialogControl {

	public abstract void hideWaitDialog();

	public abstract WaitDialog showWaitDialog();

	public abstract WaitDialog showWaitDialog(int resid);

	public abstract WaitDialog showWaitDialog(String text);
}
