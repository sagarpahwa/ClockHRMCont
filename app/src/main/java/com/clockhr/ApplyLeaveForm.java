package com.clockhr;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.clockhr.adapter.ViewPagerAdapter;
import com.clockhr.attendeceCalender.CalendarView;
import com.clockhr.fragment.ApplyLeaveFragment;
import com.clockhr.fragment.DatePickerFragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ApplyLeaveForm extends AppCompatActivity {
    private ProgressDialog pDialog;
    public static int mYear=0, mMonth=0, mDate = 0;
    public static int m2Year=0, m2Month=0, m2Date = 0;
    public static int Caller_id = 0;
    private static Activity activity;
    public static TextView from;
    private static TextView  to, remaining, dayCount;
    private EditText reason;
    private String range,leavereason,halfday,message;
    private static CheckBox isHalf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Apply Leave");
        setContentView(R.layout.activity_apply_leave_form);
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void initialize() {
        activity = ApplyLeaveForm.this;
        from = (TextView)findViewById(R.id.fromdate);
        to = (TextView)findViewById(R.id.ToDate);
        remaining = (TextView)findViewById(R.id.remainingLeave);
        dayCount = (TextView)findViewById(R.id.daysCount);
        reason = (EditText)findViewById(R.id.reason);
        isHalf = (CheckBox)findViewById(R.id.isHalfDay);
        reason.setText("");
        reason.addTextChangedListener(new TextWatcher() {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                params.weight = 0.0f;
                reason.setLayoutParams(params);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                params.weight = 1.0f;
                reason.setLayoutParams(params);
            }
        });
        remaining.setText("Remaining "+ ApplyLeaveFragment.leaveApplicable[ApplyLeaveFragment.selectedTabPosition]);
    }
    public void setFrom (View view){
        Caller_id = 0;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void setTo (View view){
        Caller_id = 1;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public void isHalf (View view){
        if(isHalf.isChecked()){
            Log.e("Half Day ","true");
        }
    }
    public void applyAsync (View view){
        if(from.getText().equals("Start")){
            AlertDialog alertDialog = new AlertDialog.Builder(
                    ApplyLeaveForm.this).create();

            // Setting Dialog Title
            alertDialog.setTitle("Clock");

            // Setting Dialog Message
            alertDialog.setMessage("Choose Start Date.");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.cross);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });

            // Showing Alert Message
            alertDialog.show();
//            Toast.makeText(ApplyLeaveForm.this, "Choose Start Date.", Toast.LENGTH_SHORT).show();
        }
        else {
            if (reason.getText().toString().equals("")) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        ApplyLeaveForm.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Reason Can't be Empty.");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.cross);

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                // Showing Alert Message
                alertDialog.show();
//                Toast.makeText(ApplyLeaveForm.this, "Reason Can't be Empty.", Toast.LENGTH_SHORT).show();
            } else {
                if (isHalf.isChecked()) {
                    halfday = "1";
                } else halfday = "0";
                if (to.getText().equals("End")) {
                    to.setText(from.getText());
                }
                range = from.getText() + "-" + to.getText();
                leavereason = reason.getText().toString();
                new ApplyLeave().execute();
            }
        }
    }
    public static void setDate(){
        if(Caller_id==0){
            from.setText(mYear+"/"+mMonth+"/"+mDate);
        }else if(Caller_id==1){
            to.setText(m2Year+"/"+m2Month+"/"+m2Date);
        }else if(Caller_id==2){

            AlertDialog alertDialog = new AlertDialog.Builder(
                    activity).create();

            // Setting Dialog Title
            alertDialog.setTitle("Clock");

            // Setting Dialog Message
            alertDialog.setMessage("Can't Select Past Date");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.cross);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });

            // Showing Alert Message
            alertDialog.show();

