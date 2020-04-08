package com.example.testtodoapp.ui.home;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class HomeFragment extends Fragment {
    static ListView dayView; // окно Дня
    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи

    static FragmentActivity faHome; // Активити, необходимое для работы адаптера
    private HomeViewModel homeViewModel; // Понятия не имею зачем эта переменная

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);


        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        faHome = getActivity();
        dayView = root.findViewById(R.id.dayList);

        //Отрисовка контура вокруг Day
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(15, Color.BLACK);
        dayView.setBackground(drawable);



        populateTable();

        //Обработчик нажатия кнопки
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Показать каскад диалоговых окон с добавлением задачи
                AddTaskDialogFragment dialog = new AddTaskDialogFragment();
                dialog.show(getFragmentManager(), "custom");
            }
        });

        return root;
    }

    public void refreshTable() {
        populateTable();
    }

    // Заполнение таблицы
    public void populateTable() {

        List<String> taskTitles = new ArrayList<>();

        for (Task task : MainActivity.taskList1) {
            taskTitles.add(task.getDayOfMonth() +
                    "." + task.getMonthOfYear() +
                    "." + task.getYear() +
                    " " + task.getHourOfDay() +
                    ":" + task.getMinute() +
                    " " + task.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(faHome,
                android.R.layout.simple_list_item_1, taskTitles);

        dayView.setAdapter(adapter);
    }
}
