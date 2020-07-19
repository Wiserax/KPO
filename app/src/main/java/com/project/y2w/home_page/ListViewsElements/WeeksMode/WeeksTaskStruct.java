package com.project.y2w.home_page.ListViewsElements.WeeksMode;

import com.project.y2w.basics.Task;

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
