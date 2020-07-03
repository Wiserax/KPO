package com.example.testtodoapp.home_page;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private final HomeFragment hf;
    private final Calendar calendar;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf_day = new SimpleDateFormat("dd");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf_month = new SimpleDateFormat("MMM");

    private Date today;
    private int activeDay;

    public Date getPresentDay() {
        return this.today;
    }

    public Calendar getDeltaCalendar(int delta) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, delta);
        return calendar;
    }

    public int getActiveDay() {
        return this.activeDay;
    }

    BandAdapter(int itemCount, @NonNull HomeFragment hf, @NonNull LinearLayoutManager llm) {
        this.itemCount = itemCount;
        this.itemCounter = 0;
        this.hf = hf;
        this.calendar = Calendar.getInstance();
        this.today = this.calendar.getTime();
        this.activeDay = 0;

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
    public void onBindViewHolder(@NonNull final DateViewHolder holder, final int position) {
        if (itemCount == 0) {
            holder.bind(new Date(), false);
        } else {
            int day = position % itemCount;

            if (day == 0) {
                holder.dayView.setTextColor(0xff10AFC5);
                holder.monthView.setTextColor(0xff10AFC5);
            } else {
                holder.dayView.setTextColor(0xffD4D4D4);
                holder.monthView.setTextColor(0xffD4D4D4);
            }

            calendar.setTime(today);
            calendar.add(Calendar.DATE, day);
            Date delta = calendar.getTime();

            holder.bind(delta, day == activeDay);
            holder.frame.setOnClickListener(view -> {
                hf.refreshTable(holder);

                activeDay = position % itemCount;
                notifyItemRangeChanged(position - itemCount, itemCount * 2);
            });
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout frame;
        final TextView dayView;
        final TextView monthView;
        final Calendar calendar;
        int id;

        DateViewHolder(@NonNull View itemView) {
            super(itemView);
            frame = itemView.findViewById(R.id.rv_frame);
            dayView = itemView.findViewById(R.id.rv_date_day);
            monthView = itemView.findViewById(R.id.rv_date_month);
            calendar = Calendar.getInstance();
            this.id = 0;
        }

        DateViewHolder(@NonNull View itemView, int id) {
            this(itemView);
            this.id = id;
        }

        void bind(@NonNull Date date, boolean active) {
            if (active) {
                frame.setBackgroundColor(0xFF767B91);
            } else {
                frame.setBackgroundColor(0x00000000);
            }
            this.calendar.setTime(date);
            dayView.setText(sdf_day.format(date));
            monthView.setText(sdf_month.format(date));
        }
    }
}
