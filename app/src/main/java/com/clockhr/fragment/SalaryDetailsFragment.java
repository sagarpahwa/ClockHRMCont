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
public class SalaryDetailsFragment extends Fragment {

    private String pan_card_no,Net_salary;
    private TextView textViewpan_card_no,textViewNet_salary;
    private TextView pan_card_num,nt_salry;
    public SalaryDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Profile_Pref",1);
        pan_card_no = sharedPreferences.getString("pan_card_no",null);
        Net_salary = sharedPreferences.getString("Net_salary",null);



        textViewpan_card_no = (TextView)view.findViewById(R.id.pan_card_no);
        textViewNet_salary = (TextView)view.findViewById(R.id.Net_salary);
        pan_card_num = (TextView)view.findViewById(R.id.pan_crd_num);
        nt_salry = (TextView)view.findViewById(R.id.nt_slry);
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        pan_card_num.setTypeface(face3);
        nt_salry.setTypeface(face3);
        textViewpan_card_no.setText(pan_card_no);
        textViewNet_salary.setText(Net_salary);



        return view;
    }

}
