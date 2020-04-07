package com.example.testtodoapp.basics;

import java.sql.Time;

public class Task {

    private String title;
    private String description;
    private Priority priority;
    int year, monthOfYear, dayOfMonth;
    int hourOfDay;
    int minute;

    public int getYear() {
        return year;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

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