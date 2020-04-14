package com.example.testtodoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testtodoapp.basics.Task;

//TODO Исприавить окно приоритета
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "Tasks.db";
    private static final String DB_TABLE = "Tasks_Table";

    //columns
    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    //public static final String PRIORITY = "PRIORITY";
    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY = "DAY";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";


    public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            //PRIORITY + "" +
            YEAR + " INTERGER, " +
            MONTH + " INTERGER, " +
            DAY + " INTERGER, " +
            HOUR + " INTERGER, " +
            MINUTE + " INTERGER " +
            ")";


    public DataBaseHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        onCreate(db);
    }

    public void insertData(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MONTH, task.getMonthOfYear());
        contentValues.put(YEAR, task.getYear());
        contentValues.put(DESCRIPTION, task.getDescription());
        contentValues.put(HOUR, task.getHourOfDay());
        contentValues.put(TITLE, task.getTitle());
        //contentValues.put(PRIORITY, task.get);
        contentValues.put(MINUTE, task.getMinute());
        contentValues.put(DAY, task.getDayOfMonth());

        db.insert(DB_TABLE, null, contentValues);
    }

    public void editTask(Task task) {

    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + DB_TABLE;

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }


    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + DB_TABLE);
    }
}
