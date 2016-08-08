package com.clockhr.fragment;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

import com.clockhr.JsonPostParser;
import com.clockhr.R;
import com.clockhr.UrlSupport;
import com.clockhr.gpsTracker.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Handler;

import android.location.Address;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClockInFragment extends Fragment implements OnMapReadyCallback{
    // Google Map
    private AlertDialog alert = null;
    private GoogleMap googleMap;
    private static final String SUCCESS_TAG = "success";
    private Button buttonPunchIn ,buttonPunchOut;
    private com.clockhr.DigitalClock dc;
    private TextView currentdate;
    GPSTracker gps;
    double lat;
    double lon;
    String punchdate;
    String punchtime;
    String formattedDate,formattedDate2;


    // Progress Dialog
    private ProgressDialog pDialog = null;
    JsonPostParser jsonPostParser = new JsonPostParser();


    public ClockInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clock_in, container, false);
        locationFinder();
        SupportMapFragment mapFragment = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

     /*Digital Clock*/
        dc = (com.clockhr.DigitalClock)view.findViewById(R.id.digitalClock1);
        android.text.format.DateFormat.is24HourFormat(getActivity());

        //what can i do with DigitalClock also? for display only
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        dc.setTypeface(face3);

        TextView myAddress = (TextView)view.findViewById(R.id.address);

        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Segoe_UI.ttf");
        myAddress.setTypeface(face);
       /*current date*/
        currentdate = (TextView)view.findViewById(R.id.datecurrent);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("MMM dd");
        formattedDate = df.format(c.getTime());
        formattedDate2 = df2.format(c.getTime());
        currentdate.setText(formattedDate2);

        Typeface face1= Typeface.createFromAsset(getActivity().getAssets(), "fonts/Segoe_UI.ttf");
        currentdate.setTypeface(face1);



        /*Punch In Button*/

        SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref", 0);

        punchtime = dc.getText().toString();

        punchdate = formattedDate;
        buttonPunchIn = (Button)view.findViewById(R.id.punchInButton);
        buttonPunchOut = (Button)view.findViewById(R.id.punchOutButton);

        buttonPunchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PunchIn().execute();
            }
        });
        buttonPunchOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PunchIn().execute();
            }
        });

        if(pref.getInt("punchinstatus",0)==1){
            buttonPunchIn.setVisibility(View.GONE);
            buttonPunchOut.setVisibility(View.VISIBLE);
        }
        else{
            buttonPunchIn.setVisibility(View.VISIBLE);
            buttonPunchOut.setVisibility(View.GONE);
        }

        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                myAddress.setText(strReturnedAddress.toString());
            }
            else{
                myAddress.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myAddress.setText("Canont get Address!");
        }
        return view;
    }

   /* @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationFinder();
        SupportMapFragment mapFragment = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

    }
*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

// adding marker
        googleMap.addMarker(marker);
        // ROSE color icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lon)).zoom(13).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
//
    }


    @SuppressWarnings("rawtypes")
    class PunchIn extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Connecting...");
            //  pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }
        @SuppressWarnings("unchecked")

        @Override
        protected String doInBackground(String... strings) {

            SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref", 0);
            SharedPreferences.Editor edit = pref.edit();
            JSONObject sendjson = new JSONObject();
            try {
                sendjson.put("domain", pref.getString("domain",null));
                sendjson.put("biometric_id", pref.getString("biometricid",null));
                sendjson.put("secure_id",pref.getString("secureid",null));
                //sendjson.put("date1", "2016-07-19");
                sendjson.put("lat",lat);
                sendjson.put("lon",lon);
                JSONObject recievejson = null;
                if(pref.getInt("punchinstatus",-1)==0) {
                    Log.e("punhinurl", UrlSupport.Punch_In_Url + "" + sendjson.toString());
                    recievejson = new JsonPostParser().sendJson(UrlSupport.Punch_In_Url,sendjson);
                    Log.e("this is:", recievejson.toString());
                }else if(pref.getInt("punchinstatus",-1)==1) {
                    Log.e("punhouturl", UrlSupport.Punch_Out_Url + "" + sendjson.toString());
                    recievejson = new JsonPostParser().sendJson(UrlSupport.Punch_Out_Url,sendjson);
                    Log.e("this is 2:", recievejson.toString());
                }
                //JSONObject recievejson = new JsonPostParser().sendJson(UrlSupport.Punch_Out_Url,sendjson);
                if(recievejson!=null){
                    if(recievejson.getInt(SUCCESS_TAG)==1){
                        int status = recievejson.getInt("status");
                        String ptime = recievejson.get("Time").toString();
                        Log.e("status recieved:",""+status);
                        edit.putString("punchintime",ptime);
                        edit.putInt("punchinstatus",status);
                        edit.commit();
                        StringTokenizer st = new StringTokenizer(ptime," ");
                        punchdate = st.nextToken();
                        punchtime = st.nextToken();
                    }
                    else {
                        Toast.makeText(getActivity(), "Punch In Failed.\nTry Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){}
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref", 0);
            //Log.e("response is:",""+pref.getInt("punchinstatus",0));
            Log.e("time n date",punchdate+"  " +punchtime+ "  "+pref.getInt("punchinstatus",0) );
         //   if((punchtime!=null)&&(!punchtime.equals("DigitalClock"))){dc.setText(punchtime);}
          //  if(punchdate!=null){currentdate.setText(punchdate);}

            if(pref.getInt("punchinstatus",0)==1){
                buttonPunchIn.setVisibility(View.GONE);
                buttonPunchOut.setVisibility(View.VISIBLE);
                alert = new AlertDialog.Builder(getActivity())
                        .setTitle("Punch In Successful.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        })
                        .create();
                alert.show();
            }else{
                buttonPunchIn.setVisibility(View.VISIBLE);
                buttonPunchOut.setVisibility(View.GONE);
                alert = new AlertDialog.Builder(getActivity())
                        .setTitle("Punch Out Successful.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        })
                        .create();
                alert.show();
            }

            pDialog.dismiss();
        }
    }


    private void locationFinder() {

        gps = new GPSTracker(getContext());

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            lat = latitude;
            lon = longitude;


//            Toast.makeText(getContext(), "Your Location is - \nLat: " + lat + "\nLong: " + lon, Toast.LENGTH_LONG).show();
//            editor.putString("latitude", String.valueOf(latitude));
//            editor.putString("longitude", String.valueOf(longitude));
//            editor.putString("tracked", "true");
            //   Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            //   Toast.makeText(getApplicationContext(), "" + distance(latitude, longitude, latitude + 3, longitude + 4), Toast.LENGTH_SHORT).show();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }/*

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getActivity(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

// adding marker
        googleMap.addMarker(marker);
        // ROSE color icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lon)).zoom(12).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Uri gmmIntentUri = Uri.parse("geo:" + latLng);
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//            }
//        });

    }*/


    @Override
    public void onResume() {
        super.onResume();
        //initilizeMap();
    }

}
