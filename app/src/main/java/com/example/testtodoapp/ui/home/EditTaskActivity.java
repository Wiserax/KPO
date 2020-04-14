package com.example.testtodoapp.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testtodoapp.MainActivity;
import com.example.testtodoapp.R;
import com.example.testtodoapp.basics.Task;

public class EditTaskActivity extends AppCompatActivity {

    TextView titleText;
    TextView descrText;
    Button saveButton;
    Button cancelButton;

    Task editedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        //Получили хэш код и создали новый таск
        final int taskHashCode = getIntent().getIntExtra("TASK_HASH_CODE", 0);

        editedTask = MainActivity.dbHandler.getByHashCode(taskHashCode);

        titleText = findViewById(R.id.titleText);
        titleText.setText(editedTask.getTitle());

        descrText = findViewById(R.id.descrText);
        descrText.setText(editedTask.getDescription());

        saveButton = findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editedTask.setTitle(titleText.getText().toString());
                editedTask.setDescription(descrText.getText().toString());
                MainActivity.dbHandler.editTask(editedTask);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        cancelButton = findViewById(R.id.cancelTaskButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
