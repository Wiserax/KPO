package com.example.testtodoapp.settings;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }

        Button reminderTimeButton = findViewById(R.id.buttonReminderTime);
        reminderTimeButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, THEME_DEVICE_DEFAULT_DARK);
            //alert.setView()
            builder.setMessage("Enter minutes before reminder");
            builder.setTitle("Minutes before reminder");

            EditText editText = new EditText(this);
            builder.setView(editText);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });


        Button manageAccountButton = findViewById(R.id.buttonManageAccount);
        manageAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
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
        Intent homeIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(homeIntent);
    }
}