package com.clockhr.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clockhr.R;
import com.clockhr.fragment.ApplyLeaveFragment;

import java.util.List;

/**
 * Created by Admin4 on 7/22/2016.
 */
public class CustomListAdapter extends BaseAdapter {

    private Activity mactivity;
    private List<String> mListTitle;
    private int mviewId;

    public CustomListAdapter(Activity mactivity, List<String> mListTitle, int mviewId) {
        this.mactivity = mactivity;
        this.mListTitle = mListTitle;
        this.mviewId = mviewId;
    }
    @Override
    public int getCount() {
        return mListTitle.size();
    }
    @Override
    public Object getItem(int i) {
        return mListTitle.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mactivity.getLayoutInflater().inflate(mviewId, viewGroup, false);
        if(mviewId==R.layout.list_item) {
            TextView textView = (TextView) view.findViewById(R.id.expandedListItem);
            textView.setText(mListTitle.get(i));
            ImageView imageView = (ImageView)view.findViewById(R.id.itemicon);
            if(i==0){
                imageView.setBackgroundResource(R.drawable.ic_mode_edit_black_24dp);
            }
            if(i==1){
                imageView.setBackgroundResource(R.drawable.ic_event_note_black_24dp);
            }
            if(i==2){
                imageView.setBackgroundResource(R.drawable.apply_leave_24dp);
            }
        }
        else if(mviewId == R.layout.custom_view_apply_leave_list){
            TextView textView = (TextView)view.findViewById(R.id.heading);
            textView.setText(ApplyLeaveFragment.namelist.get(i));
            TextView textView1 = (TextView)view.findViewById(R.id.heading_data);
            textView1.setText(mListTitle.get(i));
        }
        return view;
    }
}
