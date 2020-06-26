package com.example.testtodoapp.home_page.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testtodoapp.R;

public class PriorityAdapter extends ArrayAdapter<String> {
    Context context;
    String[] priorities;
    LayoutInflater inflater;

    public PriorityAdapter(Context context, int textViewRecourseId, String[] priorities) {
        super(context, textViewRecourseId, priorities);
        this.context = context;
        this.priorities = priorities;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        View row = inflater.inflate(R.layout.spinner_item, parent, false);

        TextView label = row.findViewById(R.id.priorityName);
        label.setText(priorities[position]);
        label.setTextSize(18);

        return row;
    }

}
