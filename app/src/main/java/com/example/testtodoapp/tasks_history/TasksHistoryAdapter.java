package com.example.testtodoapp.tasks_history;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TasksHistoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Activity home;
    private List<HistoryItemStruct> items;
    TasksHistory th;

    public TasksHistoryAdapter(Context context, List<HistoryItemStruct> items, TasksHistory th) {
        this.context = context;
        this.items = items;
        this.th = th;
    }

    @Override
    public Task getChild(int i, int j) {
        return items.get(i).list.get(j);
//        return expListDetail.get(expListTitle.get(listPosition));
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Task task = getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.tasks_history_item, null);
        }
        TextView taskTitle = convertView.findViewById(R.id.listHistoryItemTitle);
        taskTitle.setText(task.getTitle());

        TextView taskDate = convertView.findViewById(R.id.taskHistoryItemDate);
        if ((task.getHourOfDay() != 0) && (task.getMinute() != 0)) {
            String dataString = task.getHourOfDay() + ":" + task.getMinute();
            taskDate.setText(dataString);
        } else {
            String dataString = "";
            taskDate.setText(dataString);
        }

        ImageView priorityIcon = convertView.findViewById(R.id.taskHistoryItemPriority);
        int priority = task.getPriority().ordinal();

        if (priority == 0) {
            priorityIcon.setImageResource(R.drawable.priority_high_dark_theme);
        } else if (priority == 1) {
            priorityIcon.setImageResource(R.drawable.priority_med_dark_theme);
        } else if (priority == 2) {
            priorityIcon.setImageResource(R.drawable.priority_low_dark_theme);
        }


        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(250);
        fadeOut.setFillAfter(true);

        ImageView deleteButton = convertView.findViewById(R.id.trashIconHistory);
        ImageView returnButton = convertView.findViewById(R.id.returnButton);

        deleteButton.setOnClickListener(v -> {
            MainActivity.dbHandler.deleteItem(task);

            taskTitle.startAnimation(fadeOut);
            taskDate.startAnimation(fadeOut);
            returnButton.startAnimation(fadeOut);
            deleteButton.startAnimation(fadeOut);
            final Handler handler = new Handler();
            handler.postDelayed(() -> th.refreshTable(), 250);
        });

        returnButton.setOnClickListener(v -> {
            task.setCompletionStatus(false);
            Calendar dateAndTime = Calendar.getInstance();
            task.setDayOfMonth(dateAndTime.get(Calendar.DAY_OF_MONTH));
            task.setMonthOfYear(dateAndTime.get(Calendar.MONTH));
            task.setYear(dateAndTime.get(Calendar.YEAR));
            MainActivity.dbHandler.editTask(task);

            taskTitle.startAnimation(fadeOut);
            taskDate.startAnimation(fadeOut);
            returnButton.startAnimation(fadeOut);
            deleteButton.startAnimation(fadeOut);
            priorityIcon.startAnimation(fadeOut);


            final Handler handler = new Handler();
            handler.postDelayed(() -> th.refreshTable(), 250);
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return items.get(listPosition).list.size();
    }

    @Override
    public Calendar getGroup(int listPosition) {
        return items.get(listPosition).getCalendar();
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // получаем родительский элемент
        Calendar calendar = getGroup(listPosition);

        String dayOfWeek;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.MONDAY):
                dayOfWeek = "Monday";
                break;
            case (Calendar.TUESDAY):
                dayOfWeek = "Tuesday";
                break;
            case (Calendar.WEDNESDAY):
                dayOfWeek = "Wednesday";
                break;
            case (Calendar.THURSDAY):
                dayOfWeek = "Thursday";
                break;
            case (Calendar.FRIDAY):
                dayOfWeek = "Friday";
                break;
            case (Calendar.SATURDAY):
                dayOfWeek = "Saturday";
                break;
            case (Calendar.SUNDAY):
                dayOfWeek = "Sunday";
                break;
            default:
                throw new IllegalStateException("Unexpected value: "
                        + calendar.get(Calendar.DAY_OF_WEEK));
        }

        Locale aLocale;
        String date = DateUtils.formatDateTime(th, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.tasks_history_subitem, null);
        }
        String string = date + "   " + dayOfWeek;
        TextView taskTitle = convertView.findViewById(R.id.listSubitem);
        taskTitle.setText(string);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


}