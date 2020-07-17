package com.example.testtodoapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.db.CalendarHandler;
import com.example.testtodoapp.db.DataBaseHandler;
import com.example.testtodoapp.home_page.BandAdapter;
import com.example.testtodoapp.home_page.ListViewsElements.DaysMode.DaysViewAdapter;
import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksTaskStruct;
import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksViewAdapter;
import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksViewAssistantHome;
import com.example.testtodoapp.home_page.SnapToBlock;
import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
import com.example.testtodoapp.home_page.tasks.ServiceRepeatable;
import com.example.testtodoapp.settings.Settings;
import com.example.testtodoapp.settings.SignInActivity;
import com.example.testtodoapp.tasks_history.TasksHistory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity
        implements AddTaskDialogFragment.AddTaskDialogListener {

    public static DataBaseHandler dbHandler;

    public static String email;

    //
    @SuppressLint("StaticFieldLeak")
    static ListView dayView; // окно Дня
    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи

//    public static FragmentActivity faHome; // Активити, необходимое для работы адаптера
    public BandAdapter ba;
    private Calendar calendar;


    public SparkButton fastAddButton;
    public SparkButton slowAddButton;
    public RecyclerView rv;

    Vibrator vibrator;

    private int counter;

    public AtomicBoolean dailyMod;
    public AtomicBoolean isCurrentWeek;

    public static SharedPreferences sharedPreferences;

    public static ArrayList<Integer> dayFillingArray = new ArrayList<>();
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        dbHandler = new DataBaseHandler(this);

        sharedPreferences = getSharedPreferences("STATISTICS", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, 2);
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
        }

        CalendarHandler.setContext(this);
        CalendarHandler.setActivity(this);
        CalendarHandler.setContentResolver(getContentResolver());

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            email = acct.getEmail();
        }

        Calendar dateAndTime = Calendar.getInstance();
        int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
        int month = dateAndTime.get(Calendar.MONTH);
        int year = dateAndTime.get(Calendar.YEAR);

        Cursor cursor = MainActivity.dbHandler.viewDataByDate(--day, ++month, year);
        scanForNotCompletedTasks(cursor);
        cursor.close();

        //
        calendar = Calendar.getInstance();

        dayView = findViewById(R.id.dayList);
        dayView.setDivider(null);


        SparkButton button = findViewById(R.id.tasksHistoryButton);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, TasksHistory.class);
            vibrator.vibrate(60);
            startActivity(intent);
        });

        SparkButton settingsButton = findViewById(R.id.settingsHome);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Settings.class);
            vibrator.vibrate(60);
            startActivity(intent);
        });

        //Обработчик нажатия кнопки детального добавления
        slowAddButton = findViewById(R.id.slowAddButton);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        slowAddButton.setOnClickListener(view -> {
            vibrator.vibrate(60);
            if (MainActivity.email != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
                }
                // Показать каскад диалоговых окон с добавлением задачи
                AddTaskDialogFragment dialog = new AddTaskDialogFragment(this);
//                assert getFragmentManager() != null;
                dialog.show(getSupportFragmentManager(), "slow");
            } else {
                Toast.makeText(this, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.putExtra("tag_signed_in", 0);
                startActivity(intent);
            }
        });

        //Обработчик нажатия кнопки быстрого добавления
        fastAddButton = findViewById(R.id.fastAddButton);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        fastAddButton.setOnClickListener(view -> {
            vibrator.vibrate(60);
            if (MainActivity.email != null) {
                // fastAddButton.setBackgroundResource(R.drawable.add_task_button_2);

                AddTaskDialogFragment dialog = new AddTaskDialogFragment(this);
//                assert getFragmentManager() != null;
                dialog.show(getSupportFragmentManager(), "fast");

            } else {
                Toast.makeText(this, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.putExtra("tag_signed_in", 0);
                startActivity(intent);
            }
        });

        // Утка
        rv = findViewById(R.id.dateBand);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        //SnapHelper sh = new LinearSnapHelper();
        //sh.attachToRecyclerView(rv);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        SnapToBlock snapToBlock = new SnapToBlock(7);
        snapToBlock.attachToRecyclerView(rv);

        ba = new BandAdapter(14, this, llm);
        rv.setAdapter(ba);

        isCurrentWeek = new AtomicBoolean(true);
        SparkButton firstWeek = findViewById(R.id.switchWeeks);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        firstWeek.setOnClickListener(v -> {
            vibrator.vibrate(60);
            isCurrentWeek.set(true);
            refreshTable();
        });

        SparkButton secondWeek = findViewById(R.id.switchWeeks2);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        secondWeek.setOnClickListener(v -> {
            vibrator.vibrate(60);
            isCurrentWeek.set(false);
            refreshTable();
        });

        // Режим отображения задач недельный/дневной
        dailyMod = new AtomicBoolean(true);

        Button switchModButton = findViewById(R.id.switchMod);

        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        switchModButton.setOnClickListener(v -> {
            vibrator.vibrate(60);
            //изменение вида окна задач
            boolean tmpMod;
            if (dailyMod.get()) {
                switchModButton.setBackgroundResource(R.drawable.weeks_change);
                rv.setVisibility(View.INVISIBLE);
                firstWeek.setVisibility(View.VISIBLE);
                secondWeek.setVisibility(View.VISIBLE);
                tmpMod = false;

            } else {
                switchModButton.setBackgroundResource(R.drawable.days_change);
                rv.setVisibility(View.VISIBLE);
                firstWeek.setVisibility(View.INVISIBLE);
                secondWeek.setVisibility(View.INVISIBLE);
                tmpMod = true;

            }
            dailyMod.set(tmpMod);
            refreshTable();
        });


        //Swipe between History and Home
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                //Toast.makeText(faHome, "Swipe to history", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, TasksHistory.class);
                startActivity(intent);
            }

            @Override
            public void onSwipeBottom() {
                switchModButton.callOnClick();
            }

            @Override
            public void onSwipeLeft() {
                if (dailyMod.get()) {
                    counter = ba.getActiveDay();
                    counter = counter == 13 ? 0 : ++counter;

                    ba.setActiveDay(counter);
                    calendar = ba.getDeltaCalendar(counter);
                    rv.setAdapter(ba);

                    if (counter >= 7 && counter <= 13) llm.scrollToPosition(7);
                    refreshTable();
                } else {
                    if (isCurrentWeek.get()) {
                        secondWeek.callOnClick();
                    } else {
                        firstWeek.callOnClick();
                    }
                }
            }

            @Override
            public void onSwipeRight() {
                if (dailyMod.get()) {
                    counter = ba.getActiveDay();
                    counter = (counter == 0) ? 13 : --counter;

                    ba.setActiveDay(counter);
                    calendar = ba.getDeltaCalendar(counter);
                    rv.setAdapter(ba);

                    if (counter >= 7 && counter <= 13) llm.scrollToPosition(7);
                    refreshTable();
                } else {
                    if (isCurrentWeek.get()) secondWeek.callOnClick();
                    else firstWeek.callOnClick();
                }
            }
        };

        getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(onSwipeTouchListener);
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

        setDayFillingArray();

        serviceRepeatable();
    }

    private void setDayFillingArray() {
        Cursor cursor = MainActivity.dbHandler.viewData();
        dayFillingArray.clear();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                int flag = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
                if (flag == 0) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    int i = MainActivity.dbHandler.getByHashCode(hash).getDayOfMonth();
                    if (!dayFillingArray.contains(i)) {
                        dayFillingArray.add(i);
                    }
                }
            }
        }
        cursor.close();
        ba.notifyDataSetChanged();
    }


    public void serviceRepeatable() {
        Cursor cursor = MainActivity.dbHandler.viewDataByRepeatable();

        ServiceRepeatable serviceRepeatable = new ServiceRepeatable();
        if (!(cursor.getCount() == 0)) {
            while (cursor.moveToNext()) {
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                Task parent = MainActivity.dbHandler.getByHashCode(hash);
                serviceRepeatable.handleTask(parent);
            }
        }
    }

    public static void increaseTasksStatistics() {
        int current = sharedPreferences.getInt("tasks_added", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_added", (current + 1));
        editor.apply();
    }

    public static void increaseCompletedTasksStatistics() {
        int current = sharedPreferences.getInt("tasks_completed", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_completed", (current + 1));
        editor.apply();
    }

    public static void decreaseCompletedTasksStatistics() {
        int current = sharedPreferences.getInt("tasks_completed", 0);
        current--;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tasks_completed", current);
        editor.apply();
    }

    public void refreshTable(@NonNull BandAdapter.DateViewHolder holder) {
        calendar = (Calendar) holder.calendar.clone();
        refreshTable();
    }

    // Заполнение таблицы
    public void populateTable(Cursor cursor) {
        List<Task> tasks = new ArrayList<>();
        if (dailyMod.get()) {
            if (!(cursor.getCount() == 0)) {
                while (cursor.moveToNext()) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    tasks.add(MainActivity.dbHandler.getByHashCode(hash));
                }
            }
            Collections.sort(tasks);
            DaysViewAdapter taskAdapter = new DaysViewAdapter(tasks, this);
            dayView.setAdapter(taskAdapter);
        } else {
            List<WeeksTaskStruct> weekTaskList = WeeksViewAssistantHome.getWeekTaskList(cursor, isCurrentWeek);
            WeeksViewAdapter weekTaskAdapter = new WeeksViewAdapter(weekTaskList, this);
            dayView.setAdapter(weekTaskAdapter);
        }
    }


    //Обновляем таблицу при возобновлении работы активити
    @Override
    public void onResume() {
        super.onResume();
        refreshTable();
    }

    @Override
    public void addEvent(Task task) {
        CalendarHandler calendarHandler = new CalendarHandler();
        calendarHandler.addEvent(task);
    }

    private void scanForNotCompletedTasks(Cursor cursor) {
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
}
