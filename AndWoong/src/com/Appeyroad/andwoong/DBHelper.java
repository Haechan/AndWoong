package com.Appeyroad.andwoong;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
//i think this class can be replaced by adding codes in Activities
	public DBHelper(Context context){
		super(context,"AndWoong.s3db",null,1);
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		//System.out.println("let there be table");
		arg0.execSQL("CREATE TABLE IF NOT EXISTS woong(_id INTEGER PRIMARY KEY AUTOINCREMENT, startTime INTEGER, lastTime INTEGER, lastLocation INTEGER, sleepTime INTEGER, garlicNight INTEGER, wormwoodDay INTEGER, bgmSound INTEGER, effectSound INTEGER);");
		arg0.execSQL("insert into woong(startTime, lastTime, lastLocation, sleepTime, garlicNight, wormwoodDay, bgmSound, effectSound) values (0,0,0,0,0,0,1,1);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void onOpen(){
		//System.out.println("there is already a table");
	}

}