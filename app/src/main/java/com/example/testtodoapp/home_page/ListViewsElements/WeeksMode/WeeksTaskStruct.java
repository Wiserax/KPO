package com.example.testtodoapp.home_page.ListViewsElements.WeeksMode;

import com.example.testtodoapp.basics.Task;

import java.util.Calendar;

public class WeeksTaskStruct {
    private Task task;
    private String dayOfWeek;
//    private Calendar calendar;

    public WeeksTaskStruct(Task task, String dayOfWeek/*, Calendar calendar*/) {
        this.task = task;
        this.dayOfWeek = dayOfWeek;
//        this.calendar = Calendar.getInstance();
//        this.calendar = calendar;
    }

    public Task getTask() {
        return this.task;
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }
}
