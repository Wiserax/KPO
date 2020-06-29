package com.example.testtodoapp.home_page.tasks;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TasksHistory extends AppCompatActivity {

    List<Task> tasks;
    ExpandableListView expListView;
    ExpandableListAdapter expListAdapter;
    List<String> expListTitle;
    HashMap<String, Task> expListDetail = new HashMap<>();
//    private Calendar calendar;
    private Activity home;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_history);

        //Убирает верхний тайтл с названием
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        expListView = findViewById(R.id.expListView);

        Cursor cursor = MainActivity.dbHandler.viewData();

        tasks = new ArrayList<>();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                int flag = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
                if (flag == 1) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    Task temp = MainActivity.dbHandler.getByHashCode(hash);
                    expListDetail.put(temp.getTitle(), temp);
                }
            }
        }
        cursor.close();

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