package com.example.testtodoapp.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }



    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(homeIntent);
    }
}