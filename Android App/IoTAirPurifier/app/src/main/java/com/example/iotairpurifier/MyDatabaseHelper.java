package com.example.iotairpurifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "SensorRecord.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_record";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PRE_DATE = "record_predate";
    private static final String COLUMN_PRE_AIRQUALITY = "record_preairquality";
    private static final String COLUMN_PRE_GASSENSOR = "record_pregassensor";
    private static final String COLUMN_POST_DATE = "record_postdate";
    private static final String COLUMN_POST_AIRQUALITY = "record_postairquality";
    private static final String COLUMN_POST_GASSENSOR = "record_postgassensor";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_PRE_DATE + " TEXT, " +
                        COLUMN_PRE_AIRQUALITY + " TEXT, " +
                        COLUMN_PRE_GASSENSOR + " TEXT, " +
                        COLUMN_POST_DATE + " TEXT, " +
                        COLUMN_POST_AIRQUALITY + " TEXT, " +
                        COLUMN_POST_GASSENSOR + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    void addRecord(String predate, String preairquality, String pregassensor, String postdate, String postairquality, String postgassensor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PRE_DATE, predate);
        cv.put(COLUMN_PRE_AIRQUALITY, preairquality);
        cv.put(COLUMN_PRE_GASSENSOR, pregassensor);
        cv.put(COLUMN_POST_DATE, postdate);
        cv.put(COLUMN_POST_AIRQUALITY, postairquality);
        cv.put(COLUMN_POST_GASSENSOR, postgassensor);
        db.insert(TABLE_NAME, null, cv);
    }

    void deleteRowsBeyond7Days(){
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_POST_DATE + " <= date('now', '-7 day')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
