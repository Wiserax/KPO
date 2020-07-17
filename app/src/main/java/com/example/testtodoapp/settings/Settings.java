package com.example.testtodoapp.settings;

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

import com.example.testtodoapp.R;
import com.varunest.sparkbutton.SparkButton;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;

public class Settings extends AppCompatActivity {

    Vibrator vibrator;

    public SharedPreferences minutesBeforeReminder;
    private static final String PREFS_NAME = "MINUTES_BEFORE_REMINDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

        SparkButton reminderTimeButton = findViewById(R.id.buttonReminderTime);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        reminderTimeButton.setOnClickListener(v -> {

            vibrator.vibrate(50);
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

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        manageAccountButton.setOnClickListener(v -> {
            vibrator.vibrate(50);
            Intent intent = new Intent(Settings.this, SignInActivity.class);
            startActivity(intent);
        });

        SparkButton aboutButton = findViewById(R.id.buttonAbout);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        aboutButton.setOnClickListener(v -> {
            vibrator.vibrate(50);
            Toast.makeText(getApplicationContext(), "Над приложением работал \n"+
                    "Вьетнамский разработчик Ягон Дон", Toast.LENGTH_SHORT).show();
        });


        SparkButton vibrationButton = findViewById(R.id.buttonVibration);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        vibrationButton.setOnClickListener(v ->{
            vibrator.vibrate(3000);
        });

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}