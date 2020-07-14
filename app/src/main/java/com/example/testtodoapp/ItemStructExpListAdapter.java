package com.example.testtodoapp;

import android.app.Activity;
import android.widget.BaseExpandableListAdapter;

import com.example.testtodoapp.basics.ItemStruct;
import com.example.testtodoapp.basics.Task;

import java.util.Calendar;
import java.util.List;

public abstract class ItemStructExpListAdapter extends BaseExpandableListAdapter {

    private Activity home;
    private List<ItemStruct> items;

    public ItemStructExpListAdapter(List<ItemStruct> items) {
        this.items = items;
    }

    @Override
    public Task getChild(int listPosition, int expandedListPosition) {
        return items.get(listPosition).list.get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return items.get(listPosition).list.size();
    }

    @Override
    public Calendar getGroup(int listPosition) {
        return items.get(listPosition).getCalendar();
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


}