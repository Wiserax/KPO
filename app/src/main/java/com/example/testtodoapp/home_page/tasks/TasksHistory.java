package com.example.testtodoapp.home_page.tasks;

import android.database.Cursor;
import android.os.Bundle;

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TasksHistory extends AppCompatActivity {

    List<Task> tasks;
    ExpandableListView expListView;
    ExpandableListAdapter expListAdapter;
    List<String> expListTitle;
    HashMap<String, List<String>> expListDetail = new HashMap<>();
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_history);

        expListView = findViewById(R.id.expListView);

        Cursor cursor = MainActivity.dbHandler.viewData();

        tasks = new ArrayList<>();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                int flag = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
                if (flag == 1) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    tasks.add(MainActivity.dbHandler.getByHashCode(hash));
                }
            }
        }
        cursor.close();


        for (Task el : tasks) {
            List<String> list = new ArrayList<>();
            list.add(el.getDescription());
            expListDetail.put(el.getTitle(), list);
        }

        expListTitle = new ArrayList<>(expListDetail.keySet());
        expListAdapter = new TasksHistoryAdapter(this, expListTitle, expListDetail);

        expListView.setAdapter(expListAdapter);

        expListView.setOnGroupExpandListener(groupPosition -> {
        });

        expListView.setOnGroupCollapseListener(groupPosition -> {
        });

        expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);
    }
}