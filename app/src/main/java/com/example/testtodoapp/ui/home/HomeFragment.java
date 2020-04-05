package com.example.testtodoapp.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.testtodoapp.AddTaskDialogFragment;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment
{

    private HomeViewModel homeViewModel;
    private Task testTask = new Task();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ListView dayView = root.findViewById(R.id.dayList);

        //List<Task> itemList = new List();

        // определяем строковый массив
        final String[] catNames = new String[]{
                "Рыжик", "Барсик", "Мурзик", "Мурка", "Васька",
                "Томасина", "Кристина", "Пушок", "Дымка", "Кузя",
                "Китти", "Масяня", "Симба"
        };


        // используем адаптер данных
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, catNames);


        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, Color.BLACK);
        dayView.setBackground(drawable);

        dayView.setAdapter(adapter);


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
        testTask.setTitle(taskTitle);
        int i = 1;
    }
}
