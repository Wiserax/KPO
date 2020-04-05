package com.example.testtodoapp.ui.home;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.testtodoapp.AddTaskDialogFragment;
import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment
{
    static ListView dayView;
    private List<Task> taskList = new ArrayList<>();

    static FragmentActivity faHome;

    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);


        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //Отрисовка контура вокруг Day
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, Color.BLACK);

        dayView = root.findViewById(R.id.dayList);
        dayView.setBackground(drawable);

        faHome = getActivity();

        populateTable();

        FloatingActionButton fab = root.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskDialogFragment dialog = new AddTaskDialogFragment();
                dialog.show(getFragmentManager(), "custom");
            }
        });

        return root;
    }

    public void sendTaskTitle(String taskTitle, TextView textView) {
        textView.setText(taskTitle);
        populateTable();
    }

    public void populateTable() {
    List<String> taskTitles = new ArrayList<>();

    for (Task task : MainActivity.taskList1) {
        taskTitles.add(task.getTitle());
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<>(faHome,
            android.R.layout.simple_list_item_1, taskTitles);

    dayView.setAdapter(adapter);
}
}