//            Toast.makeText(activity, "Can't Select Past Date", Toast.LENGTH_SHORT).show();
        }
        if(mDate!=0&&mYear!=0&&mMonth!=0&&m2Date!=0&&m2Year!=0&&m2Month!=0){
            Calendar today = Calendar.getInstance();
            today.set(Calendar.DAY_OF_MONTH,mDate);
            today.set(Calendar.MONTH,mMonth); // 0-11 so 1 less
            today.set(Calendar.YEAR, mYear);

            Calendar thatDay = Calendar.getInstance();
            thatDay.set(Calendar.DAY_OF_MONTH,m2Date);
            thatDay.set(Calendar.MONTH,m2Month); // 0-11 so 1 less
            thatDay.set(Calendar.YEAR, m2Year);
            long diff;
            long days ;
            if(thatDay.getTimeInMillis() >= today.getTimeInMillis()) {
                diff = thatDay.getTimeInMillis() - today.getTimeInMillis(); //result in millis
                days = diff / (24 * 60 * 60 * 1000);
            }
            else{
                diff = -1;
                days = -1;
            }

            Log.e("days ",diff+" "+days+"");
            if(days==0){
                isHalf.setVisibility(View.VISIBLE);
                dayCount.setText("Days Count "+(days+1));
            }else if((days < 0)||(days>=ApplyLeaveFragment.leaveApplicable[ApplyLeaveFragment.selectedTabPosition])){
                isHalf.setVisibility(View.VISIBLE);
                dayCount.setText("Days Count 1");
                m2Date=m2Month=m2Year=0;
                to.setText("End");
                if(days<0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            activity).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Clock");

                    // Setting Dialog Message
                    alertDialog.setMessage("Can't make reverse selection");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.cross);

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();
//                    Toast.makeText(activity, "Can't make reverse selection", Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            activity).create();

                    // Setting Dialog Title
                    alertDialog.setTitle("Clock");

                    // Setting Dialog Message
                    alertDialog.setMessage("Can't apply more than Remaining Leave");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.cross);

                    // Setting OK Button
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

//                    Toast.makeText(activity,"Can't apply more than Remaining Leave",Toast.LENGTH_LONG).show();
                }
                isHalf.setVisibility(View.VISIBLE);
                dayCount.setText("Days Count 1");
                to.setText("End");
            }else if(days>0){
                isHalf.setVisibility(View.GONE);
                dayCount.setText("Days Count "+(days+1));
            }
        }else if(mDate!=0&&mYear!=0&&mMonth!=0&&to.getText().equals("End")){
            dayCount.setText("Days Count 1");

        }
    }

    class ApplyLeave extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ApplyLeaveForm.this);
            pDialog.setMessage("Applying Leave...");
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();

        }
        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... args) {
            SharedPreferences pref = getSharedPreferences("Login_Pref", 0);
            JSONObject sendjson = new JSONObject();
            try {
                sendjson.put("domain", pref.getString("domain",null));
                sendjson.put("username", pref.getString("username",null));
                sendjson.put("secure_id",pref.getString("secureid",null));
                sendjson.put("date1",range);
                sendjson.put("half_day",halfday);
                sendjson.put("reason",leavereason);
                sendjson.put("leave_id",ApplyLeaveFragment.selectedTabPosition+1);
                Log.e("sending Api Query :", UrlSupport.APPLY_LEAVE+sendjson.toString());
                JSONObject recievejson = null;
                recievejson = new JsonPostParser().sendJson(UrlSupport.APPLY_LEAVE,sendjson);
                Log.e("respose is:", " "+recievejson);
                if (recievejson!= null) {
                    int success = 0;

                    if(recievejson.has("success")){
                        success = recievejson.getInt("success");
                    }
                    message = recievejson.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            Toast.makeText(ApplyLeaveForm.this, message + "", Toast.LENGTH_SHORT).show();
            Log.e("message",message);
            if (message.equals("Leave applied successfully")){
                finish();
            }else {
                Toast.makeText(ApplyLeaveForm.this, message + "", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
        }
    }
}