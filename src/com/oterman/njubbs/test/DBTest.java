package com.oterman.njubbs.test;

import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.db.MyDBHelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

public class DBTest extends AndroidTestCase {

	//create table board(id int primary key,boardName,chineseName,category,boardUrl)
	public void testInsert(){
		MyDBHelper dbHelper=new MyDBHelper(getContext(), "bbs_data",  1);
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String insert_sql="insert into board values (2,'PICTURE','ÌùÍ¼°æ','xixi','www.baidu.com')";
		
		//db.execSQL(insert_sql);
		
	}
	
	public void testQuery(){
		MyDBHelper dbHelper=new MyDBHelper(getContext(), "bbs_data",  1);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		Cursor cursor = db.rawQuery("select * from board", null);
		while(cursor.moveToNext()){
			int id = cursor.getInt(0);
			String boardName = cursor.getString(1);
			String chineseName = cursor.getString(2);
			String category = cursor.getString(3);
			String boardUrl = cursor.getString(4);
			BoardInfo info=new BoardInfo(id, boardName, category, chineseName, boardUrl);
			System.out.println(info);
		}
	}
	
	
	
}
