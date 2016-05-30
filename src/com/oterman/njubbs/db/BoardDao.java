package com.oterman.njubbs.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oterman.njubbs.bean.BoardInfo;
import com.oterman.njubbs.utils.UiUtils;

public class BoardDao {
	
	private SQLiteDatabase db;

	//create table board(id int primary key,boardName,chineseName,category,boardUrl)
	public BoardDao() {
		MyDBHelper dbHelper=new MyDBHelper(UiUtils.getContext(), "bbs_data",  1);
		db = dbHelper.getWritableDatabase();
	}

	public void insert(BoardInfo info){
		String sql="insert into board values(?,?,?,?,?)";
		db.execSQL(sql, new String[]{info.id+"",info.boardName,info.chineseName,info.category,info.boardUrl});
	}
	
	public List<BoardInfo> queryAll(){
		String sql="select * from board";
		List<BoardInfo> list=new ArrayList<>();
		
		Cursor cursor = db.rawQuery(sql, null);
		
		while(cursor.moveToNext()){
			int id = cursor.getInt(0);
			String boardName = cursor.getString(1);
			String chineseName = cursor.getString(2);
			String category = cursor.getString(3);
			String boardUrl = cursor.getString(4);
			BoardInfo info=new BoardInfo(id, boardName, category, chineseName, boardUrl);
			
			list.add(info);
		}
		return list;
	}
	/**
	 * 得到记录数
	 */
	public int getCount(){
		String sql="select count(*) from board";
		
		Cursor cursor = db.rawQuery(sql, null);
		
		if(cursor.moveToNext()){
			return cursor.getInt(0);
		}
		return -1;
	}
	
	public List<BoardInfo> queryByCondition(String condition){
		String sql="select * from board where boardName like ?  or chineseName like ?";
		List<BoardInfo> list=new ArrayList<>();
		Cursor cursor = db.rawQuery(sql, new String[]{"%"+condition+"%","%"+condition+"%"});
		
		while(cursor.moveToNext()){
			int id = cursor.getInt(0);
			String boardName = cursor.getString(1);
			String chineseName = cursor.getString(2);
			String category = cursor.getString(3);
			String boardUrl = cursor.getString(4);
			BoardInfo info=new BoardInfo(id, boardName, category, chineseName, boardUrl);
			
			list.add(info);
		}
		return list;
	}
	
	public BoardInfo getInfoByName(String name){
		String sql="select * from board where boardName=?";
		
		Cursor cursor = db.rawQuery(sql, new String[]{name});
		
		if(cursor.moveToNext()){
			int id = cursor.getInt(0);
			String boardName = cursor.getString(1);
			String chineseName = cursor.getString(2);
			String category = cursor.getString(3);
			String boardUrl = cursor.getString(4);
			BoardInfo info=new BoardInfo(id, boardName, category, chineseName, boardUrl);
			return info;
		}
		
		return null;
		
	}
	
}
