package com.clockhr;

/**
 * Created by Sagar Pahwa on 14-07-2016.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.clockhr.gpsTracker.GPSTracker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class LoginMainActivity extends Activity {
    String androidId;
    private static int counter =0;
    Button b;
    EditText unamefield;
    EditText pswdfield;
    EditText domainfield;
    ImageButton eye;
    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;
    SharedPreferences pref = null;
    Editor editor;
    ConnectionDetector cd ;
    String username = "";
    String password = "";
    String domain = "";

/*
    TelephonyManager telephonyManager  =  ( TelephonyManager
            )getSystemService( Context.TELEPHONY_SERVICE );
*/

    // Progress Dialog
    private ProgressDialog pDialog = null;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JsonPostParser jsonPostParser = new JsonPostParser();

    // URL to get Login JSON
//    private static String url = "http://mainerp.clockerp.com/webservice/login.php?";
    //private static String url = "http://www.clockhr.com/customer/weblogin/login.php?";
//    private static String url = "http://192.168.1.149/cloud_clock/weblogin/login.php?";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    RelativeLayout relativeLayout;
    GPSTracker gps;
    Boolean Flag = true;
    double lat;
    double lon;

    @Override
    protected void onRestart() {
        super.onRestart();
        relativeLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        relativeLayout = (RelativeLayout)findViewById(R.id.splashLayout);
        relativeLayout.setVisibility(View.GONE);
        context = getApplicationContext();
        activity = this;
        initializeWidgets();



        if(!checkPermission()){
            requestPermission();
        }
        if(checkPermission()) {
            if(locationFinder()) {

                if (pref.getString("flag", "false").equals("true")) {

                    if (pref.getString("username", null) != null) {
                        unamefield.setText(pref.getString("username", null));
                        pswdfield.setText(pref.getString("password", null));
                        domainfield.setText(pref.getString("domain", null));
                        counter = 0;
                        new AttemptLogin().execute();
             /*   Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("domain",pref.getString("domain", null) );
                intent.putExtra("username", pref.getString("username", null));
                intent.putExtra("password", pref.getString("password", null));
                intent.putExtra("type", pref.getString("type",null));
                startActivity(intent);*/
                    }
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please check GPS Location Permission and try again.", Toast.LENGTH_LONG).show();
        }

    }

    private void initializeWidgets() {
        pref = getSharedPreferences("Login_Pref", 0);
        editor = pref.edit();
        b = (Button) findViewById(R.id.button1);
        unamefield = (EditText) findViewById(R.id.Uname);
        pswdfield = (EditText) findViewById(R.id.Password);
        domainfield = (EditText) findViewById(R.id.Domain);
        eye = (ImageButton) findViewById(R.id.imageButton1);
        eye.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        eye.setImageResource(R.drawable.ic_remove_red_eye_black_24dp2);
                        pswdfield.setTransformationMethod(null);
                        pswdfield.setSelection(pswdfield.getText().length());

                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED

                        eye.setImageResource(R.drawable.ic_remove_red_eye_black_24dp1);
                        pswdfield.setTransformationMethod(new PasswordTransformationMethod());

                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {

        } else {

            Toast.makeText(getApplicationContext(), "Please check Internet connection and try again.", Toast.LENGTH_LONG).show();
            finish();
        }
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    if (locationFinder()) {
                        counter = 0;
                        new AttemptLogin().execute();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please check Internet connection and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public boolean locationFinder() {
        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            lat = latitude;
            lon = longitude;
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lon, Toast.LENGTH_LONG).show();
//            editor.putString("latitude", String.valueOf(latitude));
//            editor.putString("longitude", String.valueOf(longitude));
//            editor.putString("tracked", "true");
            //   Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            //   Toast.makeText(getApplicationContext(), "" + distance(latitude, longitude, latitude + 3, longitude + 4), Toast.LENGTH_SHORT).show();
            if(lat==0.0&&lon==0.0){


                Toast.makeText(LoginMainActivity.this, "GPS is not working fine", Toast.LENGTH_SHORT).show();
                return  false;
            }else
                return true;
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            return false;
        }
    }



    @SuppressWarnings("rawtypes")
    class AttemptLogin extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginMainActivity.this);
            pDialog.setMessage("Checking Credentials...");
            //  pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            relativeLayout.setVisibility(View.VISIBLE);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success = 0;
            String type = "",fName = "",lName = "",userid="",adminid="",dbname="",biometricid="",secureid="",profile="";
            username = unamefield.getText().toString();
            password = pswdfield.getText().toString();
            domain = domainfield.getText().toString();
            /*String imeistring = telephonyManager.getDeviceId();
            idView.append("IMEI No : "+imeistring+"\n");
            String imsistring = telephonyManager.getSubscriberId();
            idView.append("IMSI No : "+imsistring+"\n");
            */
            androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            //idView.append( "AndroidID : " + androidId + "\n" );


            if(username.matches("")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        // Toast.makeText(MainActivity.this, "Username cannot be empty", Toast.LENGTH_LONG).show();
                        showalertemptyusername();
                    }});

            }
            else if(password.matches("")){

                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_LONG).show();
                        showalertemptypassword();
                    }});
            }
            else if(domain.matches("")){
                //
                runOnUiThread(new Runnable() {
                    public void run() {
                        // Toast.makeText(getApplicationContext(), "Domain cannot be empty", Toast.LENGTH_LONG).show();
                        showalertemptydomain();
                    }});
            }


            else{

                // showAlert();
                try {

                    String finalurl = null;
                    // Building Parameters
                    List params = new ArrayList();
                    params.add(new BasicNameValuePair("android_id",androidId));
                    params.add(new BasicNameValuePair("domain",domain));
                    params.add(new BasicNameValuePair("username", username));
                    params.add(new BasicNameValuePair("upss", password));
                    JSONObject param = new JSONObject();
                    param.put("android_id",androidId);
                    param.put("domain",domain);
                    param.put("username", username);
                    param.put("upss", password);

                    //List params = new ArrayList();
                    String last = null;
                    String uid=null;
                    String pwd=null;


                    last = domain.substring(0, domain.length());
                    uid=username;
                    pwd=password;

                    //finalurl = UrlSupport.Login_Url+"android_id="+androidId+"&&domain="+last+"&&username="+uid+"&&upss="+pwd;
                    finalurl = UrlSupport.Login_Url1;
                    Log.e("finalurl:",UrlSupport.Login_Url1 + "   "+ param.toString());
                    Log.e("request!", "starting");

                    // getting product details by making HTTP request
                    //JSONObject json = jsonParser.makeHttpRequest(finalurl, "POST", params);
                    //jsonPostParser.sendJson(finalurl,param);
                    //JSONObject json= new JSONObject();

                    //JSONObject json = sendJson(finalurl,param);
                    //
                    JSONObject json = jsonPostParser.sendJson(finalurl,param);
                    if(json!=null){
                        success = json.getInt(TAG_SUCCESS);
                        String jsondata = json.toString();
                        Log.e("json response",jsondata);
                        //Toast.makeText(LoginMainActivity.this, ""+jsondata, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(counter==10){
                            showAlertdomainnonexistance();
                        }
                    }
                    if (success == 1) {
                        type = json.getString("type");
                        fName = json.getString("first_name");
                        lName = json.getString("last_name");
                        userid = json.getString("user_id");
                        adminid = json.getString("ADMIN_ID");
                        dbname = json.getString("db_name");
                        secureid = json.getString("secureid");
                        biometricid = json.getString("biometric_id");
                        profile = json.getString("profile");
                        int punchin = json.getInt("punchin");
                        Log.e("Login done!", json.toString());
                        editor.putInt("punchinstatus",punchin);
                        editor.putString("secureid",secureid);
                        editor.putString("profile",profile);
                        //editor.commit();

                        //if(!pref.getString("flag","false").equals("true")) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("domain", domain);
                        editor.putString("type", type);
                        editor.putString("flag", "true");
                        editor.putString("fName", fName);
                        editor.putString("lName", lName);
                        editor.putString("userid", userid);
                        editor.putString("adminid", adminid);
                        editor.putString("dbname", dbname);
                        editor.putString("biometricid", biometricid);
                        editor.putString("androidId", androidId);
//                            editor.putInt("punchinstatus",0);
                        editor.commit();
                        //}
                        Log.e("in login:",""+pref.getInt("punchinstatus",-2));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("domain", domain);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("type", type);
                        intent.putExtra("secureid",secureid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Flag = false;
                        startActivity(intent);
                        //finish();



                    }else if(success == 0){
                        runOnUiThread(new Runnable() {
                            public void run() {

                                // showalertwrongcredentials();
                                if(counter<20){
                                    counter++;
                                    pDialog.dismiss();
                                    new AttemptLogin().execute();
                                }
                                else {
                                    counter=0;
                                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                                }
                            }});

                    }
                    else{

                        runOnUiThread(new Runnable() {
                            public void run() {
                                // showalertwrongcredentials();
                                Toast.makeText(getApplicationContext(), "Login Failed2", Toast.LENGTH_LONG).show();
                            }});

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                } /*catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/

            }

            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            /*String type = "",fName = "",lName = "",userid="",adminid="",dbname="",biometricid="";
            int success=0;
            try {
                JSONObject json = jsonPostParser.getjson();
                if (json != null) {
                    success = json.getInt(TAG_SUCCESS);
                    String jsondata = json.toString();
                    Log.e("json response", jsondata);
                    //Toast.makeText(LoginMainActivity.this, ""+jsondata, Toast.LENGTH_SHORT).show();
                } else {

                        showAlertdomainnonexistance();
                }
                if (success == 1) {
                    type = json.getString("type");
                    fName = json.getString("first_name");
                    lName = json.getString("last_name");
                    userid = json.getString("user_id");
                    adminid = json.getString("ADMIN_ID");
                    dbname = json.getString("db_name");
                    biometricid = json.getString("biometric_id");
                    Log.e("Login done!", json.toString());
                    if (!pref.getString("flag", "false").equals("true")) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("domain", domain);
                        editor.putString("type", type);
                        editor.putString("flag", "true");
                        editor.putString("fName", fName);
                        editor.putString("lName", lName);
                        editor.putString("userid", userid);
                        editor.putString("adminid", adminid);
                        editor.putString("dbname", dbname);
                        editor.putString("biometricid", biometricid);
                        editor.putString("androidId", androidId);
                        editor.commit();
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        *//*intent.putExtra("domain", domain);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("type", type);
*//*
                    startActivity(intent);
                    //finish();


                } else if (success == 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // showalertwrongcredentials();
                            if(counter<10){
                                counter++;
                                pDialog.dismiss();
                                new AttemptLogin().execute();
                            }
                            else {
                                counter=0;
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            // showalertwrongcredentials();
                            Toast.makeText(getApplicationContext(), "Login Failed2", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                // dismiss the dialog once product deleted
                pDialog.dismiss();
            }catch (Exception e){}
*/
            pDialog.dismiss();
            if(Flag)
                relativeLayout.setVisibility(View.GONE);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){

            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                break;
        }
    }

    public void showAlertdomainnonexistance(){
        if(counter==20) {
            this.runOnUiThread(new Runnable() {
                @SuppressWarnings("deprecation")
                public void run() {
                    if (counter == 20) {
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                LoginMainActivity.this).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Clock");

                        // Setting Dialog Message
                        alertDialog.setMessage("Sorry! Invalid credential ");

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
                    }
                }
            });
        }
    }

    public void showalertemptyusername(){
        LoginMainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        LoginMainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Username field cannot be empty");

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
            }
        });
    }

    public void showalertemptypassword(){

        LoginMainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        LoginMainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Password field cannot be empty");

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
            }
        });
    }

    public void  showalertemptydomain(){

        LoginMainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        LoginMainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Company code  field cannot be empty");

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
            }
        });
    }

    public void showalertimproperformat(){

        LoginMainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        LoginMainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Domain format is improper.Please check");

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
            }
        });

    }

    public void showalertwrongcredentials(){

        LoginMainActivity.this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {

                AlertDialog alertDialog = new AlertDialog.Builder(
                        LoginMainActivity.this).create();

                // Setting Dialog Title
                alertDialog.setTitle("Clock");

                // Setting Dialog Message
                alertDialog.setMessage("Please check login credentials");

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
            }
        });

    }

    protected JSONObject sendJson(final String url, final JSONObject jsn) {
        final InputStream[] is = new InputStream[1];
        String json = null;
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                //JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(url);
                  /*  json.put("email", email);
                    json.put("password", pwd);
                  */  StringEntity se = new StringEntity( jsn.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                   /*Checking response */
                    if(response!=null){
                        is[0] = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    e.printStackTrace();
//                    createDialog("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is[0], "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is[0].close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;
    }
}