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
public class QualificationDetailsFragment extends Fragment {

    private String qualification,basic_graduation,basic_specializ,basic_university,basic_year,post_graduation,post_specializ,post_year,diploma_certificate;
    private TextView textViewqualification,textViewbasic_graduation,textViewbasic_specializ,textViewbasic_university,textViewbasic_year,textViewpost_graduation,
            textViewpost_specializ,textViewpost_year,textViewdiploma_certificate;

    private TextView quali,basc_gradu,basc_spec,basc_unv,basc_yer,post_gradu,post_yer,diplo_certi;
    public QualificationDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_three, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Profile_Pref",1);
        qualification = sharedPreferences.getString("qualification",null);
        basic_graduation = sharedPreferences.getString("basic_graduation",null);
        basic_specializ = sharedPreferences.getString("basic_specializ",null);
        basic_university = sharedPreferences.getString("basic_university",null);
        basic_year = sharedPreferences.getString("basic_year",null);
        post_graduation = sharedPreferences.getString("post_graduation",null);
        post_specializ = sharedPreferences.getString("post_specializ",null);
        post_year = sharedPreferences.getString("post_year",null);
        diploma_certificate =sharedPreferences.getString("diploma_certificate",null);

        textViewqualification = (TextView)view.findViewById(R.id.qualification);
        textViewbasic_graduation = (TextView)view.findViewById(R.id.basic_graduation);
        textViewbasic_specializ = (TextView)view.findViewById(R.id.basic_specializ);
        textViewbasic_university = (TextView)view.findViewById(R.id.basic_university);
        textViewbasic_year = (TextView)view.findViewById(R.id.basic_year);
        textViewdiploma_certificate = (TextView)view.findViewById(R.id.diploma_certificate);
        textViewpost_year = (TextView)view.findViewById(R.id.post_year);
        textViewpost_graduation = (TextView)view.findViewById(R.id.post_graduation);
        textViewpost_specializ = (TextView)view.findViewById(R.id.post_specializ);
        quali = (TextView)view.findViewById(R.id.quali);
        basc_gradu = (TextView)view.findViewById(R.id.basc_gradu);
        basc_spec = (TextView)view.findViewById(R.id.basc_spec);
        basc_unv = (TextView)view.findViewById(R.id.basc_univ);
        basc_yer = (TextView)view.findViewById(R.id.basc_yer);
        post_gradu = (TextView)view.findViewById(R.id.pst_gradu);
        post_yer = (TextView)view.findViewById(R.id.post_yer);
        diplo_certi = (TextView)view.findViewById(R.id.dilpo_certi);
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        quali.setTypeface(face3);
        basc_gradu.setTypeface(face3);
        basc_spec.setTypeface(face3);
        basc_unv.setTypeface(face3);
        basc_yer.setTypeface(face3);
        post_gradu.setTypeface(face3);
        post_yer.setTypeface(face3);
        diplo_certi.setTypeface(face3);
        textViewqualification.setText(qualification);
        textViewbasic_graduation.setText(basic_graduation);
        textViewbasic_specializ.setText(basic_specializ);
        textViewbasic_university.setText(basic_university);
        textViewbasic_year.setText(basic_year);
        textViewdiploma_certificate.setText(diploma_certificate);
        textViewpost_year.setText(post_year);
        textViewpost_graduation.setText(post_graduation);
        textViewpost_specializ.setText(post_specializ);



        return view;
    }

}
