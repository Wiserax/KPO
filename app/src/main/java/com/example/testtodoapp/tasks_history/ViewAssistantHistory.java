package com.example.testtodoapp.tasks_history;

import android.database.Cursor;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewAssistantHistory {

    private static HistoryItemStruct getNewHistoryItem(Calendar calendar, Task task) {
        HistoryItemStruct item = new HistoryItemStruct(calendar);
        item.list.add(task);
        return item;
    }

    public static List<HistoryItemStruct> getWeekTaskList(Cursor cursor) {
        List<HistoryItemStruct> itemsList = new ArrayList<>();
        if (!(cursor.getCount() == 0)) {
            int counter = 0;

            while (cursor.moveToPrevious() && (counter < 150)) {
                int flag = cursor.getInt(cursor.getColumnIndex("IS_COMPLETE"));
                if (flag == 1) {
                    int hash = cursor.getInt(cursor.getColumnIndex("HASH_CODE"));
                    Task task = MainActivity.dbHandler.getByHashCode(hash);
                    Calendar calendarFromTask = Calendar.getInstance();

                    calendarFromTask.set(task.getYear(),
                            task.getMonthOfYear(),
                            task.getDayOfMonth());

                    int taskYear = task.getYear();
                    int taskMonth = task.getMonthOfYear();
                    int taskDay = task.getDayOfMonth();

                    if (itemsList.isEmpty()) {
                        itemsList.add(getNewHistoryItem(calendarFromTask, task));
                    } else {
                        boolean findFlag = false;
                        for (HistoryItemStruct el : itemsList) {
                            int year = el.getCalendar().get(Calendar.YEAR);
                            int month = el.getCalendar().get(Calendar.MONTH);
                            int day = el.getCalendar().get(Calendar.DAY_OF_MONTH);
                            if ((year == taskYear)
                                    && (month == taskMonth)
                                    && (day == taskDay)
                            ) {
                                findFlag = true;
                                el.list.add(task);
                                Comparator<Task> comparator = Task::compareTo;
                                Collections.sort(el.list, comparator);
                                break;
                            }
                        }
                        if (!findFlag) {
                            itemsList.add(getNewHistoryItem(calendarFromTask, task));
                        }
                    }
                    counter++;
                }
            }
        }
        cursor.close();

        Comparator<HistoryItemStruct> reverseHistoryItemComparator = (obj1, obj2) -> obj2.compareTo(obj1);
        Collections.sort(itemsList, reverseHistoryItemComparator);
        return itemsList;
    }
}
