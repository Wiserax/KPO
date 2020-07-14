package com.example.testtodoapp.home_page.tasks;

import com.example.testtodoapp.basics.RepeatableTask;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.HomeFragment;

import static com.example.testtodoapp.MainActivity.dbHandler;

public class ServiceRepeatable {
    Task task;

    public boolean handleTask(Task task) {
        // Есть ли метка
        RepeatableTask repeatableTask =
                dbHandler.getRepeatableByParentHash(task.getHashKey());
        if (task.getRepeatableStatus()) {
            // таск выполнен
            if (task.getCompletionStatus()) {
                // дубль (здесь и дальше это репитабл) есть
                if (repeatableTask != null) {
                    // смотрим есть ли ребёнок
                    if (checkForChild(repeatableTask)) { // передаем ребёнку права
                        Task child = dbHandler.getByHashCode(repeatableTask.getChildHash());
                        repeatableTask.setParentHash(child.getHashKey());
                        dbHandler.insertChild(repeatableTask, child);

                        child.setRepeatableStatus(true);
                        dbHandler.editTask(child);

                    } else { // если ребёнка нет то создаем его и передаем права
                        dbHandler.insertChild(repeatableTask, task);
                        Task child = dbHandler.getByHashCode(repeatableTask.getChildHash());
                        repeatableTask.setParentHash(child.getHashKey());
                        dbHandler.insertChild(repeatableTask, child);

                        child.setRepeatableStatus(true);
                        dbHandler.editTask(child);
                    }
                } else { // дубля нет (cкорее всего это абсурдная ситуация)
                    HomeFragment.makeToast("There's probably exist repeatable on next period");
                    return false;
                }
                return true;
            } else { // таск не выполнен
                Task exChild = dbHandler.getChildByDate(task.getDayOfMonth(),
                        task.getMonthOfYear(), task.getYear(), task.getTitle());

                // в случае если дубль есть
                if (repeatableTask != null) {
                    // смотрим есть ли ребёнок
                    if (checkForChild(repeatableTask)) { // ребёнок есть - всё норм
                        return true;
                    } else { // если ребёнка нет то делаем
                        dbHandler.insertChild(repeatableTask, task);
                    }
                } // метка есть, но таск снова активен -> возвращаем права обратно
                else if (exChild != null) {
                    RepeatableTask repeatableTask1 =
                            dbHandler.getRepeatableByParentHash(exChild.getHashKey());
                    dbHandler.killChild(repeatableTask1);
                    repeatableTask1.setParentHash(task.getHashKey());
                    repeatableTask1.setChildHash(exChild.getHashKey());
                    dbHandler.editRepeatable(repeatableTask1);
                    exChild.setRepeatableStatus(false);
                    dbHandler.editTask(exChild);

                } else { // дубля нет, сына нет - какого хуя?
                    repeatableTask = new RepeatableTask();
                    repeatableTask.setPeriod(7); // дефолтный период
                    repeatableTask.setParentHash(task.getHashKey());
                    dbHandler.insertChildAndRepeatable(repeatableTask, task);
                    return true;
                }
            }
        } else { //метки нет но какого-то хуя остался репитабл
            if (repeatableTask != null) {
                // но есть дубль - удаляем всё кроме самого таска
                dbHandler.actionUnsetRepeatable(repeatableTask);
                return true;
            }
        }
        return false;
    }

    public void handleParentDeleting(RepeatableTask repeatableTask) {
        if (repeatableTask != null) {
            // смотрим есть ли ребёнок
            if (checkForChild(repeatableTask)) { // передаем ребёнку права
                Task child = dbHandler.getByHashCode(repeatableTask.getChildHash());
                repeatableTask.setParentHash(child.getHashKey());
                dbHandler.insertChild(repeatableTask, child);

                child.setRepeatableStatus(true);
                dbHandler.editTask(child);

            } else { // если ребёнка нет то создаем его и передаем права
                dbHandler.insertChild(repeatableTask, task);
                Task child = dbHandler.getByHashCode(repeatableTask.getChildHash());
                repeatableTask.setParentHash(child.getHashKey());
                dbHandler.insertChild(repeatableTask, child);

                child.setRepeatableStatus(true);
                dbHandler.editTask(child);
            }
        }
    }

    public boolean checkForChild(RepeatableTask rep) {
        return dbHandler.containsInTasks(rep.getChildHash());
    }
}
