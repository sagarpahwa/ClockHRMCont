package com.clockhr;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clockhr.adapter.CustomExpandableListAdapter;
import com.clockhr.adapter.CustomListAdapter;
import com.clockhr.datasource.ExpandableListDataSource;
import com.clockhr.fragment.ApplyLeaveFragment;
import com.clockhr.fragment.AttendenceFragment;
import com.clockhr.fragment.ClockInFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;


public class  MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String msg = "No Details Found";
    private Runnable mPendingThread;
    Handler mHandler;
    private int position=1;
    private ListView mListView;
    private CustomListAdapter mcustomListAdapter;
    private List<String> mListTitle = new ArrayList<>();
    /*
        private ExpandableListView mExpandableListView;
        private ExpandableListAdapter mExpandableListAdapter;
        private List<String> mExpandableListTitle;
        private Map<String, List<String>> mExpandableListData;
        */
    private TextView mSelectedItemView;
    private FrameLayout mSelectedContent;
    String domain="",username="",password="",type = "",fName = "",lName = "",userid="",adminid="",dbname="",biometricid="",profile="";
    private AlertDialog alert = null;
    private ApplyLeaveFragment fragment3 = new ApplyLeaveFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        SharedPreferences pref = null;
        SharedPreferences.Editor editor;
        pref = getSharedPreferences("Login_Pref",0);
        editor = pref.edit();
        if(pref.getString("username", null) != null  ){
            domain = pref.getString("domain", null);
            username = pref.getString("username", null);
            password=pref.getString("password", null);
            type=pref.getString("type",null);
            fName=pref.getString("fName",null);
            lName=pref.getString("lName",null);
            userid=pref.getString("userid",null);
            adminid=pref.getString("adminid",null);
            dbname=pref.getString("dbname",null);
            biometricid=pref.getString("biometricid",null);
            profile = pref.getString("profile",null);
            //msg = "domain: "+ domain+"\nusername: " +username+"\npassword: " +password+"\ntype: "+type+"\nfirst name: "+fName+"\nlast name: "+lName+"\nuser id: "+userid+"\nadmin id: "+adminid+"\ndbname: "+dbname+"\nbiometric id: "+biometricid;
            msg = "Hello, "+fName+" "+lName+ "!";

         /*   Intent intent = getIntent();
            domain = intent.getStringExtra("domain");
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
            type = intent.getStringExtra("type");
            msg = domain+" "+username+" "+password+" "+type;
            SQLiteDatabase sqlwrite = new DemoDB(this).getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("code",domain);
            cv.put("userid",username);
            cv.put("pass",password);
            cv.put("type",type);
            cv.put("status",1);
            long l = sqlwrite.insert("demodetails",null,cv);

            Toast.makeText(this, l + "  "+msg, Toast.LENGTH_SHORT).show();*/
        }else{
            Toast.makeText(this, "something went wrong in main activity "+msg, Toast.LENGTH_SHORT).show();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mActivityTitle = getTitle().toString();
        mActivityTitle = "Mark Attendance";
            /*mActivityTitle = fName+" "+lName;
            setTitle(mActivityTitle);
            */
        mListView = (ListView)findViewById(R.id.navList);




        //mExpandableListView = (ExpandableListView) findViewById(R.id.navList);
        mSelectedContent = (FrameLayout)findViewById(R.id.selected_content);
        mSelectedItemView = (TextView) findViewById(R.id.selected_item);
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header, null, false);
        TextView userdetails = (TextView)listHeaderView.findViewById(R.id.userdetails);
        userdetails.setText(msg);

        mListView.addHeaderView(listHeaderView);
        mListTitle.add("Mark Attendance");
        mListTitle.add("Attendance");
        mListTitle.add("Apply Leave");
