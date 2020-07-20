package opd.project.y2w.home_page.ListViewsElements.WeeksMode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.y2w.R;
import opd.project.y2w.basics.ItemStruct;
import opd.project.y2w.ItemStructExpListAdapter;
import opd.project.y2w.basics.Task;
import opd.project.y2w.home_page.tasks.EditTaskActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class expListWeeksAdapter extends ItemStructExpListAdapter {
    private Activity activity;
    private LayoutInflater root;

    AtomicBoolean isClicked = new AtomicBoolean(false);

    protected AlphaAnimation fadeIn = new AlphaAnimation(0.6f, 1.0f);
    protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.6f);

    // Hashmap for keeping track of our checkbox check states
    private HashMap<Integer, boolean[]> mChildCheckStates;

    // Our getChildView & getGroupView use the viewholder patter
    // Here are the viewholders defined, the inner classes are
    // at the bottom
    public static final class GroupViewHolder {

        TextView mGroupText;
    }

    public static final class ChildViewHolder {

        TextView mChildTitleText;
        TextView mChildDateText;
        CheckBox mCheckBox;
    }

    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

//    private String groupText;
//    private String childText;

    public expListWeeksAdapter(List<ItemStruct> items, Activity activity) {
        super(items);
        this.activity = activity;
        mChildCheckStates = new HashMap<>();
    }

    public int getNumberOfCheckedItemsInGroup(int mGroupPosition) {
        boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
        int count = 0;
        if (getChecked != null) {
            for (boolean b : getChecked) {
                if (b) count++;
            }
        }
        return count;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {
//        groupText = getGroup(groupPosition);
        Calendar calendar = getGroup(groupPosition);

        String dayOfWeek;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.MONDAY):
                dayOfWeek = "Monday";
                break;
            case (Calendar.TUESDAY):
                dayOfWeek = "Tuesday";
                break;
            case (Calendar.WEDNESDAY):
                dayOfWeek = "Wednesday";
                break;
            case (Calendar.THURSDAY):
                dayOfWeek = "Thursday";
                break;
            case (Calendar.FRIDAY):
                dayOfWeek = "Friday";
                break;
            case (Calendar.SATURDAY):
                dayOfWeek = "Saturday";
                break;
            case (Calendar.SUNDAY):
                dayOfWeek = "Sunday";
                break;
            default:
                throw new IllegalStateException("Unexpected value: "
                        + calendar.get(Calendar.DAY_OF_WEEK));
        }

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.day_of_week, viewGroup, false);

            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.dayOfWeekTitle);

            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        Locale aLocale;
        String date = DateUtils.formatDateTime(activity.getApplicationContext(),
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE);
        String string = date + "   " + dayOfWeek;
        groupViewHolder.mGroupText.setText(string);

        return convertView;
    }

    @Override
    public View getChildView(int listPosition, int expListPosition, boolean isLastChild,
                             View convertView, ViewGroup viewGroup) {

        final int mGroupPosition = listPosition;
        final int mChildPosition = expListPosition;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert inflater != null;
            convertView = inflater.inflate(R.layout.task_item, viewGroup, false);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildTitleText = (TextView) convertView
                    .findViewById(R.id.taskTitle);

            childViewHolder.mChildDateText = (TextView) convertView
                    .findViewById(R.id.taskDate);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.returnBox);

            convertView.setTag(R.layout.task_item, childViewHolder);

        } else {
            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.task_item);
        }


        final Task task = getChild(mGroupPosition, mChildPosition);
        int minutes = task.getMinute();
        String minutesString;
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        childViewHolder.mChildTitleText.setText(task.getTitle().replaceAll("\n", " "));
        if (task.getHourOfDay() == 0 && task.getMinute() == 0) {
            childViewHolder.mChildDateText.setText("");
        } else {
            String formatedHourDay;
            formatedHourDay = task.getHourOfDay() + ":" + minutesString;
            childViewHolder.mChildDateText.setText(formatedHourDay);
        }

        /*
         * You have to set the onCheckChangedListener to null
         * before restoring check states because each call to
         * "setChecked" is accompanied by a call to the
         * onCheckChangedListener
         */
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            /*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
             * the value of the parent view (group) of this child (aka, the key),
             * then retrive the boolean array getChecked[]
             */
            boolean[] getChecked = mChildCheckStates.get(mGroupPosition);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            assert getChecked != null;
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {

            /*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> does not
             * contain the value of the parent view (group) of this child (aka, the key),
             * (aka, the key), then initialize getChecked[] as a new boolean array
             *  and set it's size to the total number of children associated with
             *  the parent group
             */
            boolean[] getChecked = new boolean[getChildrenCount(mGroupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(mGroupPosition, getChecked);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
                assert getChecked != null;
                getChecked[mChildPosition] = isChecked;
                mChildCheckStates.put(mGroupPosition, getChecked);

            } else {

                boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
                assert getChecked != null;
                getChecked[mChildPosition] = isChecked;
                mChildCheckStates.put(mGroupPosition, getChecked);
            }
        });

        fadeIn.setDuration(250);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(250);
        fadeOut.setFillAfter(true);
        //fadeOut.setStartOffset(4200+fadeIn.getStartOffset());


        //Handling CheckBox
