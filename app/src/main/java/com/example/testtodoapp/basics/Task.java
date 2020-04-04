package com.example.testtodoapp.basics;

import java.sql.Time;

public class Task {

    private String title;
    private String description;
    private Priority priority;

    public Time time;
    private boolean isComplete = false;

    public Task() {
    }

    public Task(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getDescription() {
        return description;
    }

    public void setCompletionStatus(boolean status) {
        isComplete = status;
    }

    public boolean getCompletionStatus() {
        return isComplete;
    }

}