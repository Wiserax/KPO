package com.example.testtodoapp.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.List;

//За основу взят ресурс отсюда
//https://startandroid.ru/ru/uroki/vse-uroki-spiskom/113-urok-54-kastomizatsija-spiska-sozdaem-svoj-adapter.html

public class TaskAdapter extends BaseAdapter {
    Context context;
    LayoutInflater root;
    List<Task> taskList;
    Activity home;

    public TaskAdapter(Context context, List<Task> taskList, Activity home) {
        this.context = context;
        this.taskList = taskList;
        this.root = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.home = home;
    }

    public TaskAdapter(Context context, LayoutInflater root, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.root = root;
    }

    public TaskAdapter(Context context, LayoutInflater root, List<Task> taskList, Activity home) {
        this.context = context;
        this.taskList = taskList;
        this.root = root;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = root.inflate(R.layout.task_item, parent, false);
        }

        Task task = (Task) getItem(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DELL_HUINA", "DELL NE HUINA");
                Cursor cursor = MainActivity.dbHandler.viewData();
                if (!(cursor.getCount() == 0))
                    cursor.moveToNext();
                int taskHash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));

                Intent intent = new Intent(home, EditTaskActivity.class);
                intent.putExtra("TASK_HASH_CODE", taskHash);

                home.startActivity(intent);
            }
        });

        // заполняем View в пункте списка данными из товаров: наименование, цена
        ((TextView) view.findViewById(R.id.taskTitle)).setText(task.getTitle());
        ((TextView) view.findViewById(R.id.taskDate)).setText(
                task.getDayOfMonth() + "-" +
                task.getMonthOfYear() + "-" +
                task.getYear() + " " +
                task.getHourOfDay() + ":" +
                task.getMinute());

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.cbBox);

        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        cbBuy.setTag(position);

        cbBuy.setChecked(task.getCompletionStatus());

        return view;
    }

    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // меняем данные товара (в корзине или нет)
            Task task = (Task) getItem((Integer) buttonView.getTag());
            task.setCompletionStatus(isChecked);
            MainActivity.dbHandler.editTask(task);
        }
    };
}
