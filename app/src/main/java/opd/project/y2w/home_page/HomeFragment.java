//package com.example.testtodoapp.home_page;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.os.Vibrator;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.testtodoapp.MainActivity;
//import com.example.testtodoapp.OnSwipeTouchListener;
//import com.example.testtodoapp.R;
//import com.example.testtodoapp.basics.Task;
//import com.example.testtodoapp.home_page.ListViewsElements.DaysMode.DaysViewAdapter;
//import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksViewAdapter;
//import com.example.testtodoapp.home_page.tasks.AddTaskDialogFragment;
//import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksTaskStruct;
//import com.example.testtodoapp.home_page.ListViewsElements.WeeksMode.WeeksViewAssistantHome;
//import com.example.testtodoapp.home_page.tasks.ServiceRepeatable;
//import com.example.testtodoapp.settings.Settings;
//import com.example.testtodoapp.settings.SignInActivity;
//import com.example.testtodoapp.tasks_history.TasksHistory;
//import com.varunest.sparkbutton.SparkButton;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import static android.content.Context.MODE_PRIVATE;
//import static android.content.Context.VIBRATOR_SERVICE;
//
//public class HomeFragment extends Fragment {
//    @SuppressLint("StaticFieldLeak")
//    static ListView dayView; // окно Дня
//    private List<Task> taskList = new ArrayList<>(); // Лист в котором содержаться задачи
//
//    public static FragmentActivity faHome; // Активити, необходимое для работы адаптера
//    public BandAdapter ba;
//    private HomeFragment hf;
//    private Calendar calendar;
//
//    Vibrator vibrator;
//
//    private int counter;
//
//    public AtomicBoolean dailyMod;
//    public AtomicBoolean isCurrentWeek;
//
//    public static ArrayList<Integer> dayFillingArray = new ArrayList<>();
//
//    @SuppressLint("SetTextI18n")
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        calendar = Calendar.getInstance();
//        hf = this;
//
//        final View root = inflater.inflate(R.layout.fragment_home, container, false);
//        faHome = getActivity();
//        dayView = root.findViewById(R.id.dayList);
//        dayView.setDivider(null);
//
//
//        SparkButton button = root.findViewById(R.id.tasksHistoryButton);
//
//       vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        button.setOnClickListener(v -> {
//            Intent intent = new Intent(faHome, TasksHistory.class);
//            vibrator.vibrate(50);
//            startActivity(intent);
//        });
//
//
//        SparkButton settingsButton = root.findViewById(R.id.settingsHome);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        settingsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(faHome, Settings.class);
//            vibrator.vibrate(50);
//            startActivity(intent);
//        });
//
//
//        //Обработчик нажатия кнопки детального добавления
//        SparkButton slowAddButton = root.findViewById(R.id.slowAddButton);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        slowAddButton.setOnClickListener(view -> {
//            vibrator.vibrate(50);
//            if (MainActivity.email != null) {
//
//                if (ActivityCompat.checkSelfPermission(faHome, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(faHome, new String[]{Manifest.permission.READ_CALENDAR}, 1);
//                }
//                // Показать каскад диалоговых окон с добавлением задачи
//                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
//                assert getFragmentManager() != null;
//                dialog.show(getFragmentManager(), "slow");
//            } else {
//                Toast.makeText(faHome, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(faHome, SignInActivity.class);
//                intent.putExtra("tag_signed_in", 0);
//                startActivity(intent);
//            }
//        });
//
//        //Обработчик нажатия кнопки быстрого добавления
//        SparkButton fastAddButton = root.findViewById(R.id.fastAddButton);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        fastAddButton.setOnClickListener(view -> {
//            vibrator.vibrate(50);
//            if (MainActivity.email != null) {
//                // fastAddButton.setBackgroundResource(R.drawable.add_task_button_2);
//
//                AddTaskDialogFragment dialog = new AddTaskDialogFragment(hf);
//                assert getFragmentManager() != null;
//                dialog.show(getFragmentManager(), "fast");
//            } else {
//                Toast.makeText(faHome, "Please log in to add tasks", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(faHome, SignInActivity.class);
//                intent.putExtra("tag_signed_in", 0);
//                startActivity(intent);
//            }
//        });
//
//        // Утка
//        RecyclerView rv = root.findViewById(R.id.dateBand);
//        LinearLayoutManager llm = new LinearLayoutManager(faHome, LinearLayoutManager.HORIZONTAL, false);
//        //SnapHelper sh = new LinearSnapHelper();
//        //sh.attachToRecyclerView(rv);
//        rv.setLayoutManager(llm);
//        rv.setHasFixedSize(true);
//
//        SnapToBlock snapToBlock = new SnapToBlock(7);
//        snapToBlock.attachToRecyclerView(rv);
//
//        ba = new BandAdapter(14, this, llm);
//        rv.setAdapter(ba);
//
//
//        isCurrentWeek = new AtomicBoolean(true);
//        SparkButton firstWeek = root.findViewById(R.id.switchWeeks);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        firstWeek.setOnClickListener(v -> {
//            vibrator.vibrate(50);
//            isCurrentWeek.set(true);
//            refreshTable();
//        });
//
//        SparkButton secondWeek = root.findViewById(R.id.switchWeeks2);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        secondWeek.setOnClickListener(v -> {
//            vibrator.vibrate(50);
//            isCurrentWeek.set(false);
//            refreshTable();
//        });
//
//        // Режим отображения задач недельный/дневной
//        dailyMod = new AtomicBoolean(true);
//
//        Button switchModButton = root.findViewById(R.id.switchMod);
//
//        vibrator = (Vibrator) faHome.getSystemService(VIBRATOR_SERVICE);
//
//        switchModButton.setOnClickListener(v -> {
//            vibrator.vibrate(50);
//            //изменение вида окна задач
//            boolean tmpMod;
//            if (dailyMod.get()) {
//                switchModButton.setText("Weeks");
//                rv.setVisibility(View.GONE);
//                firstWeek.setVisibility(View.VISIBLE);
//                secondWeek.setVisibility(View.VISIBLE);
//                tmpMod = false;
//
//            } else {
//                switchModButton.setText("Days");
//                rv.setVisibility(View.VISIBLE);
//                firstWeek.setVisibility(View.GONE);
//                secondWeek.setVisibility(View.GONE);
//                tmpMod = true;
//
//            }
//            dailyMod.set(tmpMod);
//            refreshTable();
//        });
//
//
//        //Swipe between History and Home
//        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(faHome) {
//            @Override
//            public void onSwipeTop() {
//                //Toast.makeText(faHome, "Swipe to history", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(faHome, TasksHistory.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onSwipeBottom() {
//                switchModButton.callOnClick();
//            }
//
//            @Override
//            public void onSwipeLeft() {
//                if (dailyMod.get()) {
//                    counter = ba.getActiveDay();
//                    counter = counter == 13 ? 0 : ++counter;
//
//                    ba.setActiveDay(counter);
//                    calendar = ba.getDeltaCalendar(counter);
//                    rv.setAdapter(ba);
//
//                    if (counter >= 7 && counter <= 13) llm.scrollToPosition(7);
//                    refreshTable();
//                } else {
//                    if (isCurrentWeek.get()) {
//                        secondWeek.callOnClick();
//                    } else {
//                        firstWeek.callOnClick();
//                    }
//                }
//            }
//
//            @Override
//            public void onSwipeRight() {
//                if (dailyMod.get()) {
//                    counter = ba.getActiveDay();
//                    counter = (counter == 0) ? 13 : --counter;
//
//                    ba.setActiveDay(counter);
//                    calendar = ba.getDeltaCalendar(counter);
//                    rv.setAdapter(ba);
//
//                    if (counter >= 7 && counter <= 13) llm.scrollToPosition(7);
//                    refreshTable();
//                } else {
//                    if (isCurrentWeek.get()) secondWeek.callOnClick();
//                    else firstWeek.callOnClick();
//                }
//            }
//        };
//        root.setOnTouchListener(onSwipeTouchListener);
//
//        return root;
//    }
//
//
//    public void refreshTable() {
//        Cursor cursor;
//
//        if (dailyMod.get()) {
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            int month = calendar.get(Calendar.MONTH) + 1;
//            int year = calendar.get(Calendar.YEAR);
//            cursor = MainActivity.dbHandler.viewDataByDate(day, month, year);
//        } else {
//            Calendar dateAndTime = Calendar.getInstance();
//            int day = dateAndTime.get(Calendar.DAY_OF_MONTH);
//            int month = dateAndTime.get(Calendar.MONTH);
//            int year = dateAndTime.get(Calendar.YEAR);
//
//            if (isCurrentWeek.get()) {
//                cursor = MainActivity.dbHandler.viewDataByWeek(day, month, year);
//            } else {
//                cursor = MainActivity.dbHandler.viewDataByWeek(day + 7, month, year);
//            }
//        }
//
//        populateTable(cursor);
//        cursor.close();
//
//        setDayFillingArray();
//
//        serviceRepeatable();
//    }
//
//    private void setDayFillingArray() {
//        Cursor cursor = MainActivity.dbHandler.viewData();
//        dayFillingArray.clear();
//        if (!(cursor.getCount() == 0)) {
//            while (cursor.moveToNext()) {
//                int flag = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
//                if (flag == 0) {
//                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
//                    int i = MainActivity.dbHandler.getByHashCode(hash).getDayOfMonth();
//                    if (!dayFillingArray.contains(i)) {
//                        dayFillingArray.add(i);
//                    }
//                }
//            }
//        }
//        cursor.close();
//        ba.notifyDataSetChanged();
//    }
//
//
//    public void serviceRepeatable() {
//        Cursor cursor = MainActivity.dbHandler.viewDataByRepeatable();
//
//        ServiceRepeatable serviceRepeatable = new ServiceRepeatable();
//        //List<Task> tasksToEdit = new ArrayList<>();
//        if (!(cursor.getCount() == 0)) {
//            while (cursor.moveToNext()) {
//                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
//                Task parent = MainActivity.dbHandler.getByHashCode(hash);
//                serviceRepeatable.handleTask(parent);
//            }
//        }
//    }
//
//
//    public static void increaseTasksStatistics() {
//        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
//        int current = sharedPreferences.getInt("tasks_added", 0);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("tasks_added", (current + 1));
//        editor.apply();
//    }
//
//    public static void increaseCompletedTasksStatistics() {
//        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
//        int current = sharedPreferences.getInt("tasks_completed", 0);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("tasks_completed", (current + 1));
//        editor.apply();
//    }
//
//    public static void decreaseCompletedTasksStatistics() {
//        SharedPreferences sharedPreferences = faHome.getSharedPreferences("STATISTICS", MODE_PRIVATE);
//        int current = sharedPreferences.getInt("tasks_completed", 0);
//        current--;
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("tasks_completed", current);
//        editor.apply();
//    }
//
//    void refreshTable(@NonNull BandAdapter.DateViewHolder holder) {
//        calendar = (Calendar) holder.calendar.clone();
//        refreshTable();
//    }
//
//
//    // Заполнение таблицы
//    public void populateTable(Cursor cursor) {
//        List<Task> tasks = new ArrayList<>();
//        if (dailyMod.get()) {
//            if (!(cursor.getCount() == 0)) {
//                while (cursor.moveToNext()) {
//                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
//                    tasks.add(MainActivity.dbHandler.getByHashCode(hash));
//                }
//            }
//            Collections.sort(tasks);
//            DaysViewAdapter taskAdapter = new DaysViewAdapter(tasks, faHome);
//            dayView.setAdapter(taskAdapter);
//        } else {
//            List<WeeksTaskStruct> weekTaskList = WeeksViewAssistantHome.getWeekTaskList(cursor);
//            WeeksViewAdapter weekTaskAdapter = new WeeksViewAdapter(weekTaskList, faHome);
//            dayView.setAdapter(weekTaskAdapter);
//        }
//    }
//
//
//    //Обновляем таблицу при возобновлении работы активити
//    @Override
//    public void onResume() {
//        super.onResume();
//        refreshTable();
//    }
//
//    public static void makeToast(String string) {
//        Toast.makeText(faHome, string, Toast.LENGTH_SHORT).show();
//    }
//}
