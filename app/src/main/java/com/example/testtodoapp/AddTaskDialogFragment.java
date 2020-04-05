package com.example.testtodoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.testtodoapp.basics.Task;

import static android.content.ContentValues.TAG;

public class AddTaskDialogFragment extends DialogFragment {

    public interface AddTaskDialogListener {
        void sendTaskTitle(String taskTitle);
    }

    public AddTaskDialogListener mListener;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layoutA
        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
                .setTitle("Add new task")
                // Add action buttons
                .setPositiveButton("Set task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Task task = new Task();
                        EditText editText = getDialog().findViewById(R.id.taskName);
                        String taskTitle = editText.getText().toString();
                        //task.setTitle(taskTitle);

                        mListener.sendTaskTitle(taskTitle);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (AddTaskDialogListener)getActivity();
        }catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
