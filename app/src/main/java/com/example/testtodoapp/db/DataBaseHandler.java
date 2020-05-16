package com.example.testtodoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testtodoapp.basics.Priority;
import com.example.testtodoapp.basics.Task;

//TODO Исприавить окно приоритета
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "Tasks.db";
    private static final String DB_TABLE = "Tasks_Table";

    //columns
    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PRIORITY = "PRIORITY";
    public static final String YEAR = "YEAR";
    public static final String MONTH = "MONTH";
    public static final String DAY = "DAY";
    public static final String HOUR = "HOUR";
    public static final String MINUTE = "MINUTE";
    public static final String HASH_CODE = "HASH_CODE";
    public static final String IS_COMPLETE = "IS_COMPLETE";
    public static final String ALARM_STATUS = "ALARM_STATUS";
    public static final String CALENDAR_ID = "CALENDAR_ID";


    public static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            DESCRIPTION + " TEXT, " +
            PRIORITY + " INTERGER, " +
            YEAR + " INTERGER, " +
            MONTH + " INTERGER, " +
            DAY + " INTERGER, " +
            HOUR + " INTERGER, " +
            MINUTE + " INTERGER, " +
            HASH_CODE + " INTEGER, " +
            IS_COMPLETE + " INTEGER, " +
            ALARM_STATUS + " INTEGER, " +
            CALENDAR_ID + " INTEGER " +
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
        contentValues.put(MINUTE, task.getMinute());
        contentValues.put(DAY, task.getDayOfMonth());
        contentValues.put(HASH_CODE, task.getHashKey());
        contentValues.put(CALENDAR_ID, task.getCalendarId());
        //ordinal() возвращает порядковый номер определенной константы (нумерация начинается с 0):
        //contentValues.put(PRIORITY, task.getPriority().ordinal());

        if (task.getAlarmStatus()) {
            contentValues.put(ALARM_STATUS, 1);
        } else {
            contentValues.put(ALARM_STATUS, 0);
        }

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
        newValues.put(MINUTE, task.getMinute());
        newValues.put(DAY, task.getDayOfMonth());
        //я в ахуе почему следующая строка работает
        newValues.put(IS_COMPLETE, task.getCompletionStatus());
        newValues.put(ALARM_STATUS, task.getAlarmStatus());
        newValues.put(CALENDAR_ID, task.getCalendarId());
        newValues.put(PRIORITY, task.getPriority().ordinal());


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
        String query = "SELECT * FROM " + DB_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor viewDataByDate(int day, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE + " WHERE "
                + YEAR + " = " + year + " AND "
                + MONTH + " = " + month + " AND "
                + DAY + " = " + day;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Task getByHashCode(int hashCode) {
        Task task = new Task();
        SQLiteDatabase db = this.getReadableDatabase();

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
            task.setCalendarId(cursor.getInt(cursor.getColumnIndex(CALENDAR_ID)));

            int debugInt = cursor.getInt(cursor.getColumnIndex(IS_COMPLETE));
            boolean value = debugInt > 0;
            task.setCompletionStatus(value);

            debugInt = cursor.getInt(cursor.getColumnIndex(ALARM_STATUS));
            value = debugInt > 0;
            task.setAlarmStatus(value);

            debugInt = cursor.getInt(cursor.getColumnIndex(PRIORITY));
            Priority priority = Priority.values()[debugInt]; // cast int to Enum
            task.setPriority(priority);
        }
        cursor.close();
        return task;
    }

    public void deleteItem(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM "+ DB_TABLE +" WHERE " + HASH_CODE + " = " + task.getHashKey();
        db.execSQL(query);
    }

    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE);
    }
}
