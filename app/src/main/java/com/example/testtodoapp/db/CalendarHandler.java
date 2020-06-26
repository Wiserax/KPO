package com.example.testtodoapp.db;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.settings.Settings;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

@SuppressLint("Registered")
public class CalendarHandler extends Application {

    String DEBUG_TAG = "DEBUG_MESSAGE";
    private static Context mContext;
    private static Activity mActivity;

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

    public void addEvent(Task task) {

        //Просим разрешение на взаимодействие с календарем
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);
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
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

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
            Uri url = cr.insert(CalendarContract.Events.CONTENT_URI, event);

            assert url != null;
            long eventID = Long.parseLong(Objects.requireNonNull(url.getLastPathSegment()));

            //start of failed shit
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.MINUTES, Settings.globalRemindersTime);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            //end of failed shit

            task.setCalendarId(eventID);
            MainActivity.dbHandler.editTask(task);
        }
    }

    public void editEvent(Task task) {
        //Просим разрешение на взаимодействие с календарем
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

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
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

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

            //Start of failed shit
            //ContentValues valuesq = new ContentValues();
            //valuesq.put(CalendarContract.Reminders.EVENT_ID, calID);
            //valuesq.put(CalendarContract.Reminders.MINUTES, Settings.globalRemindersTime);
            //valuesq.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            //Uri uri1 = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calID);
            //cr.update(uri1, values, null ,null);
            //end of failed shit

            Log.i(DEBUG_TAG, "Rows updated: " + rows);
        }
    }

    public void deleteEvent(Task task) {
        //Просим разрешение на взаимодействие с календарем
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);

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
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            Log.d(DEBUG_TAG, "   " + calID + " " + displayName + " " + accountName + " " + ownerName);

            calID = task.getCalendarId();
            Uri deleteUri;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calID);
            int rows = cr.delete(deleteUri, null, null);
            Log.i(DEBUG_TAG, "Rows deleted: " + rows);
        }

    }



    public static Context getContext () {
        return mContext;
    }

    public static void setContext(Context mContext1) {
        mContext = mContext1;
    }

    public static Activity getActivity () {
        return mActivity;
    }

    public static void setActivity(Activity mActivity1) {
        mActivity = mActivity1;
    }

}

//===========================REMINDER========================================
            /*cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(Reminders.MINUTES, 15);
            values.put(Reminders.EVENT_ID, 259);
            values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            uri = cr.insert(Reminders.CONTENT_URI, values);*/