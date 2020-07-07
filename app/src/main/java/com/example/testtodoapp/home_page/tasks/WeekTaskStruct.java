package com.example.testtodoapp.home_page.tasks;

import com.example.testtodoapp.basics.Task;

public class WeekTaskStruct {
    private Task task;
    private String dayOfWeek;

    public WeekTaskStruct(Task task, String dayOfWeek) {
        this.task = task;
        this.dayOfWeek = dayOfWeek;
    }

    public Task getTask() {
        return this.task;
    }

    public String getDayOfWeek() {
        return this.dayOfWeek;
    }
}
