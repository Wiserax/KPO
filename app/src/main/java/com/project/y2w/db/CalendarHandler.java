package com.project.y2w.db;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.core.app.ActivityCompat;

import com.project.y2w.MainActivity;
import com.project.y2w.basics.Task;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

@SuppressLint("Registered")
public class CalendarHandler extends Application {

    String DEBUG_TAG = "DEBUG_MESSAGE";


    

    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    private static ContentResolver cr;
    // Submit the query and get a Cursor object back.
    Cursor cur = null;
    Uri uri = CalendarContract.Calendars.CONTENT_URI;
    String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
            + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
            + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";

    String email = MainActivity.email;

    String[] selectionArgs = new String[]{email, "com.google",
            email};


    public static void setContentResolver(ContentResolver cr1) {
        cr = cr1;
    }

    public void addEvent(Task task, Activity activity) {
        Context mContext = activity.getApplicationContext();

        //Просим разрешение на взаимодействие с календарем
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }

        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        assert cur != null;
        while (cur.moveToNext()) {
            long calID;
            String displayName;
            String accountName;
            String ownerName;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            //displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            //accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            //ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            //Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth(), task.getHourOfDay(), task.getMinute());
            long startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth(), task.getHourOfDay() + 1, task.getMinute());
            long endMillis = endTime.getTimeInMillis();

            ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.CALENDAR_ID, calID);
            event.put(CalendarContract.Events.TITLE, task.getTitle());
            event.put(CalendarContract.Events.DESCRIPTION, task.getDescription());
            event.put(CalendarContract.Events.EVENT_LOCATION, "");
            event.put(CalendarContract.Events.DTSTART, startMillis);
            event.put(CalendarContract.Events.DTEND, endMillis);
            event.put(CalendarContract.Events.ALL_DAY, 0);
            event.put(CalendarContract.Events.STATUS, 1);

            TimeZone tz = TimeZone.getDefault();
            event.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            event.put(CalendarContract.Events.HAS_ALARM, 1); // 0 for false, 1 for true


            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);
            }

            Uri url = cr.insert(CalendarContract.Events.CONTENT_URI, event);

            assert url != null;
            long eventID = Long.parseLong(Objects.requireNonNull(url.getLastPathSegment()));

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);

            //Получим минуты до ремайндера
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("MINUTES_BEFORE_REMINDER", MODE_PRIVATE);
            int minutes = sharedPreferences.getInt("reminder_time", 0);

            values.put(CalendarContract.Reminders.MINUTES, minutes);
            //Log.d("reminder_time", "is + " + minutes);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

            task.setCalendarId(eventID);
            MainActivity.dbHandler.editTask(task);
        }
    }

    public void editEvent(Task task, Activity activity) {
        //Просим разрешение на взаимодействие с календарем
        Context mContext = activity.getApplicationContext();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        assert cur != null;
        while (cur.moveToNext()) {
            long calID;
            String displayName;
            String accountName;
            String ownerName;

            // Get the field values
            //calID = cur.getLong(PROJECTION_ID_INDEX);
            //displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            //accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            //ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            //Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth(), task.getHourOfDay(), task.getMinute());
            long startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth(), task.getHourOfDay() + 1, task.getMinute());
            long endMillis = endTime.getTimeInMillis();

            calID = task.getCalendarId();
            ContentValues values = new ContentValues();
            Uri updateUri;
            // The new title for the event
            values.put(CalendarContract.Events.TITLE, task.getTitle());
            values.put(CalendarContract.Events.DESCRIPTION, task.getDescription());
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calID);
            int rows = cr.update(updateUri, values, null, null);


            Long reminderID = checkIfReminderExist(cr, calID);


            if (task.getAlarmStatus()) {
                //занулим текущий ремайндер, если есть
                if (reminderID != null) {
                    Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminderID);
                    cr.delete(reminderUri, null, null);
                    //Log.i(DEBUG_TAG, "Alarm deleted");
                } else {
                    reminderID = calID;
                }

                //Получим минуты до ремайндера
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("MINUTES_BEFORE_REMINDER", MODE_PRIVATE);
                int minutes = sharedPreferences.getInt("reminder_time", 0);

                ContentValues values1 = new ContentValues();
                values1.put(CalendarContract.Reminders.EVENT_ID, reminderID);
                values1.put(CalendarContract.Reminders.MINUTES, minutes);
                values1.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                cr.insert(CalendarContract.Reminders.CONTENT_URI, values1);

                //Log.d("reminder_time", "is + " + minutes);
                //Log.i(DEBUG_TAG, "Alarm inserted");
            } else {
                if (reminderID != null) {
                    Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminderID);
                    //rows += cr.delete(reminderUri, null, null);
                    //Log.i(DEBUG_TAG, "Alarm deleted");
                }
            }

            //Log.i(DEBUG_TAG, "Rows updated: " + rows);
        }
    }

    private static Long checkIfReminderExist(ContentResolver contentResolver, long eventId) {
        Long reminderId = null;

        String[] projection = new String[]{
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.MINUTES
        };

        Cursor cursor = CalendarContract.Reminders.query(contentResolver, eventId, projection);

        while (cursor != null && cursor.moveToNext()) {
            reminderId = cursor.getLong(0);
        }

        if (cursor != null)
        cursor.close();

        return reminderId;
    }

    public void deleteEvent(Task task, Activity activity) {
        //Просим разрешение на взаимодействие с календарем

        Context mContext = activity.getApplicationContext();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        assert cur != null;
        while (cur.moveToNext()) {
            long calID;
            String displayName;
            String accountName;
            String ownerName;

            // Get the field values
            //calID = cur.getLong(PROJECTION_ID_INDEX);
            //displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            //accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            //ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            //Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

            calID = task.getCalendarId();
            Uri deleteUri;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calID);
            int rows = cr.delete(deleteUri, null, null);
            //Log.i(DEBUG_TAG, "Rows deleted: " + rows);
        }

    }

}

//===========================REMINDER========================================
            /*cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(Reminders.MINUTES, 15);
            values.put(Reminders.EVENT_ID, 259);
            values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            uri = cr.insert(Reminders.CONTENT_URI, values);*/