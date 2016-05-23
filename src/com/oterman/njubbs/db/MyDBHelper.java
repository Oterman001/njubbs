package com.oterman.njubbs.db;

import com.oterman.njubbs.utils.LogUtil;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper  extends SQLiteOpenHelper{
	
	final String CREATE_BOARDS_SQL="create table board(id int primary key,boardName,chineseName,category,boardUrl)";
	final String CREATE_FAV_BOARDS_SQL="create table favboard(id int primary key autoincrement,boardname,userid)";

	public MyDBHelper(Context context, String name, 
			int version) {
		super(context, name, null, version);
	}
	



	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.d("oncreate...创建表");
		db.execSQL(CREATE_BOARDS_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtil.d("onUpgrade...更新了。");
	}

}
