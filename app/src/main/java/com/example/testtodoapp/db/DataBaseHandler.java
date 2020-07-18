package com.example.testtodoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.testtodoapp.basics.Priority;
import com.example.testtodoapp.basics.RepeatableTask;
import com.example.testtodoapp.basics.Task;

import java.util.Calendar;

//TODO Исприавить окно приоритета
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "Tasks.db";

    private static final String DB_TABLE_1 = "Tasks_Table";
    private static final String DB_TABLE_2 = "REPEATABLE_TASKS_TABLE";

    //columns
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String PRIORITY = "PRIORITY";
    private static final String YEAR = "YEAR";
    private static final String MONTH = "MONTH";
    private static final String DAY = "DAY";
    private static final String HOUR = "HOUR";
    private static final String MINUTE = "MINUTE";
    private static final String HASH_CODE = "HASH_CODE";
    private static final String IS_COMPLETE = "IS_COMPLETE";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String CALENDAR_ID = "CALENDAR_ID";
    private static final String IS_REPEATABLE = "IS_REPEATABLE";

    private static final String PARENT_HASH = "PARENT_ID";
    private static final String CHILD_HASH = "CHILD_ID";
    private static final String REPEAT_PERIOD = "REPEAT_PERIOD";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_1 + " (" +
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
            CALENDAR_ID + " INTEGER, " +
            IS_REPEATABLE + " INTEGER " +
            ")";

    private static final String CREATE_TABLE_2 = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_2 + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            HASH_CODE + " INTEGER, " +
            PARENT_HASH + " INTEGER, " +
            CHILD_HASH + " INTEGER, " +
            REPEAT_PERIOD + " INTEGER " +
            ")";

    private static final String CREATE_TRIGGER = "CREATE TRIGGER delete_till_300 INSERT ON " + DB_TABLE_1 + " WHEN (select count(*) from "  + DB_TABLE_1 +  " )>300 " +
            "BEGIN " +
            "    DELETE FROM " + DB_TABLE_1 +  " WHERE " + HASH_CODE +  " IN  (SELECT " + HASH_CODE + " FROM "  + DB_TABLE_1 +  " ORDER BY " + ID +
            "  limit (select count(*) - 300 from " + DB_TABLE_1 + " ));" +
            " END;";



    public DataBaseHandler(Context context) {
        super(context, DB_NAME, null, 1123);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TRIGGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_1);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_2);
        onCreate(db);
    }


    //=======================================INSERT_BLOCK=======================================

    public void insertChildAndRepeatable(RepeatableTask repeatableTask, Task parent) {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        c.set(parent.getYear(),
                parent.getMonthOfYear(),
                parent.getDayOfMonth());

        c.add(Calendar.DATE, repeatableTask.getPeriod());

        Task child = new Task(parent.getTitle(), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), parent.getHourOfDay(), parent.getMinute());

        CalendarHandler calendarHandler = new CalendarHandler();
        calendarHandler.addEvent(child);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MONTH, child.getMonthOfYear());
        contentValues.put(YEAR, child.getYear());
        contentValues.put(DESCRIPTION, child.getDescription());
        contentValues.put(HOUR, child.getHourOfDay());
        contentValues.put(TITLE, child.getTitle());
        contentValues.put(MINUTE, child.getMinute());
        contentValues.put(DAY, child.getDayOfMonth());
        contentValues.put(HASH_CODE, child.getHashKey());
        contentValues.put(CALENDAR_ID, child.getCalendarId());
        //ordinal() возвращает порядковый номер определенной константы (нумерация начинается с 0):
        contentValues.put(PRIORITY, child.getPriority().ordinal());
        //int debugValue = child.getAlarmStatus() ? 1 : 0;
        //contentValues.put(ALARM_STATUS, debugValue);
        contentValues.put(ALARM_STATUS, 1);
        contentValues.put(IS_REPEATABLE, 0);
        contentValues.put(IS_COMPLETE, 0);
        db.insert(DB_TABLE_1, null, contentValues);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(HASH_CODE, repeatableTask.getHashKey());
        contentValues1.put(PARENT_HASH, repeatableTask.getParentHash());
        contentValues1.put(CHILD_HASH, child.getHashKey());
        contentValues1.put(REPEAT_PERIOD, repeatableTask.getPeriod());

        db.insert(DB_TABLE_2, null, contentValues1);
    }

    public void insertChild(RepeatableTask repeatableTask, Task parent) {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        c.set(parent.getYear(),
                parent.getMonthOfYear(),
                parent.getDayOfMonth());

        c.add(Calendar.DATE, repeatableTask.getPeriod());

        Task child = new Task(parent.getTitle(), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH),
                c.get(Calendar.YEAR), parent.getHourOfDay(), parent.getMinute());

        CalendarHandler calendarHandler = new CalendarHandler();
        calendarHandler.addEvent(child);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MONTH, child.getMonthOfYear());
        contentValues.put(YEAR, child.getYear());
        contentValues.put(DESCRIPTION, child.getDescription());
        contentValues.put(HOUR, child.getHourOfDay());
        contentValues.put(TITLE, child.getTitle());
        contentValues.put(MINUTE, child.getMinute());
        contentValues.put(DAY, child.getDayOfMonth());
        contentValues.put(HASH_CODE, child.getHashKey());
        contentValues.put(CALENDAR_ID, child.getCalendarId());
        //ordinal() возвращает порядковый номер определенной константы (нумерация начинается с 0):
        contentValues.put(PRIORITY, child.getPriority().ordinal());
        //int debugValue = child.getAlarmStatus() ? 1 : 0;
        //contentValues.put(ALARM_STATUS, debugValue);
        contentValues.put(ALARM_STATUS, 1);
        contentValues.put(IS_REPEATABLE, 0);
        contentValues.put(IS_COMPLETE, 0);
        db.insert(DB_TABLE_1, null, contentValues);

        repeatableTask.setParentHash(parent.getHashKey());
        repeatableTask.setChildHash(child.getHashKey());
        editRepeatable(repeatableTask);
    }

    public void insertRepeatable(RepeatableTask parent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(HASH_CODE, parent.getHashKey());
        contentValues.put(PARENT_HASH, parent.getParentHash());
        contentValues.put(CHILD_HASH, parent.getChildHash());
        contentValues.put(REPEAT_PERIOD, parent.getPeriod());

        db.insert(DB_TABLE_2, null, contentValues);
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
        contentValues.put(PRIORITY, task.getPriority().ordinal());

        int debugValue = task.getAlarmStatus() ? 1 : 0;
        contentValues.put(ALARM_STATUS, debugValue);
        contentValues.put(IS_REPEATABLE, 0);
        contentValues.put(IS_COMPLETE, 0);

        db.insert(DB_TABLE_1, null, contentValues);
    }

    //=======================================UPDATE_BLOCK=======================================

    public void editRepeatable(RepeatableTask task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(PARENT_HASH, task.getParentHash());
        newValues.put(CHILD_HASH, task.getChildHash());
        newValues.put(REPEAT_PERIOD, task.getPeriod());

        String whereClause = HASH_CODE + "=?";
        // now define what those ? should be
        String[] whereArgs = new String[]{String.valueOf(task.getHashKey())};

        db.update(DB_TABLE_2, newValues, whereClause, whereArgs);
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
        newValues.put(IS_REPEATABLE, task.getRepeatableStatus());


        // you can .put() even more here if you want to update more than 1 row

        // define the WHERE clause w/o the WHERE and replace variables by ?
        // Note: there are no ' ' around ? - they are added automatically
        String whereClause = HASH_CODE + "=?";

        // now define what those ? should be
        String[] whereArgs = new String[]{String.valueOf(task.getHashKey())};

        db.update(DB_TABLE_1, newValues, whereClause, whereArgs);
    }

    //==========================================GETTERS_BLOCK=============================================

    public Cursor viewRepeatableTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_2;
        return db.rawQuery(query, null);
    }


    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_1;
        return db.rawQuery(query, null);
    }

    public Cursor viewDataByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + ID + " = " + id;
        return db.rawQuery(query, null);
    }

    public Cursor viewDataByDate(int day, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + YEAR + " = " + year + " AND "
                + MONTH + " = " + (month - 1) + " AND "
                + DAY + " = " + day;
        return db.rawQuery(query, null);
    }

    public Cursor viewDataByWeek(int day, int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();
        int[] week = {day, day + 6};
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + YEAR + " = " + year + " AND "
                + MONTH + " = " + month + " AND "
                + DAY + " >= " + week[0] + " AND "
                + DAY + " <= " + week[1];
        return db.rawQuery(query, null);
    }

    public Cursor viewDataByRepeatable() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + IS_REPEATABLE + " = " + 1;
        return db.rawQuery(query, null);
    }

    public Cursor viewDataByComplete() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + IS_COMPLETE + " = " + 1;
        return db.rawQuery(query, null);
    }

    public RepeatableTask getRepeatableByChildHash(int hashCode) {
        RepeatableTask task = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + DB_TABLE_2 + " WHERE "
                + CHILD_HASH + " = " + hashCode;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            task = new RepeatableTask();
            task.setChildHash(cursor.getInt(cursor.getColumnIndex(CHILD_HASH)));
            task.setParentHash(cursor.getInt(cursor.getColumnIndex(PARENT_HASH)));
            task.setPeriod(cursor.getInt(cursor.getColumnIndex(REPEAT_PERIOD)));
            task.setHashKey(cursor.getInt(cursor.getColumnIndex(HASH_CODE)));
        }
        cursor.close();

        return task;
    }

    public RepeatableTask getRepeatableByParentHash(int hashCode) {
        RepeatableTask task = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + DB_TABLE_2 + " WHERE "
                + PARENT_HASH + " = " + hashCode;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            task = new RepeatableTask();
            task.setChildHash(cursor.getInt(cursor.getColumnIndex(CHILD_HASH)));
            task.setParentHash(cursor.getInt(cursor.getColumnIndex(PARENT_HASH)));
            task.setPeriod(cursor.getInt(cursor.getColumnIndex(REPEAT_PERIOD)));
            task.setHashKey(cursor.getInt(cursor.getColumnIndex(HASH_CODE)));
        }
        cursor.close();

        return task;
    }

    public RepeatableTask getRepeatableByHashCode(int hashCode) {
        RepeatableTask task = new RepeatableTask();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = HASH_CODE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(hashCode)};
        Cursor cursor = db.query(DB_TABLE_2, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            task.setChildHash(cursor.getInt(cursor.getColumnIndex(CHILD_HASH)));
            task.setParentHash(cursor.getInt(cursor.getColumnIndex(PARENT_HASH)));
            task.setPeriod(cursor.getInt(cursor.getColumnIndex(REPEAT_PERIOD)));
            task.setHashKey(cursor.getInt(cursor.getColumnIndex(HASH_CODE)));
        }
        cursor.close();

        return task;
    }

    public Task getByHashCode(int hashCode) {
        Task task = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = HASH_CODE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(hashCode)};
        Cursor cursor = db.query(DB_TABLE_1, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            task = new Task();
            // достаем данные из курсора
            task.setDbUID(cursor.getInt(cursor.getColumnIndex(ID)));
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

            debugInt = cursor.getInt(cursor.getColumnIndex(IS_REPEATABLE));
            value = debugInt > 0;
            task.setRepeatableStatus(value);
        }
        cursor.close();
        return task;
    }

    public boolean containsInTasks(int hashCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = HASH_CODE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(hashCode)};
        Cursor cursor = db.query(DB_TABLE_1, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public Task getChildByDate(int day, int month, int year, String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        int[] week = {day + 1, day + 7};
        String query = "SELECT * FROM " + DB_TABLE_1 + " WHERE "
                + YEAR + " = " + year + " AND "
                + MONTH + " = " + month + " AND "
                + TITLE + " = '" + title + "' AND "
                + DAY + " >= " + week[0] + " AND "
                + DAY + " <= " + week[1];

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int hash = (cursor.getInt(cursor.getColumnIndex(HASH_CODE)));
            cursor.close();
            return getByHashCode(hash);
        }
        cursor.close();
        return null;
    }


    //========================================DELETE_BLOCK======================================

    public void deleteItem(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + DB_TABLE_1 + " WHERE " + HASH_CODE + " = " + task.getHashKey();
        db.execSQL(query);
    }

    public void deleteItem(RepeatableTask task) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + DB_TABLE_2 + " WHERE " + HASH_CODE + " = " + task.getHashKey();
        db.execSQL(query);
    }

    public void actionUnsetRepeatable(RepeatableTask task) {
        SQLiteDatabase db = this.getWritableDatabase();

        //kill child
        Task child = getByHashCode(task.getChildHash());
        if (child != null) {
            CalendarHandler calendarHandler = new CalendarHandler();
            calendarHandler.deleteEvent(child);
            String query2 = "DELETE FROM " + DB_TABLE_1 + " WHERE " + HASH_CODE + " = " + task.getChildHash();
            db.execSQL(query2);
        }

        //kill repeatable
        String query = "DELETE FROM " + DB_TABLE_2 + " WHERE " + HASH_CODE + " = " + task.getHashKey();
        db.execSQL(query);
    }

    public void killChild(RepeatableTask task) {
        SQLiteDatabase db = this.getWritableDatabase();

        Task child = getByHashCode(task.getChildHash());

        if (child != null) {
            CalendarHandler calendarHandler = new CalendarHandler();
            calendarHandler.deleteEvent(child);
            String query2 = "DELETE FROM " + DB_TABLE_1 + " WHERE " + HASH_CODE + " = " + task.getChildHash();
            db.execSQL(query2);
        }
    }

    public void deleteRepeatableCompletely(RepeatableTask task) {
        SQLiteDatabase db = this.getWritableDatabase();

        CalendarHandler calendarHandler = new CalendarHandler();
        calendarHandler.deleteEvent(getByHashCode(task.getChildHash()));
        calendarHandler.deleteEvent(getByHashCode(task.getParentHash()));

        String query1 = "DELETE FROM " + DB_TABLE_2 + " WHERE " + HASH_CODE + " = " + task.getHashKey();
        String query2 = "DELETE FROM " + DB_TABLE_1 + " WHERE " + HASH_CODE + " = " + task.getChildHash();
        String query3 = "DELETE FROM " + DB_TABLE_1 + " WHERE " + HASH_CODE + " = " + task.getParentHash();
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
    }

    public void clearDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE_1);
        db.execSQL("DELETE FROM " + DB_TABLE_2);
    }
}
