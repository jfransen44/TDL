package com.example.jfransen44.tdl;

import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * Created by JFransen44 on 12/14/15.
 */
public class VerifyData {

    ArrayAdapter<String> adapter;

    public VerifyData(ArrayAdapter<String> adapter){
        this.adapter = adapter;
        Log.d("TEST", adapter.getItem(1).toString());

    }

    public static boolean isDuplicate (String testItem){
        return false;
    }


}
