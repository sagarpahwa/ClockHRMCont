package com.clockhr.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.clockhr.JsonPostParser;
import com.clockhr.R;
import com.clockhr.UrlSupport;
import com.clockhr.adapter.CustomListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

@SuppressLint("ValidFragment")
public class LeaveSubfragment extends Fragment {
    private int position = 0;
    private ListView listView;
    private CustomListAdapter adapter;

    @SuppressLint("ValidFragment")
    public LeaveSubfragment(int position) {
        this.position = position;
    }

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subfragment_leave, container, false);
        getIDs(view);
        setEvents();
        return view;
    }
    private void getIDs(View view) {
        listView = (ListView)view.findViewById(R.id.leavelist);
        adapter = new CustomListAdapter(getActivity(),ApplyLeaveFragment.superlist.get(position),R.layout.custom_view_apply_leave_list);
        listView.setAdapter(adapter);
    }
    private void setEvents() {

    }
}