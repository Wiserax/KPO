package com.example.testtodoapp.home_page;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.tasks.TaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    static ListView dayView; // окно Дня
    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи

    static FragmentActivity faHome; // Активити, необходимое для работы адаптера

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        faHome = getActivity();
        dayView = root.findViewById(R.id.dayList);
        dayView.setDivider(null);

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

        populateTable();

        //Обработчик нажатия кнопки детального добавления
        FloatingActionButton slowAddButton = root.findViewById(R.id.slowAddButton);
        slowAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Показать каскад диалоговых окон с добавлением задачи
                AddTaskDialogFragment dialog = new AddTaskDialogFragment();
                dialog.show(getFragmentManager(), "slow");
            }
        });

        //Обработчик нажатия кнопки быстрого добавления
        FloatingActionButton fastAddButton = root.findViewById(R.id.fastAddButton);
        fastAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTaskDialogFragment dialog = new AddTaskDialogFragment();
                dialog.show(getFragmentManager(), "fast");
            }
        });


        // Утка
        RecyclerView rv = root.findViewById(R.id.dateBand);
        LinearLayoutManager llm = new LinearLayoutManager(faHome, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper sh = new LinearSnapHelper();
        sh.attachToRecyclerView(rv);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        BandAdapter ba = new BandAdapter(14, faHome, llm);
        rv.setAdapter(ba);
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
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                tasks.add(MainActivity.dbHandler.getByHashCode(hash));
            }
        }

        TaskAdapter taskAdapter = new TaskAdapter(faHome, tasks, faHome);
        dayView.setAdapter(taskAdapter);
    }

}
