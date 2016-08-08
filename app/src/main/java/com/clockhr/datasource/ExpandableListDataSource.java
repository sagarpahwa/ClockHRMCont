package com.clockhr.datasource;

/**
 * Created by Sagar Pahwa on 13-07-2016.
 */
import android.content.Context;

import com.clockhr.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ExpandableListDataSource {

    /**
     * Returns fake data of films
     *
     * @param context
     * @return
     */
    public static Map<String, List<String>> getData(Context context) {
        Map<String, List<String>> expandableListData = new TreeMap<>();
        List<String> erp = Arrays.asList(context.getResources().getStringArray(R.array.erp));
        List<String> hrm = Arrays.asList(context.getResources().getStringArray(R.array.hrm));
        expandableListData.put(erp.get(0), hrm);

        return expandableListData;
    }
}
