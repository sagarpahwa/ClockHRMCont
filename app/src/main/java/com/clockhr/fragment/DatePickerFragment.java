package com.clockhr.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.clockhr.ApplyLeaveForm;
import com.clockhr.R;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        if((ApplyLeaveForm.Caller_id==1)&&(!ApplyLeaveForm.from.getText().toString().equals("Start"))){
            c.set(Calendar.YEAR,ApplyLeaveForm.mYear);
            c.set(Calendar.MONTH,ApplyLeaveForm.mMonth);
            c.set(Calendar.DAY_OF_MONTH,ApplyLeaveForm.mDate);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar thatDay = Calendar.getInstance();
        thatDay.set(Calendar.DAY_OF_MONTH,day);
        thatDay.set(Calendar.MONTH,month); // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, year);
        Calendar today = Calendar.getInstance();
        long diff = thatDay.getTimeInMillis() - today.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        if (days>=0) {
            if (ApplyLeaveForm.Caller_id == 0) {
                ApplyLeaveForm.mDate = day;
                ApplyLeaveForm.mMonth = month;
                ApplyLeaveForm.mYear = year;
            } else {
                ApplyLeaveForm.m2Date = day;
                ApplyLeaveForm.m2Month = month;
                ApplyLeaveForm.m2Year = year;
            }
        }else {
            ApplyLeaveForm.Caller_id = 2;
        }
        ApplyLeaveForm.setDate();
        // Do something with the date chosen by the user
    }
}