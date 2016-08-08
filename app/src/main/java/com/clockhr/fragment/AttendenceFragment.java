package com.clockhr.fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import com.clockhr.JSONParser;
import com.clockhr.JsonPostParser;
import com.clockhr.R;
import com.clockhr.UrlSupport;
import com.clockhr.attendeceCalender.CalendarView;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class AttendenceFragment extends Fragment {
    private static final String TAG_SUCCESS = "success";
    private ProgressDialog pDialog;
    private Calendar cal = Calendar.getInstance();
    private ArrayList<Date> events = new ArrayList<>();
    private ArrayList<String> eventType = new ArrayList<>();
    private CalendarView cv;
    int month=0,year=0;
    public AttendenceFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_attendence, container, false);
        month = 1 + cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        cv = ((CalendarView) view.findViewById(R.id.calendar_view));
        cv.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onPreviousButtonClick() {
                if(month>1)month--;
                else{month=12;year--;}
                eventType.clear();
                events.clear();
                new GetAttendence().execute();
            }
            @Override
            public void onNextButtonClick() {
                if(month<12)month++;
                else{month=1;year++;}
                eventType.clear();
                events.clear();
                new GetAttendence().execute();
            }
            @Override
            public void onDayLongPress(Date date) {
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDayPressed(Date date) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                Toast.makeText(getActivity(), df.format(date), Toast.LENGTH_SHORT).show();
                FragmentManager manager = getFragmentManager();
                AttendanceDetailsFragment attendanceDetailsFragment =  new AttendanceDetailsFragment();

                // Supply num input as an argument.
                Bundle args = new Bundle();
                args.putString("date",df.format(date) );
                attendanceDetailsFragment.setArguments(args);

                attendanceDetailsFragment.show(manager,"Show");
            }
        });
        new GetAttendence().execute();
        return view;
    }
    class GetAttendence extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;
        SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref", 0);
        SharedPreferences.Editor editor = pref.edit();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching Attendence...");
            //  pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }
        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... args) {
            JSONObject sendjson = new JSONObject();
            try {
                sendjson.put("domain", pref.getString("domain",null));
                sendjson.put("username", pref.getString("username",null));
                sendjson.put("secure_id",pref.getString("secureid",null));
                String mmonth;
                if(month<10)
                    mmonth = "0"+month;
                else
                    mmonth = ""+month;
                String date = year + "-" + mmonth;
                sendjson.put("date1",date);
                Log.e("sending Api Query :", UrlSupport.Attendence_Url+sendjson.toString());
                JSONObject recievejson = null;
                recievejson = new JsonPostParser().sendJson(UrlSupport.Attendence_Url,sendjson);
                Log.e("respose is:", " "+recievejson);
                JSONArray jsonArray = null;
                if (recievejson!= null) {
                    jsonArray = recievejson.getJSONArray("attendence");
                    String jsondata = recievejson.toString();
                    Log.e("json response", jsondata);
                }
                if (jsonArray != null) {
                    int d = 0;
                    String status;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        //StringTokenizer day = new StringTokenizer(obj.getString("date"), "-");
                        d = obj.getInt("date");
                        status = obj.getString("status");
                        /*while (day.hasMoreTokens()) {
                            d = Integer.valueOf(day.nextToken());
                        }
                        */
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month - 1);
                        cal.set(Calendar.DAY_OF_MONTH, d);
                        /*cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        */
                        events.add(i,cal.getTime());
                        eventType.add(i,status);
                        Log.e("day: ",i+"  "+   d+eventType.get(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
        protected void onPostExecute(String file_url) {
            cv.updateCalendar(events, eventType);
            // dismiss the dialog once product deleted
            pDialog.dismiss();
        }
    }
}