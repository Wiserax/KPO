package com.project.y2w.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.project.y2w.R;
import com.varunest.sparkbutton.SparkButton;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;

public class Settings extends AppCompatActivity {

    Vibrator vibrator;

    public SharedPreferences minutesBeforeReminder;
    private static final String PREFS_NAME = "MINUTES_BEFORE_REMINDER";

    AtomicBoolean vibroStatus = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        SharedPreferences vibrationPrefs = getSharedPreferences("VIBRATION_PREFERENCES", MODE_PRIVATE);
        vibroStatus.set(vibrationPrefs.getBoolean("vibration_status", true));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

        SparkButton reminderTimeButton = findViewById(R.id.buttonReminderTime);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        reminderTimeButton.setOnClickListener(v -> {
            if (vibroStatus.get())
                vibrator.vibrate(40);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK);
            minutesBeforeReminder = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int currentTime = minutesBeforeReminder.getInt("reminder_time", 30);
            builder.setMessage("Current reminder time: " + currentTime);

            // Получаем контекст в котором создаем диалог
            View dialogView = LayoutInflater.from(this)
                    .inflate(R.layout.alert_reminder_time, null);

            // Создание самого диалога
            builder.setView(dialogView)
                    .setTitle("Minutes before reminder")
                    .setPositiveButton("Confirm", (dialog, which) -> {

                        EditText editText = dialogView.findViewById(R.id.minutesBefore);
                        int minutes = Integer.parseInt(editText.getText().toString());

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putInt("reminder_time", minutes);
                        editor.apply();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    })
                    .show();
        });

        SparkButton manageAccountButton = findViewById(R.id.buttonManageAccount);

        manageAccountButton.setOnClickListener(v -> {
            if (vibroStatus.get())
            vibrator.vibrate(40);
            Intent intent = new Intent(Settings.this, SignInActivity.class);
            startActivity(intent);
        });

        SparkButton aboutButton = findViewById(R.id.buttonAbout);

        aboutButton.setOnClickListener(v -> {
            if (vibroStatus.get())
            vibrator.vibrate(40);
            Toast.makeText(getApplicationContext(), "Y2W is a stylish and powerful day planner \n"+
                    "for those who value their time!", Toast.LENGTH_SHORT).show();
        });



        SparkButton vibrationButtonOn = findViewById(R.id.buttonVibrationOn);

        SparkButton vibrationButtonOff = findViewById(R.id.buttonVibrationOff);


        if (vibroStatus.get()) {
            vibrationButtonOn.setVisibility(View.VISIBLE);
            vibrationButtonOff.setVisibility(View.INVISIBLE);
        } else {
            vibrationButtonOn.setVisibility(View.INVISIBLE);
            vibrationButtonOff.setVisibility(View.VISIBLE);
        }

        vibrationButtonOn.setOnClickListener(v -> {

            SharedPreferences.Editor editor = vibrationPrefs.edit();

            vibrator.vibrate(40);
            vibroStatus.set(false);
            editor.putBoolean("vibration_status", false);
            editor.apply();

            vibrationButtonOn.setVisibility(View.INVISIBLE);
            vibrationButtonOff.setVisibility(View.VISIBLE);
        });

        vibrationButtonOff.setOnClickListener(v -> {

            SharedPreferences.Editor editor = vibrationPrefs.edit();

            vibroStatus.set(true);
            editor.putBoolean("vibration_status", true);
            editor.apply();

            vibrationButtonOn.setVisibility(View.VISIBLE);
            vibrationButtonOff.setVisibility(View.INVISIBLE);
        });

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}