package com.clockhr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clockhr.fragment.BankDetailsFragment;
import com.clockhr.fragment.ExperienceDetailsFragment;
import com.clockhr.fragment.HomeFragment;
import com.clockhr.fragment.QualificationDetailsFragment;
import com.clockhr.fragment.SalaryDetailsFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

        public static final String EXTRA_NAME = "cheese_name";
    // Progress Dialog
    private ProgressDialog pDialog = null;
    JsonPostParser jsonPostParser = new JsonPostParser();
    String biometricId;
    String domain;
    String secureid;
    private static final String SUCCESS_TAG = "success";
    private ImageView imageView;
     TextView textView;
    private String Emp_name,Emp_number,Position, Department,Joining_Date,Gender,Contact,Email_id,Reporting_Manager,Profile_Pic,location,qualification,pan_card_no,basic_graduation,basic_specializ,
            basic_university,basic_year,post_graduation,post_specializ,post_year,diploma_certificate,experience_detail,experience_year,experience_month,Net_salary,deduction,ifsc_code,account_number,account_hoder_name,bank_location,bank_info;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    SharedPreferences pref = null;
    SharedPreferences.Editor editor;


    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            pref = getSharedPreferences("Profile_Pref", 1);
            editor = pref.edit();
            SharedPreferences pref = getSharedPreferences("Login_Pref",0);
            String fName=pref.getString("fName",null);
            String lName=pref.getString("lName",null);
            setTitle(fName+" "+lName);
            biometricId = pref.getString("biometricid",null);
            domain = pref.getString("domain", null);
            secureid = pref.getString("secureid",null);
            Intent intent = getIntent();
            final String cheeseName = intent.getStringExtra(EXTRA_NAME);
            imageView = (ImageView) findViewById(R.id.backdrop);
//            textView = (TextView)findViewById(R.id.DetailsProfile);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(cheeseName);
            collapsingToolbar.setCollapsedTitleTextColor(0xffffffff);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
          new ProfileDetails().execute();

//            loadBackdrop();


        }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new SalaryDetailsFragment(), "Salary Details");
        adapter.addFragment(new QualificationDetailsFragment(), "Qualification Details");
        adapter.addFragment(new ExperienceDetailsFragment(),"Experience Details");
        adapter.addFragment(new BankDetailsFragment(),"Bank Details");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

//        private void loadBackdrop() {
//            final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
//
//            Glide.with(this).load(R.drawable.default_avatar).centerCrop().into(imageView);
//        }

    @SuppressWarnings("rawtypes")
    class ProfileDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfileActivity.this);
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
                sendjson.put("domain", domain);
                sendjson.put("emp_id", biometricId);
                sendjson.put("secure_id",secureid);
                JSONObject recievejson = null;
                Log.e("Employee_Profile", UrlSupport.Employee_Profile + "" + sendjson.toString());
                recievejson = new JsonPostParser().sendJson(UrlSupport.Employee_Profile,sendjson);

                if(recievejson!=null){

                    if(recievejson.getInt(SUCCESS_TAG)==1){
                        Log.e("profile respose: ", recievejson.toString());
                        try {
                            int status = recievejson.getInt("Status");
                             Emp_name = recievejson.getString("Emp_name");
                            Emp_number = recievejson.get("Emp_number").toString();
                            Position = recievejson.get("Position").toString();
                            Department = recievejson.get("Department").toString();
                            Joining_Date = recievejson.get("Joining_Date").toString();
                            Gender = recievejson.get("Gender").toString();
                            Contact = recievejson.get("Contact").toString();
                            Email_id = recievejson.get("Email_id").toString();
                            Reporting_Manager = recievejson.get("Reporting_Manager").toString();
                            Profile_Pic = recievejson.get("Profile_Pic").toString();
                            location = recievejson.get("current_location").toString();
                            qualification = recievejson.get("qualification").toString();
                            pan_card_no = recievejson.get("pan_card_no").toString();
                            basic_graduation = recievejson.get("basic_graduation").toString();
                            basic_specializ = recievejson.get("basic_specializ").toString();
                            basic_university = recievejson.get("basic_university").toString();
                            basic_year = recievejson.get("basic_year").toString();
                            post_graduation = recievejson.get("post_graduation").toString();
                            post_specializ = recievejson.get("post_specializ").toString();
                            post_year = recievejson.get("post_year").toString();
                            diploma_certificate = recievejson.get("diploma_certificate").toString();
                            experience_detail = recievejson.get("experience_detail").toString();
                            experience_year = recievejson.get("experience_year").toString();
                            experience_month = recievejson.get("experience_month").toString();
                            Net_salary = recievejson.get("Net_salary").toString();
                            ifsc_code = recievejson.get("ifsc_code").toString();
                            account_number = recievejson.get("account_number").toString();
                            account_hoder_name = recievejson.get("account_hoder_name").toString();
                            bank_location = recievejson.get("bank_location").toString();
                            bank_info = recievejson.get("bank_info").toString();

                            Log.e("Emp_name",Emp_name);
                            Log.e("Profile_Pic",Profile_Pic);
                            Log.e("diploma_certificate",diploma_certificate);
                            Log.e("qualification",qualification);
                            editor.putInt("status",status);
                            editor.putString("Emp_name",Emp_name);
                            editor.putString("Emp_number",Emp_number);
                            editor.putString("Position",Position);
                            editor.putString("Department",Department);
                            editor.putString("Joining_Date",Joining_Date);
                            editor.putString("Gender",Gender);
                            editor.putString("Contact",Contact);
                            editor.putString("Email_id",Email_id);
                            editor.putString("Reporting_Manager",Reporting_Manager);
                            editor.putString("current_location",location);
                            editor.putString("qualification",qualification);
                            editor.putString("pan_card_no",pan_card_no);
                            editor.putString("basic_graduation",basic_graduation);
                            editor.putString("basic_specializ",basic_specializ);
                            editor.putString("basic_university",basic_university);
                            editor.putString("basic_year",basic_year);
                            editor.putString("post_graduation",post_graduation);
                            editor.putString("post_specializ",post_specializ);
                            editor.putString("post_year",post_year);
                            editor.putString("diploma_certificate",diploma_certificate);
                            editor.putString("experience_detail",experience_detail);
                            editor.putString("experience_year",experience_year);
                            editor.putString("experience_month",experience_month);
                            editor.putString("Net_salary",Net_salary);
                            editor.putString("ifsc_code",ifsc_code);
                            editor.putString("account_number",account_number);
                            editor.putString("account_hoder_name",account_hoder_name);
                            editor.putString("bank_location",bank_location);
                            editor.putString("bank_info",bank_info);
                            editor.commit();

                        }catch (Exception e){
                            Log.e("inner error: ", ""+e);


                        }
//
//
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Failed.\nTry Again.", Toast.LENGTH_SHORT).show();
                    }
                }





            }catch (JSONException e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Log.e("Profile Url",Profile_Pic+Emp_name);
//            textView.setText(Emp_name);

//
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager(viewPager);
            tabLayout = (TabLayout)findViewById(R.id.detail_tabs);
            tabLayout.setupWithViewPager(viewPager);
            Picasso.with(ProfileActivity.this)
                    .load("http://"+Profile_Pic)
                    .placeholder(R.drawable.default_avatar) // optional
                    .into(imageView);

        }
    }

}
