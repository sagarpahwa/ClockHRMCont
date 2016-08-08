package com.clockhr.adapter;

/**
 * Created by admin on 7/28/2016.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clockhr.R;

import java.util.ArrayList;



/**
 * Created by DAT on 8/16/2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();
    Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        /*mFragmentList.clear();
        mFragmentTitleList.clear();
        */this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        int i = 0;
        if(mFragmentTitleList.contains(title)){
            i = mFragmentList.indexOf(title);
            mFragmentList.remove(i);
            mFragmentTitleList.remove(i);
            mFragmentList.add(i,fragment);
            mFragmentTitleList.add(i,title);
        }else{
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        Log.e("adapter says ", ""+mFragmentList.toString());
    }

    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab_item, null);
        TextView tabItemName = (TextView) view.findViewById(R.id.textViewTabItemName);
        tabItemName.setText(mFragmentTitleList.get(position));
        Log.e("frag list size ",""+mFragmentList.size());
        tabItemName.setTextColor(context.getResources().getColor(R.color.white));
        return view;
    }



    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
