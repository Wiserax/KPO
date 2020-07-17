package com.example.testtodoapp.home_page.ListViewsElements.WeeksMode;

import android.database.Cursor;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeeksViewAssistantHome {
    public static List<WeeksTaskStruct> getWeekTaskList(Cursor cursor, AtomicBoolean isCurrentWeek) {
        List<WeeksTaskStruct> taskList = new ArrayList<>();

        if (!(cursor.getCount() == 0)) {

            Calendar calendar = Calendar.getInstance();


            boolean isFirstMon = true;
            boolean isFirstTue = true;
            boolean isFirstWed = true;
            boolean isFirstThu = true;
            boolean isFirstFri = true;
            boolean ifFirstSat = true;
            boolean ifFirstSun = true;

            List<WeeksTaskStruct> monList = new ArrayList<>();
            List<WeeksTaskStruct> tueList = new ArrayList<>();
            List<WeeksTaskStruct> wedList = new ArrayList<>();
            List<WeeksTaskStruct> thuList = new ArrayList<>();
            List<WeeksTaskStruct> friList = new ArrayList<>();
            List<WeeksTaskStruct> satList = new ArrayList<>();
            List<WeeksTaskStruct> sunList = new ArrayList<>();


            boolean isFirstTask = true;
//            cursor.moveToFirst();
            Calendar firstDay = Calendar.getInstance();
//            int firstHash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
//            Task firstTask = MainActivity.dbHandler.getByHashCode(firstHash);

//            firstDayOfWeek.set(firstTask.getYear(), firstTask.getMonthOfYear(),
//                    firstTask.getDayOfMonth(),
//                    firstTask.getHourOfDay(),
//                    firstTask.getMinute());


            while (cursor.moveToNext()) {
                int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                Task task = MainActivity.dbHandler.getByHashCode(hash);

                if (isFirstTask) {
                    firstDay.set(task.getYear(), task.getMonthOfYear(),
                            task.getDayOfMonth(),
                            task.getHourOfDay(),
                            task.getMinute());

                    isFirstTask = false;

                }

                calendar.set(task.getYear(),
                        task.getMonthOfYear(),
                        task.getDayOfMonth(),
                        task.getHourOfDay(),
                        task.getMinute());

                WeeksTaskStruct task_s;
                WeeksTaskStruct string_s;
                String dayOfWeek = "";


                task_s = new WeeksTaskStruct(task, "");

                int taskDay = calendar.get(Calendar.DAY_OF_WEEK);

                switch (taskDay) {
                    case Calendar.MONDAY:
                        if (isFirstMon) {
                            dayOfWeek = "Monday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            isFirstMon = false;
                            monList.add(string_s);
                        }
                        monList.add(task_s);
                        break;
                    case Calendar.TUESDAY:
                        if (isFirstTue) {
                            dayOfWeek = "Tuesday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            isFirstTue = false;
                            tueList.add(string_s);
                        }
                        tueList.add(task_s);
                        break;
                    case Calendar.WEDNESDAY:
                        if (isFirstWed) {
                            dayOfWeek = "Wednesday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            isFirstWed = false;
                            wedList.add(string_s);
                        }
                        wedList.add(task_s);
                        break;
                    case Calendar.THURSDAY:
                        if (isFirstThu) {
                            dayOfWeek = "Thursday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            isFirstThu = false;
                            thuList.add(string_s);
                        }
                        thuList.add(task_s);
                        break;
                    case Calendar.FRIDAY:
                        if (isFirstFri) {
                            dayOfWeek = "Friday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            isFirstFri = false;
                            friList.add(string_s);
                        }
                        friList.add(task_s);
                        break;
                    case Calendar.SATURDAY:
                        if (ifFirstSat) {
                            dayOfWeek = "Saturday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            ifFirstSat = false;
                            satList.add(string_s);
                        }
                        satList.add(task_s);
                        break;
                    case Calendar.SUNDAY:
                        if (ifFirstSun) {
                            dayOfWeek = "Sunday";
                            string_s = new WeeksTaskStruct(null, dayOfWeek);
                            ifFirstSun = false;
                            sunList.add(string_s);
                        }
                        sunList.add(task_s);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + taskDay);
                }
            }
            cursor.close();


            int loopLimit = 8;

//            int currentDay = firstDay.getFirstDayOfWeek();

//            if (isCurrentWeek.get()) {
//                loopLimit = loopLimit - currentDay + 1;
//                currentDay = firstDay.get(Calendar.DAY_OF_WEEK);
//            }

//            calendar = Calendar.getInstance();
//            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
//            for (int i = currentDay; i < loopLimit; i++) {
            for (int i = 1; i < 8; i++) {
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
                        throw new IllegalStateException("Unexpected value: " + i);
                }
            }

        }
        return taskList;
    }
}
