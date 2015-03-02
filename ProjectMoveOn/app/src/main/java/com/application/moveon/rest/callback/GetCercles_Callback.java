package com.application.moveon.rest.callback;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.cercle.Adapter.ListCercleAdapter;
import com.application.moveon.rest.modele.CerclePojo;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class GetCercles_Callback implements Callback<CerclePojo[]> {

    private Activity activity;
    private ListView list;
    private ArrayList<CerclePojo> formatedata;
    private FragmentInfoCercle fragmentInfoCercle;

    public GetCercles_Callback(Activity activity, ListView list, FragmentInfoCercle fragmentInfoCercle) {
        //this.adapter = adapter;
        this.activity = activity;
        this.list = list;
        this.formatedata = new ArrayList<CerclePojo>();
        this.fragmentInfoCercle = fragmentInfoCercle;

    }

    @Override
    public void success(CerclePojo[] cerclePojos, Response response) {

        if(cerclePojos == null) return;
        ArrayList<CerclePojo> datas = new ArrayList<CerclePojo>(Arrays.asList(cerclePojos));


        final ListCercleAdapter a = new ListCercleAdapter(datas, activity.getBaseContext(), fragmentInfoCercle);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(list != null)list.setAdapter(a);
            }
        });

    }

    @Override
    public void failure(RetrofitError error) {
        Log.d("GET_CERCLES : ",error.toString());
    }
}
