package com.example.testtodoapp.ui.home;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
        drawable.setStroke(6,  Color.parseColor("#96000000")); // Размер и цвет рамки
        drawable.setColor(Color.parseColor("#6038eb50")); // Цвет бэкграунда
        drawable.setCornerRadius(40f); // Сколько будет закругляться
        dayView.setBackground(drawable);


        //Добавление кнопки очистить базу данных
        Button clearDButton = root.findViewById(R.id.clearButton);
        clearDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbHandler.clearDB();
                populateTable();
                Toast.makeText(root.getContext(), "Table have been cleared", Toast.LENGTH_SHORT).show();
            }
        });

        setOnItemListener(dayView);



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
        List<String> taskList = new ArrayList<>();

        Cursor cursor = MainActivity.dbHandler.viewData();
        ArrayList<Task> tasks = new ArrayList<>();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                int index = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                tasks.add(MainActivity.dbHandler.getByHashCode(index));
            }
        }

        TaskAdapter taskAdapter = new TaskAdapter(faHome, tasks);

        dayView.setAdapter(taskAdapter);
    }

    //TODO Сделать полноценное изменение таска с открытием нового активити
    public void setOnItemListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Task task = (Task) parent.getItemAtPosition(position);
                Toast.makeText(faHome, "Нажат элемент с именем " + task.getTitle(), Toast.LENGTH_SHORT).show();*/

                //String taskTitle = "Dell не хуйня";
                //int taskHash = task.getHashKey();


                //Следующие 4 строки на время пока нет адаптера
                Cursor cursor = MainActivity.dbHandler.viewData();
                if (!(cursor.getCount() == 0))
                cursor.moveToNext();
                int taskHash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));

                Intent intent = new Intent(faHome, EditTaskActivity.class);
                intent.putExtra("TASK_HASH_CODE", taskHash);


                startActivity(intent);
            }
        });
    }


}
