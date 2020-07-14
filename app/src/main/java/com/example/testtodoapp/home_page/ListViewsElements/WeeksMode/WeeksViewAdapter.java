package com.example.testtodoapp.home_page.ListViewsElements.WeeksMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.HomeFragment;
import com.example.testtodoapp.home_page.tasks.EditTaskActivity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;

public class WeeksViewAdapter extends BaseAdapter {
    private LayoutInflater root;
    private List<WeeksTaskStruct> list;
    private Activity home;

    AtomicBoolean isClicked = new AtomicBoolean(false);

    protected AlphaAnimation fadeIn = new AlphaAnimation(0.6f, 1.0f);
    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.6f);

    public WeeksViewAdapter(List<WeeksTaskStruct> list, Activity home) {
        this.list = list;
        Context context = home.getApplicationContext();
        this.root = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.home = home;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WeeksTaskStruct getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view;
        view = root.inflate(R.layout.task_item, parent, false);

        final WeeksTaskStruct el = getItem(position);

        if (el.getDayOfWeek().equals("")) {
            int minutes = el.getTask().getMinute();
            String minutesString;
            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = String.valueOf(minutes);
            }

            // заполняем View в пункте списка данными из товаров: наименование, цена
            TextView taskTitle = view.findViewById(R.id.taskTitle);
            taskTitle.setText(el.getTask().getTitle().replaceAll("\n", " "));


            TextView taskDate = view.findViewById(R.id.taskDate);


            if (el.getTask().getHourOfDay() == 0 && el.getTask().getMinute() == 0) {
                taskDate.setText("");
            } else {
                taskDate.setText(el.getTask().getHourOfDay() + ":" + minutesString);
            }


            fadeIn.setDuration(250);
            fadeIn.setFillAfter(true);
            fadeOut.setDuration(250);
            fadeOut.setFillAfter(true);


            //Handling CheckBox
            CheckBox cbBuy = view.findViewById(R.id.returnBox);
            cbBuy.setOnCheckedChangeListener(myCheckChangeList);
            cbBuy.setTag(position);
            cbBuy.setChecked(el.getTask().getCompletionStatus());

            cbBuy.setOnClickListener(v -> {
                isClicked.set(true);
                if (cbBuy.isChecked()) {
                    HomeFragment.increaseCompletedTasksStatistics();
                    taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    taskTitle.startAnimation(fadeOut);
                    taskDate.startAnimation(fadeOut);
                } else {
                    HomeFragment.decreaseCompletedTasksStatistics();
                    taskTitle.setPaintFlags(taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    taskTitle.startAnimation(fadeIn);
                    taskDate.startAnimation(fadeIn);
                    taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
                    taskDate.setTextColor(Color.parseColor("#D4D4D4"));
                }
            });


            final Handler handler = new Handler();

            if (cbBuy.isChecked()) {
                taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                if (isClicked.get()) {
                    handler.postDelayed(() -> {
                        taskTitle.setTextColor(Color.parseColor("#A3D4D4D4"));
                        taskDate.setTextColor(Color.parseColor("#A3D4D4D4"));
                    }, 250);
                } else {
                    taskTitle.setTextColor(Color.parseColor("#A3D4D4D4"));
                    taskDate.setTextColor(Color.parseColor("#A3D4D4D4"));
                }
            } else {
                taskTitle.setPaintFlags(taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                if (isClicked.get()) {
                    handler.postDelayed(() -> {
                        taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
                        taskDate.setTextColor(Color.parseColor("#D4D4D4"));
                    }, 250);
                } else {
                    taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
                    taskDate.setTextColor(Color.parseColor("#D4D4D4"));
                }
            }


            //Handling priority color icon
            ImageView imageView = view.findViewById(R.id.priorityIconTask);
            int priority = el.getTask().getPriority().ordinal();

            if (priority == 0) {
                imageView.setImageResource(R.drawable.priority_high_dark_theme);
            } else if (priority == 1) {
                imageView.setImageResource(R.drawable.priority_med_dark_theme);
            } else if (priority == 2) {
                imageView.setImageResource(R.drawable.priority_low_dark_theme);
            }

            view.setOnClickListener(v -> {
                cbBuy.setChecked(!cbBuy.isChecked());
                el.getTask().setCompletionStatus(cbBuy.isChecked());
                cbBuy.callOnClick();

                MainActivity.dbHandler.editTask(el.getTask());
            });

            view.setOnLongClickListener(v -> {
                Intent intent = new Intent(home, EditTaskActivity.class);
                intent.putExtra("TASK_HASH_CODE", el.getTask().getHashKey());
                home.startActivity(intent);
                return true;
            });
        } else {
            String s = el.getDayOfWeek();
            view = root.inflate(R.layout.day_of_week, parent, false);
            ((TextView) view.findViewById(R.id.dayOfWeekTitle)).setText(s);

            view.setOnLongClickListener(v -> {
                Task task = getItem(position + 1).getTask();
                Task newTask = new Task();

                AlertDialog.Builder builder = new AlertDialog.Builder(home, THEME_DEVICE_DEFAULT_DARK);

                View dialogView = LayoutInflater.from(home)
                        .inflate(R.layout.dialog_new_task, null);

                builder.setView(dialogView)
                        .setTitle("Add new task")
                        // Add action buttons
                        .setPositiveButton("Set task", (dialog, id) -> {
                            EditText editText = dialogView.findViewById(R.id.taskName);
                            String taskTitle = editText.getText().toString();
                            newTask.setTitle(taskTitle);
                            newTask.setDescription("");
                            newTask.setYear(task.getYear());
                            newTask.setMonthOfYear(task.getMonthOfYear());
                            newTask.setDayOfMonth(task.getDayOfMonth());
                            MainActivity.dbHandler.insertData(newTask);
                            dialog.dismiss();
//                            HomeFragment.refreshTable();
                            HomeFragment.increaseTasksStatistics();
                            Intent intent = new Intent(home, EditTaskActivity.class);
                            intent.putExtra("TASK_HASH_CODE", newTask.getHashKey());
                            home.startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel())
                        .show();

                return true;
            });
        }
        return view;
    }

//     обработчик для чекбоксов
    private OnCheckedChangeListener myCheckChangeList = (buttonView, isChecked) -> {
        // меняем данные товара (в корзине или нет)
        Task task = getItem((Integer) buttonView.getTag()).getTask();
        task.setCompletionStatus(isChecked);
        MainActivity.dbHandler.editTask(task);
    };


}
