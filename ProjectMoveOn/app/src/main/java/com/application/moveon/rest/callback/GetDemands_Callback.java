package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.application.moveon.friends.DemandsAdapter;
import com.application.moveon.R;
import com.application.moveon.rest.modele.DemandsPojo;

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
    private ProgressDialog p;

    public GetDemands_Callback(String mail, Activity activity, ProgressDialog p) {
        this.mail = mail;
        this.activity = activity;
        this.p = p;
    }

    @Override
    public void success(DemandsPojo[] strings, Response response) {

        final TextView textv = (TextView)activity.findViewById(R.id.no_demands);
        final DemandsPojo[] condition = strings;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                p.hide();
                if(condition == null){
                    textv.setVisibility(View.VISIBLE);
                    textv.setText("Vous n'avez pas de demande d'ami en attente");


                }else {

                    textv.setVisibility(View.INVISIBLE);
                }
            }
        });

        if(condition == null)return;

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
