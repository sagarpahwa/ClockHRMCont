package com.clockhr.fragment;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clockhr.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExperienceDetailsFragment extends Fragment {

    private String experience_detail,experience_year,experience_month;
    private TextView textViewexperience_detail,textViewexperience_year,textViewexperience_month;

    private TextView exp_yer,exp_mnth,exp_detal;

    public ExperienceDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_experince_details, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Profile_Pref",1);

        experience_detail = sharedPreferences.getString("experience_detail",null);
        experience_year = sharedPreferences.getString("experience_year",null);
        experience_month = sharedPreferences.getString("experience_month",null);

        textViewexperience_detail = (TextView)view.findViewById(R.id.experience_detail);
        textViewexperience_month = (TextView)view.findViewById(R.id.experience_month);
        textViewexperience_year = (TextView)view.findViewById(R.id.experience_year);
        exp_yer = (TextView)view.findViewById(R.id.exp_yer);
        exp_mnth = (TextView)view.findViewById(R.id.exp_mnth);
        exp_detal = (TextView)view.findViewById(R.id.exp_detl);
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        exp_yer.setTypeface(face3);
        exp_mnth.setTypeface(face3);
        exp_detal.setTypeface(face3);
        textViewexperience_year.setText(experience_year);
        textViewexperience_month.setText(experience_month);
        textViewexperience_detail.setText(experience_detail);
        return view;
    }

}