//        CheckBox cbBuy = view.findViewById(R.id.returnBox);
//        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
//        cbBuy.setTag(listPosition);
//        cbBuy.setChecked(task.getCompletionStatus());
//
//        cbBuy.setOnClickListener(v -> {
//            isClicked.set(true);
//            if (cbBuy.isChecked()) {
//                HomeFragment.increaseCompletedTasksStatistics();
//                taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                taskTitle.startAnimation(fadeOut);
//                taskDate.startAnimation(fadeOut);
//            } else {
//                HomeFragment.decreaseCompletedTasksStatistics();
//                taskTitle.setPaintFlags(taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//                taskTitle.startAnimation(fadeIn);
//                taskDate.startAnimation(fadeIn);
//                taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
//                taskDate.setTextColor(Color.parseColor("#D4D4D4"));
//            }
//        });


//        final Handler handler = new Handler();
//
//        if (cbBuy.isChecked()) {
//            taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//            if (isClicked.get()) {
//                handler.postDelayed(() -> {
//                    taskTitle.setTextColor(Color.parseColor("#A3D4D4D4"));
//                    taskDate.setTextColor(Color.parseColor("#A3D4D4D4"));
//                }, 250);
//            } else {
//                taskTitle.setTextColor(Color.parseColor("#A3D4D4D4"));
//                taskDate.setTextColor(Color.parseColor("#A3D4D4D4"));
//            }
//        } else {
//            taskTitle.setPaintFlags(taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
//
//            if (isClicked.get()) {
//                handler.postDelayed(() -> {
//                    taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
//                    taskDate.setTextColor(Color.parseColor("#D4D4D4"));
//                }, 250);
//            } else {
//                taskTitle.setTextColor(Color.parseColor("#D4D4D4"));
//                taskDate.setTextColor(Color.parseColor("#D4D4D4"));
//            }
//        }


        //Handling priority color icon
        ImageView imageView = convertView.findViewById(R.id.priorityIconTask);
        int priority = task.getPriority().ordinal();

        if (priority == 0) {
            imageView.setImageResource(R.drawable.priority_high_dark_theme);
        } else if (priority == 1) {
            imageView.setImageResource(R.drawable.priority_med_dark_theme);
        } else if (priority == 2) {
            imageView.setImageResource(R.drawable.priority_low_dark_theme);
        }

//        view.setOnClickListener(v -> {
//            cbBuy.setChecked(!cbBuy.isChecked());
//            task.setCompletionStatus(cbBuy.isChecked());
//            cbBuy.callOnClick();
//
//            MainActivity.dbHandler.editTask(task);
//        });

        convertView.setOnLongClickListener(v -> {
            Intent intent = new Intent(activity, EditTaskActivity.class);
            intent.putExtra("TASK_HASH_CODE", task.getHashKey());
            activity.startActivity(intent);
            return true;
        });
        return convertView;
    }

//    // обработчик для чекбоксов
//    private CompoundButton.OnCheckedChangeListener myCheckChangeList = (buttonView, isChecked) -> {
//        // меняем данные товара (в корзине или нет)
//        Task task = getChild((Integer) buttonView.getTag());
//        task.setCompletionStatus(isChecked);
//        MainActivity.dbHandler.editTask(task);
//    };
}