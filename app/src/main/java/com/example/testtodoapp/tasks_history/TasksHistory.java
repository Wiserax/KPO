package com.example.testtodoapp.tasks_history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.OnSwipeTouchListener;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.List;


public class TasksHistory extends AppCompatActivity {

    List<Task> tasks;
    ExpandableListView expListView;
    ExpandableListAdapter expListAdapter;
    List<Task> expListTitle;

    TasksHistory th;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) throws NullPointerException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_history);

        th = this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("History");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int completed  = sharedPreferences.getInt("tasks_completed", 0);
        int added  = sharedPreferences.getInt("tasks_added", 0);


        TextView addedText = findViewById(R.id.addedTasksField);
        addedText.setText("" + added);

        TextView completedText = findViewById(R.id.completedTasksField);
        completedText.setText("" + completed);

        expListView = findViewById(R.id.expListView);
        refreshTable();


        expListView.setOnGroupExpandListener(groupPosition -> {
        });

        expListView.setOnGroupCollapseListener(groupPosition -> {
        });

        expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);
        expListView.setGroupIndicator(null);

        //ArrayList<Boolean> checkboxStatus = new ArrayList<>();

        /*expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.returnBox);
                checkBox.setChecked(true);
                checkBox.setTag(groupPosition);
                return true;
            }
        });*/


        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(TasksHistory.this) {
            @Override
            public void onSwipeBottom() {
                onBackPressed();
            }
        };

        getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(onSwipeTouchListener);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void refreshTable() {
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
        expListAdapter = new TasksHistoryAdapter(this, tasks, th);
        expListView.setAdapter(expListAdapter);
    }
}