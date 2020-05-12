package com.example.testtodoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.testtodoapp.ui.home.EditTaskActivity;
import com.example.testtodoapp.ui.home.HomeFragment;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Settings extends  AppCompatActivity {

    public static int globalRemindersTime = 30;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        //EditTextPreference pref = (EditTextPreference)findPreference("reminder_time1");
        //pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        try {
            int value = Math.abs(Integer.parseInt(prefs.getString("reminder_time1", "30")));
            globalRemindersTime = value;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a number value", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Settings.this, MainActivity.class);
        startActivity(homeIntent);
    }
}