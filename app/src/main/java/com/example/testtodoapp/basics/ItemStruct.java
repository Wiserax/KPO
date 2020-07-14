package com.example.testtodoapp.basics;

import java.util.ArrayList;
import java.util.Calendar;

public class ItemStruct implements Comparable<ItemStruct> {
    private Calendar calendar;
    public ArrayList<Task> list;

    public ItemStruct(Calendar calendar) {
        this.calendar = Calendar.getInstance();
        this.calendar = calendar;
        this.list = new ArrayList<>();
    }

    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public int compareTo(ItemStruct historyItemStruct) {
        return this.calendar.compareTo(historyItemStruct.calendar);
    }
}
