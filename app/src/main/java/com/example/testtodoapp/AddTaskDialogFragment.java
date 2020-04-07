package com.example.testtodoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class AddTaskDialogFragment extends DialogFragment {

    public interface AddTaskDialogListener {
        void sendTaskTitle(String taskTitle);
    }
    TextView currentDateTime;

    Calendar dateAndTime=Calendar.getInstance();
    public AddTaskDialogListener mListener;
    private List<Task> itemList;

    FragmentActivity fragmentActivity;

    Task task = new Task();


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        //currentDateTime = (TextView) getActivity().findViewById(R.id.currentDateTime);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layoutA
        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
                .setTitle("Add new task")
                // Add action buttons
                .setPositiveButton("Set task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = getDialog().findViewById(R.id.taskName);
                        String taskTitle = editText.getText().toString();
                        task.setTitle(taskTitle);

                        //костыль пиздец

                        dialog.dismiss();
                        fragmentActivity = getActivity();

                        new DatePickerDialog(fragmentActivity, d,
                                dateAndTime.get(Calendar.YEAR),
                                dateAndTime.get(Calendar.MONTH),
                                dateAndTime.get(Calendar.DAY_OF_MONTH))
                                .show();


                        MainActivity.taskList1.add(task);
                        mListener.sendTaskTitle(taskTitle);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }


    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
/*            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);*/
            task.setYear(year);
            task.setMonthOfYear(monthOfYear);
            task.setDayOfMonth(dayOfMonth);

            new TimePickerDialog(fragmentActivity, t,
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true)
                    .show();
        }
    };

    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            /*dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);*/
            task.setHourOfDay(hourOfDay);
            task.setMinute(minute);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AddTaskDialogListener)getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    public void sendTaskList(List<Task> inputList) {
        itemList = inputList;
    }
}
