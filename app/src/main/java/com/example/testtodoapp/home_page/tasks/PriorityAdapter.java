package com.example.testtodoapp.home_page.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testtodoapp.R;

public class PriorityAdapter extends ArrayAdapter<String> {
    private String[] priorities;
    private LayoutInflater inflater;
    private boolean isPeriodAdapter;

    PriorityAdapter(Context context, int textViewRecourseId, String[] priorities, boolean isPeriodAdapter) {
        super(context, textViewRecourseId, priorities);
        this.priorities = priorities;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isPeriodAdapter = isPeriodAdapter;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, parent);
    }

    private View getCustomView(int position,
                               ViewGroup parent) {
        View root = inflater.inflate(R.layout.spinner_item, parent, false);

        TextView label = root.findViewById(R.id.priorityName);
        label.setText(priorities[position]);
        label.setTextSize(18);

        if (!isPeriodAdapter) {
            ImageView imageView = root.findViewById(R.id.priorityIcon);
            if (position == 0) {
                imageView.setImageResource(R.drawable.priority_high_dark_theme);
            } else if (position == 1) {
                imageView.setImageResource(R.drawable.priority_med_dark_theme);
            } else if (position == 2) {
                imageView.setImageResource(R.drawable.priority_low_dark_theme);
            }
        }

        return root;
    }

}
