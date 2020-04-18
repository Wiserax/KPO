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
    public static final String HASH_CODE = "HASH_CODE";
    public static final String IS_COMPLETE = "IS_COMPLETE";


    public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            //PRIORITY + "" +
            YEAR + " INTERGER, " +
            MONTH + " INTERGER, " +
            DAY + " INTERGER, " +
            HOUR + " INTERGER, " +
            MINUTE + " INTERGER, " +
            HASH_CODE + " INTEGER, " +
            IS_COMPLETE + " INTEGER " +
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
        contentValues.put(HASH_CODE, task.getHashKey());
        if (task.getCompletionStatus()) {
            contentValues.put(IS_COMPLETE, 1);
        } else {
            contentValues.put(IS_COMPLETE, 0);
        }


        db.insert(DB_TABLE, null, contentValues);
    }

    public void editTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        // define the new value you want
        ContentValues newValues = new ContentValues();

        newValues.put(MONTH, task.getMonthOfYear());
        newValues.put(YEAR, task.getYear());
        newValues.put(DESCRIPTION, task.getDescription());
        newValues.put(HOUR, task.getHourOfDay());
        newValues.put(TITLE, task.getTitle());
        //contentValues.put(PRIORITY, task.get);
        newValues.put(MINUTE, task.getMinute());
        newValues.put(DAY, task.getDayOfMonth());


        // you can .put() even more here if you want to update more than 1 row

        // define the WHERE clause w/o the WHERE and replace variables by ?
        // Note: there are no ' ' around ? - they are added automatically
        String whereClause = HASH_CODE + "=?";

        // now define what those ? should be
        String[] whereArgs = new String[] {String.valueOf(task.getHashKey())};

        db.update(DB_TABLE, newValues, whereClause, whereArgs);
    }


    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + DB_TABLE;

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Task getByHashCode(int hashCode) {
        Task task = new Task();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + DB_TABLE + " where " + HASH_CODE + "=?";

        String selection = HASH_CODE + "=?";
        String[] selectionArgs = new String[] {String.valueOf(hashCode)};
        Cursor cursor = db.query(DB_TABLE, null, selection, selectionArgs, null, null, null);

        if(cursor.moveToFirst()){
            // достаем данные из курсора
            //TODO Возможно изменить когда добавится статус и приоритет
            task.setDayOfMonth(cursor.getInt(cursor.getColumnIndex(DAY)));
            task.setMonthOfYear(cursor.getInt(cursor.getColumnIndex(MONTH)));
            task.setYear(cursor.getInt(cursor.getColumnIndex(YEAR)));
            task.setHourOfDay(cursor.getInt(cursor.getColumnIndex(HOUR)));
            task.setMinute(cursor.getInt(cursor.getColumnIndex(MINUTE)));
            task.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
            task.setHashKey(cursor.getInt(cursor.getColumnIndex(HASH_CODE)));

            int debugInt = cursor.getInt(cursor.getColumnIndex(IS_COMPLETE));
            boolean value = debugInt > 0;
            task.setCompletionStatus(value);
        }
        return task;
    }

    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + DB_TABLE);
    }
}
