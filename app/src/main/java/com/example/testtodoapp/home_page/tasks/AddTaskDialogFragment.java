package com.example.testtodoapp.home_page.tasks;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class AddTaskDialogFragment extends DialogFragment {

    public interface AddTaskDialogListener {
        void refreshTable();
        void addEvent(Task task);
    }

    public AddTaskDialogListener mListener;

    // получим текущее время из календаря
    Calendar dateAndTime = Calendar.getInstance();
    // необходимо для передачи в date/timePicker'ы
    FragmentActivity faDialog;

    // Создаем экземпляр класс для дальнейшей передачи в HomeFragment, где он будет записан и передан в dayView
    Task task = new Task();

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //assert getTag() != null;
        //int flag = Integer.parseInt(getTag());

        final String flag = getTag();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layoutA
        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
                .setTitle("Add new task")
                // Add action buttons
                .setPositiveButton("Set task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Запомним активити, для корректного отображения следующих диалоговыъ окон
                        faDialog = getActivity();
                        // Получаем информацию из EditText и устанавливаем имя для нового класса
                        EditText editText = getDialog().findViewById(R.id.taskName);
                        String taskTitle = editText.getText().toString();
                        task.setTitle(taskTitle);

                        //Закрываем работу окна выбора имени и приступаем к следующим
                        dialog.dismiss();
                        //Создание диалогового окна выбора даты
                        if (flag.equals("slow")) {
                            new DatePickerDialog(faDialog, iluhaShluha,
                                    //получаем текущую дату
                                    dateAndTime.get(Calendar.YEAR),
                                    dateAndTime.get(Calendar.MONTH),
                                    dateAndTime.get(Calendar.DAY_OF_MONTH))
                                    .show();
                        } else {
                            MainActivity.dbHandler.insertData(task);
                            mListener.refreshTable();
                            Toast.makeText(faDialog, "Task successfully added", Toast.LENGTH_SHORT).show();
                            //dialog.cancel();
                        }
                    }
                })

                // При нажатии на "Cancel" всё отменяется
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

        DatePickerDialog.OnDateSetListener iluhaShluha = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // устанавливаем дату
            task.setYear(year);
            task.setMonthOfYear(monthOfYear);
            task.setDayOfMonth(dayOfMonth);

            //отображаем диалог выбора времени
            new TimePickerDialog(faDialog, t,
                    //получаем текущее время
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true)
                    .show();
        }
    };



    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // устанавливаем время
            task.setHourOfDay(hourOfDay);
            task.setMinute(minute);

            //Отправляем готовый класс и снова заполняем таблицу с новым элементом

            MainActivity.dbHandler.insertData(task);
            Toast.makeText(faDialog, "Task successfully added", Toast.LENGTH_SHORT).show();
            mListener.addEvent(task);
            mListener.refreshTable();
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
