package com.example.testtodoapp.home_page.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.HomeFragment;

import java.util.List;

//За основу взят ресурс отсюда
//https://startandroid.ru/ru/uroki/vse-uroki-spiskom/113-urok-54-kastomizatsija-spiska-sozdaem-svoj-adapter.html

public class TaskAdapter extends BaseAdapter {
    private LayoutInflater root;
    private List<Task> taskList;
    private Activity home;

    public TaskAdapter(Context context, List<Task> taskList, Activity home) {
        this.taskList = taskList;
        this.root = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.home = home;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = root.inflate(R.layout.task_item, parent, false);
        }

        final Task task = (Task) getItem(position);

        int minutes = task.getMinute();
        String minutesString;
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        // заполняем View в пункте списка данными из товаров: наименование, цена
        ((TextView) view.findViewById(R.id.taskTitle)).setText(task.getTitle().replaceAll("\n", " "));


        if (task.getHourOfDay() == 0 && task.getMinute() == 0) {
            ((TextView) view.findViewById(R.id.taskDate)).setText("");
        } else {
            ((TextView) view.findViewById(R.id.taskDate)).setText(
                    task.getHourOfDay() + ":" + minutesString);
        }


        //Handling CheckBox
        CheckBox cbBuy = view.findViewById(R.id.cbBox);
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        cbBuy.setTag(position);
        cbBuy.setChecked(task.getCompletionStatus());

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
            Intent intent = new Intent(home, EditTaskActivity.class);
            intent.putExtra("TASK_HASH_CODE", task.getHashKey());

            home.startActivity(intent);
        });

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
