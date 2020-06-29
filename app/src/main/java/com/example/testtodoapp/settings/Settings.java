package com.example.testtodoapp.settings;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;

public class Settings extends AppCompatActivity {

    public SharedPreferences minutesBeforeReminder;
    private static final String PREFS_NAME = "MINUTES_BEFORE_REMINDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Application application;


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }


        Button reminderTimeButton = findViewById(R.id.buttonReminderTime);
        reminderTimeButton.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK);
            minutesBeforeReminder = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int currentTime  = minutesBeforeReminder.getInt("reminder_time", 0);
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



        Button manageAccountButton = findViewById(R.id.buttonManageAccount);
        manageAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.this, SignInActivity.class);
            startActivity(intent);
        });

        Button aboutButton = findViewById(R.id.buttonAbout);
        aboutButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Наше приложение тоП!!1!", Toast.LENGTH_SHORT).show();
        });

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Settings.this, MainActivity.class);
        startActivity(homeIntent);
    }
}