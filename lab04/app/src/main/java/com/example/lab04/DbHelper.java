package com.example.lab04;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private Context context;
    public DbHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null , Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query ="CREATE TABLE " +
                Constants.TABLE_NAME + " (" +
                Constants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Constants.COLUMN_USERNAME + " TEXT UNIQUE, " +
                Constants.COLUMN_NAME + " TEXT, " +
                Constants.COLUMN_SCORE + " INTEGER, " +
                Constants.COLUMN_TIME_SPENT + " INTEGER);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
    public boolean addScore(TableEntry player){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.COLUMN_USERNAME, player.getUsername());
        cv.put(Constants.COLUMN_NAME, player.getName());
        cv.put(Constants.COLUMN_SCORE, player.getScore());
        cv.put(Constants.COLUMN_TIME_SPENT, (int)(player.getPlayingTime() / 1000));
        long res = db.insert(Constants.TABLE_NAME, null, cv);
        if (res == -1){
            Toast.makeText(context, "Failed to insert score ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public Cursor getAllData(){
        String query = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
             cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public boolean isNameAvailable(String name){
        String query = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " +
                Constants.COLUMN_USERNAME + "= ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[] {name});
        }
        return cursor.getCount() == 0;
    }
}
