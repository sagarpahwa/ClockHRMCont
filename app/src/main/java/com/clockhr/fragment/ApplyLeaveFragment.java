package com.clockhr.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.clockhr.ApplyLeaveForm;
import com.clockhr.JsonPostParser;
import com.clockhr.R;
import com.clockhr.UrlSupport;
import com.clockhr.adapter.ViewPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApplyLeaveFragment extends Fragment {
    public static ArrayList<ArrayList<String>> superlist = new ArrayList<ArrayList<String>>();
    private boolean pending = false;
    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private ViewPagerAdapter adapter = null;
    private ProgressDialog pDialog;
    public static List<String> namelist = new ArrayList<>();
    public static int leaveApplicable[] = {0,0,0,0,0,0,0,0,0,0};
    private  View view;
    public static int selectedTabPosition;
    private Button applyLeave;
    public ApplyLeaveFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //this.container = container;
        view = inflater.inflate(R.layout.fragment_apply_leave, container, false);
        getIDs(view);
        //setEvents();
        //TODO:
        new GetLeaveData().execute();
        return view;
    }
    private void getIDs(View view) {
        /*viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        */
        namelist.add("Leave Type : ");
        namelist.add("Is Half Day : ");
        namelist.add("Is Carry Forward : ");
        namelist.add("Maximum Leave : ");
        namelist.add("Remaining Leave : ");
        namelist.add("Opening Leave : ");
        namelist.add("Carry Fwd Leave : ");
        namelist.add("Credit Leave : ");
        namelist.add("Apply Leave : ");
        namelist.add("Approved Leave : ");
    }
    private void setEvents() {
        applyLeave = (Button)view.findViewById(R.id.applyLeave);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);

                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                if(leaveApplicable[selectedTabPosition]>0){
                    applyLeave.setBackgroundColor(0x99009688);
                }else {
                    applyLeave.setBackgroundColor(0x99FFFFFF);
                }
                Log.d("Selected", "Selected " + tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab!=null)
                    Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
        applyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (leaveApplicable[selectedTabPosition]>0){
                    Intent intent = new Intent(getActivity(),ApplyLeaveForm.class);
                    intent.putExtra("remainingLeave",leaveApplicable[selectedTabPosition]);
                    pending=true;
                    startActivity(intent);
                }else{
                    Toast.makeText(getActivity(), "No Remaining Leaves", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void addPage(String pagename, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        LeaveSubfragment leaveSubfragment = new LeaveSubfragment(position);
        leaveSubfragment.setArguments(bundle);
        adapter.addFrag(leaveSubfragment, pagename);
/*
        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0)
            tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
*/
    }
    public void setupTabLayout() {
        selectedTabPosition = viewPager.getCurrentItem();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
        }
    }
    class GetLeaveData extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
            adapter = new ViewPagerAdapter(getFragmentManager(), getActivity());
            viewPager.setAdapter(adapter);
            tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
            setEvents();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching Attendence...");
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();

        }
        @SuppressWarnings("unchecked")
        @Override
        protected String doInBackground(String... args) {
            SharedPreferences pref = getActivity().getSharedPreferences("Login_Pref", 0);
            JSONObject sendjson = new JSONObject();
            try {
                sendjson.put("domain", pref.getString("domain",null));
                sendjson.put("username", pref.getString("username",null));
                sendjson.put("secure_id",pref.getString("secureid",null));
                Log.e("sending Api Query :", UrlSupport.Apply_Leave+sendjson.toString());
                JSONObject recievejson = null;
                recievejson = new JsonPostParser().sendJson(UrlSupport.Apply_Leave,sendjson);
                Log.e("respose is:", " "+recievejson);
                JSONArray jsonArray = null;
                if (recievejson!= null) {
                    jsonArray = recievejson.getJSONArray("leavedata");
                    //String jsondata = recievejson.toString();
                    Log.e("json array", ""+jsonArray);
                }
                if (jsonArray != null) {
                    superlist.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int r=0;
                        JSONObject obj = jsonArray.getJSONObject(i);
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(obj.getString("leave_type"));
                        list.add(obj.getString("is_half_day"));
                        list.add(obj.getString("is_carry_forward"));
                        list.add(obj.getString("maximum_leave"));
                        list.add(obj.getString("remaining_leave"));
                        r= Integer.valueOf(obj.getString("remaining_leave"));
                        if(r>0) leaveApplicable[i] = r;
                        else leaveApplicable[i] = 0;
                        list.add(obj.getString("opening_leave"));
                        list.add(obj.getString("carry_fwd_Leave"));
                        list.add(obj.getString("credt_leave"));
                        list.add(obj.getString("apply_Leave"));
                        list.add(obj.getString("approved_leave"));
                        superlist.add(i,list);
                        Log.e("json list #"+i+"", ""+list);
                        addPage(obj.getString("leave_name"),i);
                    }
                    Log.e("superlist ",""+superlist);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            adapter.notifyDataSetChanged();
            viewPager.setAdapter(adapter);
            setEvents();
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(0);
            Log.e("tab count ",""+tabLayout.getTabCount()+"  "+viewPager.getChildCount());
            setupTabLayout();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pending) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            Fragment newFragment = this;
            this.onDestroy();
            ft.remove(this);
            ft.replace(R.id.selected_content, newFragment,null);
            ft.addToBackStack(null);
            ft.commit();
            pending=false;
        }
    }
}