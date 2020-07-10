package com.example.testtodoapp.tasks_history;

import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryItemStruct implements Comparable<HistoryItemStruct> {
    private Calendar calendar;
    public ArrayList<Task> list;

    public HistoryItemStruct(Calendar calendar) {
        this.calendar = Calendar.getInstance();
        this.calendar = calendar;
        this.list = new ArrayList<>();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public int compareTo(HistoryItemStruct historyItemStruct) {
        return this.calendar.compareTo(historyItemStruct.calendar);
    }
}
