package com.example.testtodoapp.tasks_history;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.OnSwipeTouchListener;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TasksHistory extends AppCompatActivity {

    List<Task> tasks;
    ExpandableListView expListView;
    ExpandableListAdapter expListAdapter;
    List<Task> expListTitle;
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
//                    Task temp = MainActivity.dbHandler.getByHashCode(hash);
                    tasks.add(MainActivity.dbHandler.getByHashCode(hash));
                }
            }
        }
        cursor.close();

        expListTitle = new ArrayList<>(tasks);

//        ArrayList listForSort = new ArrayList<String>();

        expListAdapter = new TasksHistoryAdapter(this, tasks);

        expListView.setAdapter(expListAdapter);

        expListView.setOnGroupExpandListener(groupPosition -> {
        });

        expListView.setOnGroupCollapseListener(groupPosition -> {
        });

        expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);


        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(TasksHistory.this) {
            @Override
            public void onSwipeBottom() {
                //Toast.makeText(TasksHistory.this, "Swipe to history", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        };

        getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(onSwipeTouchListener);
    }
}