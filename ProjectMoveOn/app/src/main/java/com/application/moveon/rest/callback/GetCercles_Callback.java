package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.cercle.CercleAdapter;
import com.application.moveon.friends.UserAdapter;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class GetCercles_Callback implements Callback<CerclePojo[]> {

    private String id;
    private Activity activity;
    //private UserAdapter adapter;
    private ListView list;
    private ProgressDialog progressDialog;
    private ArrayList<CerclePojo> formatedata;

    public GetCercles_Callback(Activity activity, ListView list) {
        //this.adapter = adapter;
        this.activity = activity;
        this.list = list;
        this.formatedata = new ArrayList<CerclePojo>();

    }

    @Override
    public void success(CerclePojo[] cerclePojos, Response response) {

        Log.d("QUENTIN", "BIEN JOUE BOBY");

        if(cerclePojos == null) return;
        ArrayList<CerclePojo> datas = new ArrayList<CerclePojo>(Arrays.asList(cerclePojos));


        final CercleAdapter a = new CercleAdapter(datas, activity.getBaseContext());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list != null)list.setAdapter(a);
            }
        });

        /*
        if(userPojos == null) return;
        ArrayList<UserPojo> datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
        Collections.sort(datas);
        final ListView lv = (ListView) activity.findViewById(R.id.list_contacts);
        final UserAdapter a = new UserAdapter(datas, activity.getBaseContext());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(lv != null)lv.setAdapter(a);
            }
        });
        */

    }

    @Override
    public void failure(RetrofitError error) {

        Log.d("QUENTIN", "CEST MOVAY  BOBY");

    }
}
