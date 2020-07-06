package com.example.testtodoapp.home_page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.testtodoapp.CircularList;
import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.OnSwipeTouchListener;
import com.example.testtodoapp.R;
import com.example.testtodoapp.WeekViewAssistant;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
import com.example.testtodoapp.home_page.tasks.TaskAdapter;
import com.example.testtodoapp.tasks_history.TasksHistory;
import com.example.testtodoapp.settings.Settings;
import com.example.testtodoapp.settings.SignInActivity;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    static ListView dayView; // окно Дня
    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи

    public static FragmentActivity faHome; // Активити, необходимое для работы адаптера
    public BandAdapter ba;
    private HomeFragment hf;
    private Calendar calendar;

    public AtomicBoolean dailyMod;

    public AtomicBoolean isCurrentWeek;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendar = Calendar.getInstance();
        hf = this;


        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        faHome = getActivity();
        dayView = root.findViewById(R.id.dayList);
        dayView.setDivider(null);

        SparkButton button = root.findViewById(R.id.tasksHistoryButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(faHome, TasksHistory.class);
            startActivity(intent);
        });


        SparkButton settingsButton = root.findViewById(R.id.settingsHome);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(faHome, Settings.class);
            startActivity(intent);
        });


        //Обработчик нажатия кнопки детального добавления
        Button slowAddButton = root.findViewById(R.id.slowAddButton);
        slowAddButton.setOnClickListener(view -> {
            if (MainActivity.email != null) {
                // Показать каскад диалоговых окон с добавлением задачи
                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
                assert getFragmentManager() != null;
                dialog.show(getFragmentManager(), "slow");
            } else {
                Toast.makeText(faHome, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(faHome, SignInActivity.class);
                intent.putExtra("tag_signed_in", 0);
                startActivity(intent);
            }
        });

        //Обработчик нажатия кнопки быстрого добавления
        Button fastAddButton = root.findViewById(R.id.fastAddButton);
        fastAddButton.setOnClickListener(view -> {
            if (MainActivity.email != null) {
                fastAddButton.setBackgroundResource(R.drawable.add_task_button_2);

                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
                assert getFragmentManager() != null;
                dialog.show(getFragmentManager(), "fast");

                final Handler handler = new Handler();
                handler.postDelayed(() -> fastAddButton.setBackgroundResource(R.drawable.add_task_button), 20);

            } else {
                Toast.makeText(faHome, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
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


        //Swipe between History and Home
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(faHome) {
            @Override
            public void onSwipeTop() {
                //Toast.makeText(faHome, "Swipe to history", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(faHome, TasksHistory.class);
                startActivity(intent);
            }
        };
        root.setOnTouchListener(onSwipeTouchListener);


        isCurrentWeek = new AtomicBoolean(true);
        Button firstWeek = root.findViewById(R.id.switchWeeks);
        firstWeek.setOnClickListener(v -> {
            isCurrentWeek.set(true);
            refreshTable();
        });

        Button secondWeek = root.findViewById(R.id.switchWeeks2);
        secondWeek.setOnClickListener(v -> {
            isCurrentWeek.set(false);
            refreshTable();
        });

        // Режим отображения задач недельный/дневной
        dailyMod = new AtomicBoolean(true);

        Button switchModButton = root.findViewById(R.id.switchMod);
        switchModButton.setOnClickListener(v -> {
            //изменение вида окна задач
            boolean tmpMod;
            if (dailyMod.get()) {
                switchModButton.setText("Weeks");
                rv.setVisibility(View.GONE);
                firstWeek.setVisibility(View.VISIBLE);
                secondWeek.setVisibility(View.VISIBLE);

                tmpMod = false;
            } else {
                switchModButton.setText("Days");
                rv.setVisibility(View.VISIBLE);
                firstWeek.setVisibility(View.GONE);
                secondWeek.setVisibility(View.GONE);

                tmpMod = true;
            }
            dailyMod.set(tmpMod);
            refreshTable();
        });

        refreshTable();
        return root;
    }


    public void refreshTable() {
        Cursor cursor;

        if (dailyMod.get()) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            cursor = MainActivity.dbHandler.viewDataByDate(day, month, year);
        } else {
            Calendar dateAndTime = Calendar.getInstance();
            int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
            int month = dateAndTime.get(Calendar.MONTH);
            int year = dateAndTime.get(Calendar.YEAR);

            if (isCurrentWeek.get()) {
                cursor = MainActivity.dbHandler.viewDataByWeek(day, month, year);
            } else {
                cursor = MainActivity.dbHandler.viewDataByWeek(day + 7, month, year);
            }
        }

        populateTable(cursor);
        cursor.close();

        Calendar dateAndTime = Calendar.getInstance();
        int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
        int month = dateAndTime.get(Calendar.MONTH);
        int year = dateAndTime.get(Calendar.YEAR);
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

    public static void increaseTasksStatistics() {
        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int current = sharedPreferences.getInt("tasks_added", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_added", (current + 1));
        editor.apply();
    }

    public static void increaseCompletedTasksStatistics() {
        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int current = sharedPreferences.getInt("tasks_completed", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_completed", (current + 1));
        editor.apply();
    }

    public static void decreaseCompletedTasksStatistics() {
        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int current = sharedPreferences.getInt("tasks_completed", 0);
        current--;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_completed", current);
        editor.apply();

        SharedPreferences sharedPreferences2 = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
        int current2 = sharedPreferences.getInt("tasks_completed", 0);
    }

    void refreshTable(@NonNull BandAdapter.DateViewHolder holder) {
        calendar = (Calendar) holder.calendar.clone();
        refreshTable();
    }


    // Заполнение таблицы
    public void populateTable(Cursor cursor) {
        List<Object> objectList = new ArrayList<>();
        if (dailyMod.get()) {
            if (!(cursor.getCount() == 0)) {
                while (cursor.moveToNext()) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    objectList.add(MainActivity.dbHandler.getByHashCode(hash));
                }
            }
        } else {
            objectList = WeekViewAssistant.proc(cursor);
        }
        TaskAdapter taskAdapter = new TaskAdapter(faHome, objectList, faHome);
        dayView.setAdapter(taskAdapter);
    }


    //Обновляем таблицу при возобновлении работы активити
    @Override
    public void onResume() {
        super.onResume();
        refreshTable();
    }
}
