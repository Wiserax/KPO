package com.example.testtodoapp.home_page.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

import java.util.HashMap;
import java.util.List;

public class TasksHistoryAdapter extends BaseExpandableListAdapter{

    private Context context;
    private Activity home;
    private List<String> expListTitle;
    private HashMap<String, Task> expListDetail;

    public TasksHistoryAdapter(Context context, List<String> expListTitle,
                               HashMap<String, Task> expListDetail) {
        this.context = context;
        this.expListTitle = expListTitle;
        this.expListDetail = expListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expListPosition) {
        return expListDetail.get(expListTitle.get(listPosition));
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // получаем дочерний элемент
        Task task = (Task) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.tasks_history_subitem, null);
        }
        TextView description = (TextView) convertView.findViewById(R.id.listSubitem);
        description.setText(task.getDescription());

        TextView dataView = (TextView) convertView.findViewById(R.id.listSubitemTaskDate);
        if ((task.getHourOfDay() != 0) && (task.getMinute() != 0)) {
            String dataString = task.getHourOfDay() + ":" + task.getMinute();
            dataView.setText(dataString);
        }
        else {
            String dataString = "-";
            dataView.setText(dataString);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.listSubitemPriorityIconTask);
        int priority = task.getPriority().ordinal();

        if (priority == 0) {
            imageView.setImageResource(R.drawable.priority_high_dark_theme);
        } else if (priority == 1) {
            imageView.setImageResource(R.drawable.priority_med_dark_theme);
        } else if (priority == 2) {
            imageView.setImageResource(R.drawable.priority_low_dark_theme);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return expListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return expListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        // получаем родительский элемент
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.tasks_history_item, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
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