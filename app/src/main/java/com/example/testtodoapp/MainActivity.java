package com.example.testtodoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.db.CalendarHandler;
import com.example.testtodoapp.db.DataBaseHandler;
import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
import com.example.testtodoapp.settings.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AddTaskDialogFragment.AddTaskDialogListener {

    public static DataBaseHandler dbHandler;

    public static String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DataBaseHandler(this);

       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }

        CalendarHandler.setContext(this);
        CalendarHandler.setActivity(this);
        CalendarHandler.setContentResolver(getContentResolver());

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            email = acct.getEmail();
        }

        Calendar dateAndTime = Calendar.getInstance();
        int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
        int month = dateAndTime.get(Calendar.MONTH);
        int year = dateAndTime.get(Calendar.YEAR);

        Cursor cursor = MainActivity.dbHandler.viewDataByDate(--day, ++month, year);
        scanForNotCompetedTasks(cursor);
        cursor.close();

    }

    @Override
    public void addEvent(Task task) {
        CalendarHandler calendarHandler = new CalendarHandler();
        calendarHandler.addEvent(task);
    }

    private void scanForNotCompetedTasks(Cursor cursor) {
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                //если задача не выполнена, то переносим её на следующий день
                int is_complete = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
                if (is_complete == 0) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    Task task = MainActivity.dbHandler.getByHashCode(hash);
                    int nextDay = task.getDayOfMonth() + 1;
                    task.setDayOfMonth(nextDay);
                    MainActivity.dbHandler.editTask(task);
                }
            }
        }
    }

    /*private void scanRepeatableTasks(Cursor cursor) {
        if (cursor.getCount() == 0) {
            return;
        }
        List<Task> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            int is_complete = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
            if (is_complete == 0) {
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                Task task = MainActivity.dbHandler.getByHashCode(hash);

                tasks.add(task);
            }
        }

        //(медленно) Ищем повторяющиеся таски и отмечаем что
        for (int i = 0; i < (tasks.size() - 1); i++) {
            Task task = tasks.get(i);
            for (int j = (i + 1); (j < tasks.size()); j++) {
                Task tmpTask = tasks.get(j);
                if (task.getTitle().equals(tmpTask.getTitle())) {
                    task.setRepeatableStatus(false);
                    dbHandler.editTask(task);
                }
            }
        }
    }*/

   //TODO Выполнение таска убирает аларм и рипит о нём
}
