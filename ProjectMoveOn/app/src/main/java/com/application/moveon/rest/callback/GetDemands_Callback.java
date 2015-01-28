package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.Friends.DemandsAdapter;
import com.application.moveon.Friends.UserAdapter;
import com.application.moveon.R;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 26/01/2015.
 */
public class GetDemands_Callback implements Callback<DemandsPojo[]>{
    private String mail;
    private Activity activity;

    public GetDemands_Callback(String mail, Activity activity) {
        this.mail = mail;
        this.activity = activity;
    }

    @Override
    public void success(DemandsPojo[] strings, Response response) {
        if(strings == null) return;
        ArrayList<DemandsPojo> datas = new ArrayList<DemandsPojo>(Arrays.asList(strings));
        Collections.sort(datas);
        final ListView lv = (ListView) activity.findViewById(R.id.list_demands);
        final DemandsAdapter a = new DemandsAdapter(activity,datas, mail);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(a);
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {

    }
}