/*
            mExpandableListView.addHeaderView(listHeaderView);
            mExpandableListData = ExpandableListDataSource.getData(this);
            mExpandableListTitle = new ArrayList(mExpandableListData.keySet());*/

        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        CircleImageView circleImageView =(CircleImageView)findViewById(R.id.profilepicnewprofile);
        Picasso.with(MainActivity.this)
                .load("http://"+profile)
                .placeholder(R.drawable.default_avatar) // optional
                .into(circleImageView);
        listHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });
        UrlSupport.position=1;
        getSupportFragmentManager().beginTransaction().add(R.id.selected_content,new BlankFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new ClockInFragment()).commit();
        getSupportActionBar().setTitle(mActivityTitle);
    }
    private void addDrawerItems() {
        mcustomListAdapter = new CustomListAdapter(MainActivity.this,mListTitle,R.layout.list_item);
        mListView.setAdapter(mcustomListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UrlSupport.position = i;

                getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new BlankFragment(),null).commit();
                position = i;
                Log.e("clicked on item ",""+i);
                mActivityTitle = mListTitle.get(i-1);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mPendingThread = new Runnable() {
                    @Override
                    public void run() {
                        if(position==1){

                            getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new ClockInFragment(),null).commit();
                        }
                        else if(position==2){
                            getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new AttendenceFragment(),null).commit();
                        }
                        else if (position==3){
                            getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,fragment3,null).commit();
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new BlankFragment(),null).commit();
                        }
                    }
                };
            }
        });
        /*
        mExpandableListAdapter = new CustomExpandableListAdapter(this, mExpandableListTitle, mExpandableListData);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                getSupportActionBar().setTitle(mExpandableListTitle.get(groupPosition).toString());
                mSelectedItemView.setText(mExpandableListTitle.get(groupPosition).toString());
            }
        });
        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                getSupportActionBar().setTitle(R.string.film_genres);
                mSelectedItemView.setText(R.string.selected_item);
            }
        });
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (mExpandableListData.get(mExpandableListTitle.get(groupPosition))))
                        .get(childPosition).toString();
                //getSupportActionBar().setTitle(selectedItem);
                mActivityTitle = selectedItem;
                mSelectedItemView.setText(mExpandableListTitle.get(groupPosition).toString() + " -> " + selectedItem);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if(groupPosition==0&&childPosition==0)
                    getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new AttendenceFragment(),null).commit();
                else if(groupPosition==0&&childPosition==1)
                    getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new ClockInFragment(),null).commit();
                else
                    getSupportFragmentManager().beginTransaction().replace(R.id.selected_content,new BlankFragment(),null).commit();
                return false;
            }
        });*/
    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.film_genres);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
                //if(getSupportActionBar().getTitle().toString().toLowerCase().equals("dashboard"))
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                if (mPendingThread != null) {
                    mHandler.post(mPendingThread);
                    mPendingThread = null;
                }
                getSupportActionBar().setTitle(mActivityTitle);

            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            //Intent myintent =new Intent(MainActivity.this,LoginMainActivity.class);
            SharedPreferences preferences = getSharedPreferences("Login_Pref",0);
            SharedPreferences.Editor  editor = preferences.edit();
            editor.putString("flag","false");
            editor.commit();
            finish();
            return super.onOptionsItemSelected(item);
        }
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
//            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                System.exit(0);
            }
        }, 2000);
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        alert = new AlertDialog.Builder(this)
//                .setTitle("Are You Sure You Want To Exit App.")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                        System.exit(0);
//
//                    }
//                })
//                .create();
//        alert.show();
//    }

    /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String msg = data.getStringExtra("code")+""+data.getStringExtra("id")+""+data.getStringExtra("pass");
        SQLiteDatabase sqlwrite = new DemoDB(this).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("code",data.getStringExtra("code"));
        cv.put("userid",data.getStringExtra("id"));
        cv.put("pass",data.getStringExtra("pass"));
        cv.put("status",1);
        sqlwrite.insert("demodetails",null,cv);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }*/
}