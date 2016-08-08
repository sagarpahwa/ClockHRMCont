package com.clockhr;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sagar Pahwa on 25-06-2016.
 */
public class DemoDB extends SQLiteOpenHelper {
    public DemoDB(Context context) {  super(context, "ELearnLocalDB.db", null, 1);  }
    @Override
    public void onCreate(SQLiteDatabase localDB) {
        localDB.execSQL("create table demodetails (" +
                "id integer primary key AUTOINCREMENT," +
                "code text," +
                "userid text," +
                "pass text," +
                "type text," +
                "status integer DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

