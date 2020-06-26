package com.example.testtodoapp.basics;

public class Task {

    private String title;
    private String description;
    private Priority priority;
    private int year, monthOfYear, dayOfMonth;
    private int hourOfDay;
    private int minute;
    private boolean alarmStatus;
    private long calendarId;

    private int hashKey;

    public boolean getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(boolean alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    private boolean isComplete;

    public Task() {
        hashKey = hashCode();
        alarmStatus = true;
        //priority = Priority.LOW;
        isComplete = false;
    }

    public Task(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        isComplete = false;
        hashKey = hashCode();
        alarmStatus = true;
        //this.priority = priority;
    }

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

    public int getHashKey() {
        return hashKey;
    }

    public String getDescription() {
        return description;
    }

    public boolean getCompletionStatus() {
        return isComplete;
    }

    public void setHashKey(int hashKey) {
        this.hashKey = hashKey;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompletionStatus(boolean complete) {
        isComplete = complete;
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

    public String getTitle() {
        return title;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

}