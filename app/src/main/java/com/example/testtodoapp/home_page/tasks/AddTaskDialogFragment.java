package com.example.testtodoapp.home_page.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;
//import com.example.testtodoapp.home_page.HomeFragment;

import java.util.Calendar;
import java.util.Objects;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;
import static android.content.ContentValues.TAG;

public class AddTaskDialogFragment extends DialogFragment {

    public interface AddTaskDialogListener {
        void addEvent(Task task);
    }

    private AddTaskDialogListener mListener;
    private MainActivity mainActivity;
//    private HomeFragment hf;

    public AddTaskDialogFragment(MainActivity mainActivity) {
//        this.hf = hf;
        this.mainActivity = mainActivity;
    }


    // получим текущее время из календаря
    private Calendar dateAndTime = Calendar.getInstance();
    // необходимо для передачи в date/timePicker'ы
    private FragmentActivity faDialog;

    // Создаем экземпляр класс для дальнейшей передачи в HomeFragment, где он будет записан и передан в dayView
    private Task task = new Task();

    @NonNull
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), THEME_DEVICE_DEFAULT_DARK);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        //assert getTag() != null;
        //int flag = Integer.parseInt(getTag());

        final String flag = getTag();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layoutA
        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
                .setTitle("Add new task")
                // Add action buttons
                .setPositiveButton("Set task", (dialog, id) -> {
                    // Запомним активити, для корректного отображения следующих диалоговыъ окон
                    faDialog = getActivity();
                    // Получаем информацию из EditText и устанавливаем имя для нового класса
                    EditText editText = Objects.requireNonNull(getDialog()).findViewById(R.id.taskName);
                    String taskTitle = editText.getText().toString();
                    task.setTitle(taskTitle);
                    task.setDescription("");
                    //Закрываем работу окна выбора имени и приступаем к следующим
                    dialog.dismiss();
                    //Создание диалогового окна выбора даты
                    assert flag != null;
                    if (flag.equals("slow")) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(faDialog, THEME_DEVICE_DEFAULT_DARK, dateSetListener,
                                //получаем текущую дату
                                dateAndTime.get(Calendar.YEAR),
                                dateAndTime.get(Calendar.MONTH),
                                dateAndTime.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    } else {
                        Calendar calendar;
                        if (mainActivity.dailyMod.get()) {
                            calendar = mainActivity.ba.getDeltaCalendar(mainActivity.ba.getActiveDay());
                        } else {
                            calendar = Calendar.getInstance();
                            if (!mainActivity.isCurrentWeek.get()) {
                                int tmp = calendar.get(Calendar.DAY_OF_MONTH);
                                calendar.set(Calendar.DAY_OF_MONTH, tmp + 7);
                            }
                        }

                        task.setYear(calendar.get(Calendar.YEAR));
                        task.setMonthOfYear(calendar.get(Calendar.MONTH));
                        task.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
                        task.setAlarmStatus(false);

                        MainActivity.dbHandler.insertData(task);
                        mainActivity.refreshTable();
                        mainActivity.increaseTasksStatistics();

                        Toast.makeText(faDialog, "Task successfully added", Toast.LENGTH_SHORT).show();
                        //dialog.cancel();
                    }
                })

                // При нажатии на "Cancel" всё отменяется
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // устанавливаем дату
            task.setYear(year);
            task.setMonthOfYear(monthOfYear);
            task.setDayOfMonth(dayOfMonth);

            //отображаем диалог выбора времени
            TimePickerDialog timePickerDialog = new TimePickerDialog(faDialog, THEME_DEVICE_DEFAULT_DARK, timeSetListener,
                    //получаем текущее время
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }
    };


    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // устанавливаем время
            task.setHourOfDay(hourOfDay);
            task.setMinute(minute);

            //Отправляем готовый класс и снова заполняем таблицу с новым элементом

            MainActivity.dbHandler.insertData(task);
            Toast.makeText(faDialog, "Task successfully added", Toast.LENGTH_SHORT).show();
            mListener.addEvent(task);
            mainActivity.refreshTable();
            mainActivity.increaseTasksStatistics();
        }

    };

    // Этот метод необходимо переопределить для передачи данных из
    // текущего диалогового окна в основные активити
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AddTaskDialogListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
