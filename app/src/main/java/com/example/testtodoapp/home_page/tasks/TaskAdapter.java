package com.example.testtodoapp.home_page.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.HomeFragment;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//За основу взят ресурс отсюда
//https://startandroid.ru/ru/uroki/vse-uroki-spiskom/113-urok-54-kastomizatsija-spiska-sozdaem-svoj-adapter.html

public class TaskAdapter extends BaseAdapter {
    private LayoutInflater root;
    private List<Object> objectList;
    private Activity home;

    AtomicBoolean isClicked = new AtomicBoolean(false);

    protected AlphaAnimation fadeIn = new AlphaAnimation(0.6f, 1.0f);
    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.6f);

    public TaskAdapter(Context context, List<Object> objectList, Activity home) {
        this.objectList = objectList;
        this.root = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.home = home;
    }

    @Override
    public int getCount() {
        return objectList.size();
    }

    @Override
    public Object getItem(int position) {
        return objectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        view = root.inflate(R.layout.task_item, parent, false);

        final Object obj = getItem(position);

        if (obj instanceof Task) {
            Task task = (Task) obj;
            int minutes = task.getMinute();
            String minutesString;
            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = String.valueOf(minutes);
            }

            // заполняем View в пункте списка данными из товаров: наименование, цена
            TextView taskTitle = view.findViewById(R.id.taskTitle);
            taskTitle.setText(task.getTitle().replaceAll("\n", " "));


            TextView taskDate = view.findViewById(R.id.taskDate);


            if (task.getHourOfDay() == 0 && task.getMinute() == 0) {
                taskDate.setText("");
            } else {
                taskDate.setText(task.getHourOfDay() + ":" + minutesString);
            }


            fadeIn.setDuration(250);
            fadeIn.setFillAfter(true);
            fadeOut.setDuration(250);
            fadeOut.setFillAfter(true);
            //fadeOut.setStartOffset(4200+fadeIn.getStartOffset());


            //Handling CheckBox
            CheckBox cbBuy = view.findViewById(R.id.returnBox);
            cbBuy.setOnCheckedChangeListener(myCheckChangeList);
            cbBuy.setTag(position);
            cbBuy.setChecked(task.getCompletionStatus());

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
            int priority = task.getPriority().ordinal();

            if (priority == 0) {
                imageView.setImageResource(R.drawable.priority_high_dark_theme);
            } else if (priority == 1) {
                imageView.setImageResource(R.drawable.priority_med_dark_theme);
            } else if (priority == 2) {
                imageView.setImageResource(R.drawable.priority_low_dark_theme);
            }

            view.setOnClickListener(v -> {
                cbBuy.setChecked(!cbBuy.isChecked());
                task.setCompletionStatus(cbBuy.isChecked());
                cbBuy.callOnClick();

                MainActivity.dbHandler.editTask(task);
            });

            view.setOnLongClickListener(v -> {
                Intent intent = new Intent(home, EditTaskActivity.class);
                intent.putExtra("TASK_HASH_CODE", task.getHashKey());
                home.startActivity(intent);
                return true;
            });
        } else if (obj instanceof String) {
            String s = (String) obj;
            view = root.inflate(R.layout.day_of_week, parent, false);
            ((TextView) view.findViewById(R.id.dayOfWeekTitle)).setText(s);

            view.setOnLongClickListener(v -> {
                Object nextObj = getItem(position + 1);
                Task task = (Task) nextObj;
                Task newTask = new Task();


                newTask.setTitle("Task on " + s);

                newTask.setYear(task.getYear());
                newTask.setMonthOfYear(task.getMonthOfYear());
                newTask.setDayOfMonth(task.getDayOfMonth());
                MainActivity.dbHandler.insertData(newTask);
                Intent intent = new Intent(home, EditTaskActivity.class);
                intent.putExtra("TASK_HASH_CODE", newTask.getHashKey());
                home.startActivity(intent);
                return true;
            });

        }

        return view;
    }

    // обработчик для чекбоксов
    private OnCheckedChangeListener myCheckChangeList = (buttonView, isChecked) -> {
        // меняем данные товара (в корзине или нет)
        Task task = (Task) getItem((Integer) buttonView.getTag());
        task.setCompletionStatus(isChecked);
        MainActivity.dbHandler.editTask(task);
    };


}
