package com.example.testtodoapp.home_page.tasks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import com.example.testtodoapp.db.CalendarHandler;
import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Priority;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.home_page.HomeFragment;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    TextView titleText;
    TextView descrText;
    Button saveButton;
    Button cancelButton;

    Button dateButton;
    Button timeButton;

    Task task;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //Получили хэш код и создали новый таск
        final int taskHashCode = getIntent().getIntExtra("TASK_HASH_CODE", 0);

        task = MainActivity.dbHandler.getByHashCode(taskHashCode);

        titleText = findViewById(R.id.taskTitle);

        String titleString = task.getTitle().replaceAll("\n", " ");
        titleText.setText(titleString);

        //Слудующие два метода снова делают активным мерцающий курсор
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descrText.setCursorVisible(true);
            }
        });

        titleText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                descrText.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(descrText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        // Блок поля taskDescription
        descrText = findViewById(R.id.taskDes);
        descrText.setText(task.getDescription());


        //Слудующие два метода снова делают активным мерцающий курсор
        descrText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descrText.setCursorVisible(true);
            }
        });

        descrText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                descrText.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(descrText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


        // Кнопки Даты и Времени
        dateButton = findViewById(R.id.buttonChangeDate);
        timeButton = findViewById(R.id.buttonChangeTime);

        String minutesString = getNumeric(task.getMinute());
        String monthsString = getNumeric(task.getMonthOfYear() + 1);
        String dayString = getNumeric(task.getDayOfMonth());
        String hoursString = getNumeric(task.getHourOfDay());

        if (task.getMonthOfYear() != 0 && task.getYear() != 0 ) {
            dateButton.setText(dayString + "." + monthsString + "." + task.getYear());
            timeButton.setText(hoursString + ":" + minutesString);
        } else {
            dateButton.setText("-");
            timeButton.setText("-");
        }

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditTaskActivity.this, d,
                        //получаем текущую дату
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EditTaskActivity.this, t,
                        //получаем текущее время
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE), true)
                        .show();
            }
        });


        // адаптер
        String[] data = {"High", "Medium", "Low"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = findViewById(R.id.spinnerTaskPriority);
        spinner.setAdapter(adapter);
        // заголовок
        //spinner.setPrompt("Priority");
        // выделяем элемент
        spinner.setSelection(task.getPriority().ordinal());
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                Priority priority = Priority.values()[position]; // cast int to Enum
                task.setPriority(priority);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        saveButton = findViewById(R.id.markAsCompleteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setTitle(titleText.getText().toString());
                task.setDescription(descrText.getText().toString());
                MainActivity.dbHandler.editTask(task);
                CalendarHandler calendarHandler = new CalendarHandler();
                calendarHandler.editEvent(task);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        CheckBox cbBuy = findViewById(R.id.alarmCheckBox);

        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        cbBuy.setTag(1); // рандомный тэг для нашего чекбокса, по факту неважно ведь он у нас один

        cbBuy.setChecked(task.getAlarmStatus());
    }

    public String getNumeric(int val) {
        String valString;
        if (val < 10) {
            valString = "0" + val;
        } else {
            valString = String.valueOf(val);
        }
        return valString;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            task.setAlarmStatus(isChecked);
            //MainActivity.dbHandler.editTask(task);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.deleteMenuButton) {
            MainActivity.dbHandler.deleteItem(task);
            CalendarHandler calendarHandler = new CalendarHandler();
            calendarHandler.deleteEvent(task);

            HomeFragment homeFragment = new HomeFragment();
            homeFragment.populateTable();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // устанавливаем дату
            task.setYear(year);
            task.setMonthOfYear(monthOfYear);
            task.setDayOfMonth(dayOfMonth);
            String monthsString = getNumeric(task.getMonthOfYear() + 1);
            String dayString = getNumeric(task.getDayOfMonth());
            dateButton.setText(dayString + "." + monthsString + "." + task.getYear());
        }
    };


    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // устанавливаем время
            task.setHourOfDay(hourOfDay);
            task.setMinute(minute);

            String minutesString = getNumeric(task.getMinute());
            String hoursString = getNumeric(task.getHourOfDay());
            timeButton.setText(hoursString +  ":" + minutesString);
        }
    };
}