package com.example.testtodoapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TaskViewModel extends ViewModel {

    private MutableLiveData<ArrayList> mDate = new MutableLiveData<>();
    private MutableLiveData<ArrayList> mTime = new MutableLiveData<>();

    public void setDate(ArrayList date) {

        mDate.setValue(date);
    }

    public LiveData<ArrayList> getDate() {
        return mDate;
    }

    public void setTime(ArrayList time) {
        mTime.setValue(time);
    }

    public LiveData<ArrayList> getTime() {
        return mTime;
    }
}
