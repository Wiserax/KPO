package com.example.testtodoapp.home_page;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.OnSwipeTouchListener;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
import com.example.testtodoapp.home_page.tasks.TaskAdapter;
import com.example.testtodoapp.tasks_history.TasksHistory;
import com.example.testtodoapp.settings.Settings;
import com.example.testtodoapp.settings.SignInActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    static ListView dayView; // окно Дня
    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи

    private static FragmentActivity faHome; // Активити, необходимое для работы адаптера
    public BandAdapter ba;
    private HomeFragment hf;
    private Calendar calendar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        hf = this;

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        faHome = getActivity();
        dayView = root.findViewById(R.id.dayList);
        dayView.setDivider(null);

        Button taskHistoryButton = root.findViewById(R.id.tasksHistoryButton);
        taskHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(faHome, TasksHistory.class);
            startActivity(intent);
        });

        //Добавление кнопки очистить базу данных
        /*Button clearDButton = root.findViewById(R.id.clearButton);
        clearDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbHandler.clearDB();
                refreshTable();
                Toast.makeText(root.getContext(), "Table have been cleared", Toast.LENGTH_SHORT).show();
            }
        });*/


        Button settingsButton = root.findViewById(R.id.settingsHome);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(faHome, Settings.class);
            startActivity(intent);
        });

        refreshTable();


        //Обработчик нажатия кнопки детального добавления
        Button slowAddButton = root.findViewById(R.id.slowAddButton);
        slowAddButton.setOnClickListener(view -> {
            if (MainActivity.email != null) {
                // Показать каскад диалоговых окон с добавлением задачи
                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
                dialog.show(getFragmentManager(), "slow");
            } else {
                Toast.makeText(faHome, "Please log in to add tasks",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(faHome, SignInActivity.class);
                intent.putExtra("tag_signed_in", 0);
                startActivity(intent);
            }
        });

        //Обработчик нажатия кнопки быстрого добавления
        Button fastAddButton = root.findViewById(R.id.fastAddButton);
        fastAddButton.setOnClickListener(view -> {
            if (MainActivity.email != null) {
                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
                dialog.show(getFragmentManager(), "fast");
            } else {
                Toast.makeText(faHome, "Please log in to add tasks",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(faHome, SignInActivity.class);
                intent.putExtra("tag_signed_in", 0);
                startActivity(intent);
            }
        });


        // Утка
        RecyclerView rv = root.findViewById(R.id.dateBand);
        LinearLayoutManager llm = new LinearLayoutManager(faHome, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper sh = new LinearSnapHelper();
        sh.attachToRecyclerView(rv);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        ba = new BandAdapter(14, this, llm);
        rv.setAdapter(ba);



        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(faHome) {
            @Override
            public void onSwipeTop() {
                //Toast.makeText(faHome, "Swipe to history", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(faHome, TasksHistory.class);
                startActivity(intent);
            }
        };

        root.setOnTouchListener(onSwipeTouchListener);

        return root;
    }


    public void refreshTable() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        Cursor cursor = MainActivity.dbHandler.viewDataByDate(day, month, year);
        populateTable(cursor);
        cursor.close();

        Cursor cursor1 = MainActivity.dbHandler.viewDataByDate(day - 1, month, year);
        scanForNotCompetedTasks(cursor1);
        cursor1.close();
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

    void refreshTable(@NonNull BandAdapter.DateViewHolder holder) {
        calendar = (Calendar)holder.calendar.clone();
        refreshTable();
    }


    // Заполнение таблицы
    private void populateTable(Cursor cursor) {
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
