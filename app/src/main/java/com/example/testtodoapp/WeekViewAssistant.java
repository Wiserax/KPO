package com.example.testtodoapp;

import android.database.Cursor;

import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekViewAssistant {
    public static List<Object> proc(Cursor cursor) {
        List<Object> objectList = new ArrayList<>();

        if (!(cursor.getCount() == 0)) {

            List<Task> mondayTasksList = new ArrayList<>();
            List<Task> tuesdayTasksList = new ArrayList<>();
            List<Task> wednesdayTasksList = new ArrayList<>();
            List<Task> thursdayTasksList = new ArrayList<>();
            List<Task> fridayTasksList = new ArrayList<>();
            List<Task> saturdayTasksList = new ArrayList<>();
            List<Task> sundayTasksList = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();

            while (cursor.moveToNext()) {
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));

                Task task = MainActivity.dbHandler.getByHashCode(hash);

                calendar.set(task.getYear(),
                        task.getMonthOfYear(),
                        task.getDayOfMonth(),
                        task.getHourOfDay(),
                        task.getHourOfDay());

                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.MONDAY:
                        mondayTasksList.add(task);
                        break;
                    case Calendar.TUESDAY:
                        tuesdayTasksList.add(task);
                        break;
                    case Calendar.WEDNESDAY:
                        wednesdayTasksList.add(task);
                        break;
                    case Calendar.THURSDAY:
                        thursdayTasksList.add(task);
                        break;
                    case Calendar.FRIDAY:
                        fridayTasksList.add(task);
                        break;
                    case Calendar.SATURDAY:
                        saturdayTasksList.add(task);
                        break;
                    case Calendar.SUNDAY:
                        sundayTasksList.add(task);
                        break;
                    default:
                        break;
                }
            }

            String dayOfWeek;
            calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

            for (int i = currentDay; i < 8; i++) {
                switch (i) {
                    case Calendar.MONDAY:
                        if (!mondayTasksList.isEmpty()) {
                            dayOfWeek = "Monday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(mondayTasksList);
                        }
                        break;
                    case Calendar.TUESDAY:
                        if (!tuesdayTasksList.isEmpty()) {
                            dayOfWeek = "Tuesday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(tuesdayTasksList);
                        }
                        break;
                    case Calendar.WEDNESDAY:
                        if (!wednesdayTasksList.isEmpty()) {
                            dayOfWeek = "Wednesday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(wednesdayTasksList);
                        }
                        break;
                    case Calendar.THURSDAY:
                        if (!thursdayTasksList.isEmpty()) {
                            dayOfWeek = "Thursday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(thursdayTasksList);
                        }
                        break;
                    case Calendar.FRIDAY:
                        if (!fridayTasksList.isEmpty()) {
                            dayOfWeek = "Friday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(fridayTasksList);
                        }
                        break;
                    case Calendar.SATURDAY:
                        if (!saturdayTasksList.isEmpty()) {
                            dayOfWeek = "Saturday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(saturdayTasksList);
                        }
                        break;
                    case Calendar.SUNDAY:
                        if (!sundayTasksList.isEmpty()) {
                            dayOfWeek = "Sunday";
                            objectList.add(dayOfWeek);
                            objectList.addAll(sundayTasksList);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return objectList;
    }
}
