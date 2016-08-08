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
public class BankDetailsFragment extends Fragment {
    private String ifsc_code,account_number,account_hoder_name,bank_location,bank_info;
    private TextView textViewifsc_code,textViewaccount_number,textViewaccount_hoder_name,textViewbank_location,textViewbank_info;
    private TextView ifsc,acc_nme,acc_hol_nme,bnk_loc,bnk_info;
    public BankDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bank_details, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Profile_Pref",1);

        ifsc_code = sharedPreferences.getString("ifsc_code",null);
        account_number = sharedPreferences.getString("account_number",null);
        account_hoder_name = sharedPreferences.getString("account_hoder_name",null);
        bank_location = sharedPreferences.getString("bank_location",null);
        bank_info = sharedPreferences.getString("bank_info",null);

        textViewifsc_code = (TextView)view.findViewById(R.id.ifsc_code);
        textViewaccount_hoder_name = (TextView)view.findViewById(R.id.account_hoder_name);
        textViewaccount_number = (TextView)view.findViewById(R.id.account_number);
        textViewbank_location = (TextView)view.findViewById(R.id.bank_location);
        textViewbank_info = (TextView)view.findViewById(R.id.bank_info);
        ifsc = (TextView)view.findViewById(R.id.ifsc);
        acc_nme = (TextView)view.findViewById(R.id.acct_num);
        acc_hol_nme = (TextView)view.findViewById(R.id.acc_hol_nme);
        bnk_loc = (TextView)view.findViewById(R.id.bnk_loc);
        bnk_info = (TextView)view.findViewById(R.id.bnk_info);

        Typeface face3= Typeface.createFromAsset(getActivity().getAssets(), "fonts/segoe-ui-light.ttf");
        ifsc.setTypeface(face3);
        acc_nme.setTypeface(face3);
        acc_hol_nme.setTypeface(face3);
        bnk_loc.setTypeface(face3);
        bnk_info.setTypeface(face3 );

        textViewbank_info.setText(bank_info);
        textViewbank_location.setText(bank_location);
        textViewaccount_number.setText(account_number);
        textViewifsc_code.setText(ifsc_code);
        textViewaccount_hoder_name.setText(account_hoder_name);





        return view;
    }

}
