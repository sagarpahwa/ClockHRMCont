package com.clockhr.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clockhr.JsonPostParser;
import com.clockhr.R;
import com.clockhr.UrlSupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceDetailsFragment extends DialogFragment {


    public AttendanceDetailsFragment() {
        // Required empty public constructor
    }

    private TextView textViewdate;

    // Progress Dialog
    private ProgressDialog pDialog = null;
    private static final String SUCCESS_TAG = "success";
    String biometricId;
    String secureid;
    String myValue;
    String domain;
    private CardView cardViewValue;
    private CardView cardViewNoValue;
    int status;
    private String NotSucess;
    String NoSucessData = "No Punch In";
    private String total_working_hours,shift,from,to,grace_time,half_day,success,InTime,OutTime,completed_Working_hours;
    private TextView textViewtotal_working_hours,textViewshift,textViewfrom,textViewto,textViewgrace_time,textViewhalf_day,textViewInTime,textViewOutTime,textViewcompleted_Working_hours;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance_details3, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        cardViewValue = (CardView)view.findViewById(R.id.ValueCard);
        cardViewNoValue = (CardView)view.findViewById(R.id.NoValueCard);
        textViewdate = (TextView) view.findViewById(R.id.datetext);
        /*textView Init*/
        textViewtotal_working_hours = (TextView)view.findViewById(R.id.total_working_hours);
        textViewshift = (TextView)view.findViewById(R.id.shift);
        textViewfrom = (TextView)view.findViewById(R.id.from);
        textViewto = (TextView)view.findViewById(R.id.to);
        textViewgrace_time = (TextView)view.findViewById(R.id.grace_time);
        textViewhalf_day = (TextView)view.findViewById(R.id.half_day);
        textViewInTime = (TextView)view.findViewById(R.id.InTime);
        textViewOutTime = (TextView)view.findViewById(R.id.OutTime);
        textViewcompleted_Working_hours = (TextView)view.findViewById(R.id.completed_Working_hours);

        Bundle mArgs = getArguments();
        myValue = mArgs.getString("date");
//        textViewdate.setText(myValue);
        Log.e("dateformat",myValue);
        SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref",0);
        biometricId = pref.getString("biometricid",null);
        domain = pref.getString("domain", null);
        secureid = pref.getString("secureid",null);
        new PunchInPunchOutDetail().execute();
        return view;
    }
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        // request a window without the title
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        return dialog;
//    }

    @SuppressWarnings("rawtypes")
    class PunchInPunchOutDetail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Connecting...");
            //  pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            JSONObject sendjson = new JSONObject();
            try {
                sendjson.put("date1", myValue);
                sendjson.put("biometric_id", biometricId);
                sendjson.put("secure_id",secureid);
                sendjson.put("domain",domain);

                JSONObject recievejson = null;
                Log.e("Attendance_detail", UrlSupport.Attendance_Detail_Url + "" + sendjson.toString());
                recievejson = new JsonPostParser().sendJson(UrlSupport.Attendance_Detail_Url,sendjson);

                if(recievejson!=null){

                    if(recievejson.getInt(SUCCESS_TAG)==1){
                        Log.e("attendance respose: ", recievejson.toString());
                        try {
                             status = recievejson.getInt("status");
                            total_working_hours = recievejson.get("total_working_hours").toString();
                            shift = recievejson.get("shift").toString();
                            from = recievejson.get("from").toString();
                            to = recievejson.get("to").toString();
                            grace_time = recievejson.get("grace_time").toString();
                            half_day = recievejson.get("half_day").toString();
                            InTime = recievejson.get("InTime").toString();
                            OutTime = recievejson.get("OutTime").toString();
                            completed_Working_hours = recievejson.get("completed_Working_hours").toString();


//                            Log.e("Total Data",status+total_working_hours+shift+from+to+grace_time+half_day+InTime+OutTime+completed_Working_hours);






                        }catch (Exception e){
                            Log.e("inner error: ", ""+e);


                        }
//
//
                    }
                    else if (recievejson.getInt(SUCCESS_TAG)==0){
                        try {
                             status = recievejson.getInt("status");
                            NotSucess = recievejson.get("message").toString();


                        }catch (Exception e){

                        }
//                        Toast.makeText(getActivity(), "Failed.\nTry Again.", Toast.LENGTH_SHORT).show();

                    }
                }





            }catch (JSONException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           if (status==1){
               cardViewValue.setVisibility(View.VISIBLE);
               cardViewNoValue.setVisibility(View.GONE);
           }else if (status==0){
               cardViewNoValue.setVisibility(View.VISIBLE);
               cardViewValue.setVisibility(View.GONE);
           }
            textViewtotal_working_hours.setText(total_working_hours);
            textViewshift.setText(shift);
            textViewto.setText(to);
            textViewfrom.setText(from);
            textViewInTime.setText(InTime);
            textViewOutTime.setText(OutTime);
            textViewgrace_time.setText(grace_time);
            textViewhalf_day.setText(half_day);
            textViewcompleted_Working_hours.setText(completed_Working_hours);
            textViewdate.setText(NoSucessData);
            pDialog.dismiss();
        }
    }

}
