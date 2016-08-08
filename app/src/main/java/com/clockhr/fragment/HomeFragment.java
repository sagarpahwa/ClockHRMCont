package com.clockhr.fragment;


import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clockhr.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    SharedPreferences pref = null;
    SharedPreferences.Editor editor;
    private String Emp_name,Emp_number,Position, Department,Joining_Date,Gender,Contact,Email_id,Reporting_Manager,Profile_Pic,location;
    private TextView textViewEmpName,textViewEmpNumber,textViewEmpPosition,textViewEmpDepartment,textViewEmpJoinigDate,textViewEmpGender,textViewEmpContact
            ,textViewEmpEmail,textViewEmpReportingManager,textViewEmplocation;
    private TextView nme,emp_num,pos,dept,join_dte,gen,contct,eml_id,repo_mngr,loc;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Profile_Pref",1);
        Emp_name = sharedPreferences.getString("Emp_name",null);
        Emp_number = sharedPreferences.getString("Emp_number",null);
        Position = sharedPreferences.getString("Position",null);
        Department = sharedPreferences.getString("Department",null);
        Joining_Date = sharedPreferences.getString("Joining_Date",null);
        Gender = sharedPreferences.getString("Gender",null);
        Contact = sharedPreferences.getString("Contact",null);
        Email_id = sharedPreferences.getString("Email_id",null);
        Reporting_Manager = sharedPreferences.getString("Reporting_Manager",null);
        location = sharedPreferences.getString("current_location",null);


        textViewEmpName = (TextView)view.findViewById(R.id.Emp_name);
        textViewEmpNumber = (TextView)view.findViewById(R.id.Emp_number);
        textViewEmpPosition = (TextView)view.findViewById(R.id.Emp_position);
        textViewEmpDepartment = (TextView)view.findViewById(R.id.Emp_department);
        textViewEmpJoinigDate = (TextView)view.findViewById(R.id.Emp_joining_date);
        textViewEmpGender = (TextView)view.findViewById(R.id.Emp_gender);
        textViewEmpContact = (TextView)view.findViewById(R.id.Emp_contact);
        textViewEmpEmail = (TextView)view.findViewById(R.id.Emp_email_id);
        textViewEmpReportingManager = (TextView)view.findViewById(R.id.Emp_reporting_manager);
        textViewEmplocation = (TextView)view.findViewById(R.id.Emp_location);
        nme = (TextView)view.findViewById(R.id.nme);
        emp_num = (TextView)view.findViewById(R.id.emp_num);
        pos = (TextView)view.findViewById(R.id.pos);
        dept =(TextView)view.findViewById(R.id.dept);
        join_dte =(TextView)view.findViewById(R.id.join_dte);
        gen = (TextView)view.findViewById(R.id.gen);
        contct = (TextView)view.findViewById(R.id.contac);
        eml_id =(TextView)view.findViewById(R.id.eml_id);
        repo_mngr =(TextView)view.findViewById(R.id.repo_mngr);
        loc = (TextView)view.findViewById(R.id.loc);
        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        nme.setTypeface(face3);
        emp_num.setTypeface(face3);
        pos.setTypeface(face3);
        dept.setTypeface(face3);
        join_dte.setTypeface(face3);
        gen.setTypeface(face3);
        contct.setTypeface(face3);
        eml_id.setTypeface(face3);
        repo_mngr.setTypeface(face3);
        loc.setTypeface(face3);
        textViewEmplocation.setText(location);
        textViewEmpReportingManager.setText(Reporting_Manager);
        textViewEmpEmail.setText(Email_id);
        textViewEmpContact.setText(Contact);
        textViewEmpGender.setText(Gender);
        textViewEmpJoinigDate.setText(Joining_Date);
        textViewEmpDepartment.setText(Department);
        textViewEmpPosition.setText(Position);
        textViewEmpNumber.setText(Emp_number);
        textViewEmpName.setText(Emp_name);

        return view;
    }

}
