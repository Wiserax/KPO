package com.example.testtodoapp.home_page.ListViewsElements.WeeksMode;

import com.example.testtodoapp.basics.Task;

public class WeeksTaskStruct {
    private Task task;
    private String dayOfWeek;

    public WeeksTaskStruct(Task task, String dayOfWeek) {
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
