package com.example.testtodoapp.home_page.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Priority;
import com.example.testtodoapp.basics.RepeatableTask;
import com.example.testtodoapp.basics.Task;
import com.example.testtodoapp.db.CalendarHandler;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;
import static com.example.testtodoapp.MainActivity.dbHandler;

public class EditTaskActivity extends AppCompatActivity {

    TextView titleText;
    TextView descrText;
    Button saveButton;

    Button dateButton;
    Button timeButton;
    CheckBox cbAlarm;

    Task task;
    Calendar dateAndTime = Calendar.getInstance();

    boolean isKeyboardShowing = false;
    boolean isTaskChanged = false;
    boolean isChild = false;
    boolean isFastTask = false;

    int period = 7;


    AtomicBoolean isRepeatChanged = new AtomicBoolean(false);

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);



        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //Получили хэш код и создали новый таск
        final int taskHashCode = getIntent().getIntExtra("TASK_HASH_CODE", 0);

        task = MainActivity.dbHandler.getByHashCode(taskHashCode);

        titleText = findViewById(R.id.taskTitle);

        String titleString = task.getTitle().replaceAll("\n", " ");
        titleText.setText(titleString);

        //Слудующие два метода снова делают активным мерцающий курсор
        titleText.setOnClickListener(v -> {
            titleText.setCursorVisible(true);
            isTaskChanged = true;
        });

        titleText.setOnEditorActionListener((v, actionId, event) -> {
            titleText.setCursorVisible(true);
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert in != null;
                in.hideSoftInputFromWindow(descrText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        });

        // Блок поля taskDescription
        descrText = findViewById(R.id.taskDes);
        descrText.setText(task.getDescription());


        //Слудующие два метода снова делают активным мерцающий курсор
        descrText.setOnClickListener(v -> {
            descrText.setCursorVisible(true);
            isTaskChanged = true;
        });

        descrText.setOnEditorActionListener((v, actionId, event) -> {
            descrText.setCursorVisible(false);
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert in != null;
                in.hideSoftInputFromWindow(descrText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
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

        if (task.getHourOfDay() != 0 && task.getMinute() != 0) {
            timeButton.setText(hoursString + ":" + minutesString);
        } else {
            timeButton.setText("-");
        }

        dateButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditTaskActivity.this, THEME_DEVICE_DEFAULT_DARK, d,
                    //получаем текущую дату
                    dateAndTime.get(Calendar.YEAR),
                    dateAndTime.get(Calendar.MONTH),
                    dateAndTime.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            isTaskChanged = true;
        });

        timeButton.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(EditTaskActivity.this, THEME_DEVICE_DEFAULT_DARK, t,
                    //получаем текущее время
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true);
            timePickerDialog.show();
            isTaskChanged = true;
        });


        // адаптер
        String[] priorities = {"High", "Medium", "Low"};

        PriorityAdapter adapter = new PriorityAdapter(EditTaskActivity.this,
                R.layout.spinner_item, priorities, false);

        Spinner spinner = findViewById(R.id.spinnerTaskPriority);
        spinner.setAdapter(adapter);


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

        spinner.setOnTouchListener((v, event) -> {
            isTaskChanged = true;
            return false;
        });

        String[] periods = {"After day", "After 2 days", "After 3 days", "After 4 days", "After 5 days", "After 6 days", "After week"};

        PriorityAdapter adapter2 = new PriorityAdapter(EditTaskActivity.this,
                R.layout.spinner_item, periods, true);

        Spinner spinner2 = findViewById(R.id.spinnerTaskPeriod);
        spinner2.setAdapter(adapter2);

        // selection
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                period = position + 1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinner2.setOnTouchListener((v, event) -> {
            isTaskChanged = true;
            return false;
        });


        CheckBox cbRepeat = findViewById(R.id.repeatableCheckBox);
        cbRepeat.setTag(2);
        cbRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            RepeatableTask repeatableTaskChildRole =
                    MainActivity.dbHandler.getRepeatableByChildHash(task.getHashKey());

            if (repeatableTaskChildRole != null || task.getCompletionStatus()) {
                isChecked = false;
                buttonView.setChecked(false);
                Toast.makeText(getBaseContext(), "This task will automatically become repeatable after completing the previous one", Toast.LENGTH_SHORT).show();
            }

            if (isChecked) spinner2.setVisibility(View.VISIBLE);
            else spinner2.setVisibility(View.INVISIBLE);

            task.setRepeatableStatus(isChecked);
        });

        cbRepeat.setOnTouchListener((v, event) -> {
            isRepeatChanged.set(true);
            isTaskChanged = true;
            return false;
        });
        cbRepeat.setChecked(task.getRepeatableStatus());

        spinner2.setSelection(getPeriod(task));

        if (cbRepeat.isChecked()) spinner2.setVisibility(View.VISIBLE);
        else spinner2.setVisibility(View.INVISIBLE);



        //CheckBoxes
        cbAlarm = findViewById(R.id.alarmCheckBox);
        cbAlarm.setTag(1); // рандомный тэг для нашего чекбокса, по факту неважно ведь он у нас один
        cbAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setAlarmStatus(isChecked);
            isTaskChanged = true;

            if (task.getMinute() == 0 && task.getHourOfDay() == 0) isFastTask = true;
        });

        cbAlarm.setOnTouchListener((v, event) -> {
            isTaskChanged = true;
            return false;
        });

        cbAlarm.setChecked(task.getAlarmStatus());

        //Ставим лиснер который определяет открыта клава или нет
        findViewById(android.R.id.content).getRootView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    Rect r = new Rect();
                    findViewById(android.R.id.content).getRootView().getWindowVisibleDisplayFrame(r);
                    int screenHeight = findViewById(android.R.id.content).getRootView().getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) isKeyboardShowing = true;
                    } else {
                        // keyboard is closed
                        if (isKeyboardShowing) isKeyboardShowing = false;
                    }
                });
    }

    private int getPeriod(Task task) {
        if (!task.getRepeatableStatus()) {
            return 6;
        }

        RepeatableTask tmp = dbHandler.getRepeatableByParentHash(task.getHashKey());
        if (tmp != null) {
            return (tmp.getPeriod() - 1);
        } else {
            return 6;
        }
    }

    @Override
    public void onBackPressed() {
        if (isTaskChanged) {
            task.setTitle(titleText.getText().toString());
            task.setDescription(descrText.getText().toString());

            CalendarHandler calendarHandler = new CalendarHandler();
            if (!isFastTask) {
                calendarHandler.editEvent(task);
            } else {
                if (task.getAlarmStatus()) {
                    if (task.getHourOfDay() != 0 && task.getMinute() != 0) {
                        calendarHandler.addEvent(task);
                    } else {
                        Toast.makeText(EditTaskActivity.this, "Please enter hour and minutes to set alarm field", Toast.LENGTH_SHORT).show();
                        task.setAlarmStatus(false);
                        cbAlarm.setChecked(false);
                        return;
                    }
                }
            }
            MainActivity.dbHandler.editTask(task);


            RepeatableTask repeatableTaskParentRole =
                    MainActivity.dbHandler.getRepeatableByParentHash(task.getHashKey());
            RepeatableTask repeatableTaskChildRole =
                    MainActivity.dbHandler.getRepeatableByChildHash(task.getHashKey());

            if (repeatableTaskChildRole != null) isChild = true;

            if (task.getRepeatableStatus()) {
                if (repeatableTaskParentRole == null) {
                    if (!isChild) {
                        repeatableTaskParentRole = new RepeatableTask();
                        repeatableTaskParentRole.setParentHash(task.getHashKey());
                        repeatableTaskParentRole.setPeriod(period);
                        MainActivity.dbHandler.insertChildAndRepeatable(repeatableTaskParentRole, task);
                    }
                } else {
                    repeatableTaskParentRole.setPeriod(period);
                    MainActivity.dbHandler.editRepeatable(repeatableTaskParentRole);

                    // если дубль уже есть, то меняем поля и у наследника
                    Task child = MainActivity.dbHandler.getByHashCode(repeatableTaskParentRole.getChildHash());
                    child.setTitle(task.getTitle());
                    child.setHourOfDay(task.getHourOfDay());
                    child.setMinute(task.getMinute());

                    Calendar c = Calendar.getInstance();
                    c.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth());
                    c.add(Calendar.DATE, repeatableTaskParentRole.getPeriod());
                    child.setYear(c.get(Calendar.YEAR));
                    child.setMonthOfYear(c.get(Calendar.MONTH));
                    child.setDayOfMonth(c.get(Calendar.DAY_OF_MONTH));
                    MainActivity.dbHandler.editTask(child);
                    calendarHandler.editEvent(child);
                }
            } else {
                if (repeatableTaskParentRole != null) {
                    if (!isChild) {
                        if (repeatableTaskParentRole.getParentHash() == task.getHashKey()) // если родитель, значит метку сняли
                            // и значит удаляем дубль
                            MainActivity.dbHandler.actionUnsetRepeatable(repeatableTaskParentRole);
                    }
                }
            }

            // !isChild
            if (repeatableTaskChildRole != null) {
                // если дубль уже есть, то меняем поля и у наследника
                Task parent = MainActivity.dbHandler.getByHashCode(repeatableTaskChildRole.getParentHash());
                parent.setTitle(task.getTitle());
                parent.setHourOfDay(task.getHourOfDay());
                parent.setMinute(task.getMinute());

                Calendar c = Calendar.getInstance();
                c.set(task.getYear(), task.getMonthOfYear(), task.getDayOfMonth());
                c.add(Calendar.DATE, -(repeatableTaskChildRole.getPeriod()));
                parent.setYear(c.get(Calendar.YEAR));
                parent.setMonthOfYear(c.get(Calendar.MONTH));
                parent.setDayOfMonth(c.get(Calendar.DAY_OF_MONTH));
                MainActivity.dbHandler.editTask(parent);
                calendarHandler.editEvent(parent);
            }

            isRepeatChanged.set(false);


            isTaskChanged = false;
        }
        super.onBackPressed();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    public boolean onSupportNavigateUp() {
        if (isKeyboardShowing) hideSoftKeyboard(EditTaskActivity.this);
        onBackPressed();
        return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        RepeatableTask repeatableTaskChildRole =
                MainActivity.dbHandler.getRepeatableByChildHash(task.getHashKey());
        isChild = repeatableTaskChildRole != null;

        RepeatableTask repeatableTaskParentRole =
                MainActivity.dbHandler.getRepeatableByParentHash(task.getHashKey());

        if (item.getItemId() == R.id.deleteMenuButton) {
            if (repeatableTaskChildRole != null || repeatableTaskParentRole != null) {
                if (isChild) {
                    MainActivity.dbHandler.deleteItem(task);
                    CalendarHandler calendarHandler = new CalendarHandler();
                    calendarHandler.deleteEvent(task);
                } else {
                    MainActivity.dbHandler.deleteRepeatableCompletely(
                            MainActivity.dbHandler.getRepeatableByParentHash(
                                    task.getHashKey()
                            )
                    );
                }
            } else {
                MainActivity.dbHandler.deleteItem(task);
                CalendarHandler calendarHandler = new CalendarHandler();
                calendarHandler.deleteEvent(task);
            }

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
