package opd.project.y2w.tasks_history;

import android.database.Cursor;

import opd.project.y2w.MainActivity;
import opd.project.y2w.basics.ItemStruct;
import opd.project.y2w.basics.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemListConstructor {

    private static ItemStruct getNewItem(Calendar calendar, Task task) {
        ItemStruct item = new ItemStruct(calendar);
        item.list.add(task);
        return item;
    }
    
    private static boolean updateCondition(Cursor cursor,
                                           boolean isCursorDirectionReversed, int counterLimit,
                                           int counter) {
        boolean condition;
        if (isCursorDirectionReversed) {
            condition = (cursor.moveToPrevious() && (counter < counterLimit));
        } else {
            condition = (cursor.moveToNext());
        }
        return condition;
    }

    public static List<ItemStruct> getTaskList(Cursor cursor, boolean isCursorDirectionReversed,
                                               int counterLimit) {
        int counter = 0;

        boolean condition = updateCondition(cursor, isCursorDirectionReversed, counterLimit, counter);

        List<ItemStruct> itemsList = new ArrayList<>();
        if (!(cursor.getCount() == 0)) {
            while (condition) {
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
                        itemsList.add(getNewItem(calendarFromTask, task));
                    } else {
                        boolean findFlag = false;
                        for (ItemStruct el : itemsList) {
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
                            itemsList.add(getNewItem(calendarFromTask, task));
                        }
                    }
                    counter++;
                }
                condition = updateCondition(cursor, isCursorDirectionReversed, counterLimit, counter);
            }
            Comparator<ItemStruct> reverseHistoryItemComparator = (obj1, obj2) -> obj2.compareTo(obj1);
            Collections.sort(itemsList, reverseHistoryItemComparator);
        }
        return itemsList;
    }
}
