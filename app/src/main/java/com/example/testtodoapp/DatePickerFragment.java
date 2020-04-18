package com.example.testtodoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private TaskViewModel taskViewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        taskViewModel = ViewModelProviders.of(requireActivity()).get(TaskViewModel.class);

        // В пикере изначально показывается текущий год, месяц, день
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                //Некоторые дефолтные темы
//                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
//                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
//                AlertDialog.THEME_HOLO_LIGHT,
//                AlertDialog.THEME_HOLO_DARK,
//                AlertDialog.THEME_TRADITIONAL,
                this, year, month, day);

        // кастомный тайтл над календарем begin
        TextView textView = new TextView(getActivity());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        // разметка для тайтла программно, а не через xml
        textView.setLayoutParams(layoutParams);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setText("This is a custom title.");
        textView.setTextColor(Color.parseColor("#ff0000"));
        textView.setBackgroundColor(Color.parseColor("#FFD2DAA7"));

        datePickerDialog.setTitle("Chose date for your task");
        // кастомный тайтл над календарем end

//         Create a new instance of DatePickerDialog and return it
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        ArrayList<Integer> date = new ArrayList<Integer>(3);
        date.add(year);
        date.add(month);
        date.add(dayOfMonth);
        taskViewModel.setDate(date);

        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }
}
