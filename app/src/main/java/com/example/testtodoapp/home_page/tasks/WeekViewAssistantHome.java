package com.example.testtodoapp.home_page.tasks;

import android.database.Cursor;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekViewAssistantHome {
    public static List<WeekTaskStruct> getWeekTaskList(Cursor cursor) {
        List<WeekTaskStruct> taskList = new ArrayList<>();

        if (!(cursor.getCount() == 0)) {

            Calendar calendar = Calendar.getInstance();

            boolean isFirstMon = true;
            boolean isFirstTue = true;
            boolean isFirstWed = true;
            boolean isFirstThu = true;
            boolean isFirstFri = true;
            boolean ifFirstSat = true;
            boolean ifFirstSun = true;

            List<WeekTaskStruct> monList = new ArrayList<>();
            List<WeekTaskStruct> tueList = new ArrayList<>();
            List<WeekTaskStruct> wedList = new ArrayList<>();
            List<WeekTaskStruct> thuList = new ArrayList<>();
            List<WeekTaskStruct> friList = new ArrayList<>();
            List<WeekTaskStruct> satList = new ArrayList<>();
            List<WeekTaskStruct> sunList = new ArrayList<>();

            while (cursor.moveToNext()) {
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                Task task = MainActivity.dbHandler.getByHashCode(hash);

                calendar.set(task.getYear(),
                        task.getMonthOfYear(),
                        task.getDayOfMonth(),
                        task.getHourOfDay(),
                        task.getMinute());

                WeekTaskStruct task_s;
                WeekTaskStruct string_s;
                String dayOfWeek = "";

                task_s = new WeekTaskStruct(task, "");

                int taskDay = calendar.get(Calendar.DAY_OF_WEEK);

                switch (taskDay) {
                    case Calendar.MONDAY:
                        if (isFirstMon) {
                            dayOfWeek = "Monday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            isFirstMon = false;
                            monList.add(string_s);
                        }
                        monList.add(task_s);
                        break;
                    case Calendar.TUESDAY:
                        if (isFirstTue) {
                            dayOfWeek = "Tuesday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            isFirstTue = false;
                            tueList.add(string_s);
                        }
                        tueList.add(task_s);
                        break;
                    case Calendar.WEDNESDAY:
                        if (isFirstWed) {
                            dayOfWeek = "Wednesday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            isFirstWed = false;
                            wedList.add(string_s);
                        }
                        wedList.add(task_s);
                        break;
                    case Calendar.THURSDAY:
                        if (isFirstThu) {
                            dayOfWeek = "Thursday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            isFirstThu = false;
                            thuList.add(string_s);
                        }
                        thuList.add(task_s);
                        break;
                    case Calendar.FRIDAY:
                        if (isFirstFri) {
                            dayOfWeek = "Friday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            isFirstFri = false;
                            friList.add(string_s);
                        }
                        friList.add(task_s);
                        break;
                    case Calendar.SATURDAY:
                        if (ifFirstSat) {
                            dayOfWeek = "Saturday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            ifFirstSat = false;
                            satList.add(string_s);
                        }
                        satList.add(task_s);
                        break;
                    case Calendar.SUNDAY:
                        if (ifFirstSun) {
                            dayOfWeek = "Sunday";
                            string_s = new WeekTaskStruct(null, dayOfWeek);
                            ifFirstSun = false;
                            sunList.add(string_s);
                        }
                        sunList.add(task_s);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + taskDay);
                }
            }

            calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            for (int i = currentDay; i < 8; i++) {
                switch (i) {
                    case (Calendar.MONDAY):
                        taskList.addAll(monList);
                        break;
                    case (Calendar.TUESDAY):
                        taskList.addAll(tueList);
                        break;
                    case (Calendar.WEDNESDAY):
                        taskList.addAll(wedList);
                        break;
                    case (Calendar.THURSDAY):
                        taskList.addAll(thuList);
                        break;
                    case (Calendar.FRIDAY):
                        taskList.addAll(friList);
                        break;
                    case (Calendar.SATURDAY):
                        taskList.addAll(satList);
                        break;
                    case (Calendar.SUNDAY):
                        taskList.addAll(sunList);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + currentDay);
                }
            }

        }
        return taskList;
    }
}
