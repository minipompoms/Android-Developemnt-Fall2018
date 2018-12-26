package com.example.paige.encryptionapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "assets.db";
    private static final int DATABASE_VERSION = 1;
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql_user = "CREATE TABLE auth(id integer primary key,username text null, password text null);";
        String sql_history = "CREATE TABLE history(id integer primary key,filename text, kunci text);";

        Log.d("LOG_DB", "onCreate: " + sql_user);
        db.execSQL(sql_user);
        db.execSQL(sql_history);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }



    public void insertDataHistory(String filename, String key){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO history(filename, kunci) VALUES ('"+filename+"','"+key+"');";
        db.execSQL(sql);
    }


}