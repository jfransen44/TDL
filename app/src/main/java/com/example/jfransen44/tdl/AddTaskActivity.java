package com.example.jfransen44.tdl;

/**
 * Created by JFransen44 on 12/13/15.
 */
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    public EditText taskText;
    public DatePicker date_Picker;
    public TimePicker time_Picker;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        Intent intent = getIntent();

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        taskText = (EditText) findViewById(R.id.taskText);
        date_Picker = (DatePicker) findViewById(R.id.datePicker);
        date_Picker.init(year, month, day, null);
        time_Picker =  (TimePicker) findViewById(R.id.timePicker);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_display_message,
                    container, false);
            return rootView;
        }
    }

    public void getTaskInfo(View v){
        Intent returnIntent = new Intent();
        String taskDate = getMonth() + "-" + getDay() + "-" + getYear();

        String taskTime = convertTime(getHour(), getMinute());

        returnIntent.putExtra("taskString", getTaskString());
        returnIntent.putExtra("dateString", taskDate);
        returnIntent.putExtra("timeString", taskTime);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private String getTaskString(){
        return taskText.getText().toString();
    }

    private String getDay(){
        return Integer.toString(date_Picker.getDayOfMonth());
    }

    private String getMonth(){
        return Integer.toString(date_Picker.getMonth() + 1);
    }

    private String getYear(){
        return Integer.toString(date_Picker.getYear());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private int getHour(){
        return time_Picker.getCurrentHour();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private int getMinute(){
        return time_Picker.getCurrentMinute();
    }

    private String convertTime(int hour, int minute){
        String amPM = "AM";
        String time = "";
        String minutes = "";
        Log.d("tag", Integer.toString(minute));

        if (hour > 12){
            hour -= 12;
            amPM = "PM";
        }
        if (minute < 10){
            minutes = "0" + Integer.toString(minute);
        }
        else if (minute == 0){
            minutes = "00";
        }
        else{
            minutes = Integer.toString(minute);
        }
        time = Integer.toString(hour) + ":" + minutes + " " + amPM;
        return time;
    }
}
