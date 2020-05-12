package com.example.testtodoapp.home_page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testtodoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BandAdapter extends RecyclerView.Adapter<BandAdapter.DateViewHolder> {
    private final int itemCount;
    private int itemCounter;
    private final Context ctx;
    private final Calendar calendar;
    private Date date;

    public BandAdapter(int itemCount, @NonNull Context ctx, @NonNull LinearLayoutManager llm) {
        this.itemCount = itemCount;
        this.itemCounter = 0;
        this.ctx = ctx;
        this.calendar = Calendar.getInstance();
        this.date = this.calendar.getTime();

        if (itemCount != 0) {
            llm.scrollToPosition(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % itemCount);
        } else {
            llm.scrollToPosition(Integer.MAX_VALUE / 2);
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        int id = R.layout.rv_date_item;

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View v = inflater.inflate(id, parent, false);

        DateViewHolder dvh = new DateViewHolder(v, itemCounter);
        itemCounter++;
        return dvh;
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        if (itemCount == 0) {
            holder.bind("42", "MAY");
        } else {
            calendar.setTime(date);
            calendar.add(Calendar.DATE, position % itemCount);
            Date delta = calendar.getTime();
            SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
            SimpleDateFormat sdf_month = new SimpleDateFormat("MMM");
            holder.bind(sdf_day.format(delta), sdf_month.format(delta));
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        final TextView dayView;
        final TextView monthView;
        int id;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);

            dayView = itemView.findViewById(R.id.rv_date_day);
            monthView = itemView.findViewById(R.id.rv_date_month);
            this.id = 0;
        }

        public DateViewHolder(@NonNull View itemView, int id) {
            this(itemView);
            this.id = id;
        }

        void bind(@NonNull String day, @NonNull String month) {
            dayView.setText(day);
            monthView.setText(month);
        }
    }
}
