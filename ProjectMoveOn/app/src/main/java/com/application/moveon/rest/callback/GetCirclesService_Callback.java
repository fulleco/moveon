package com.application.moveon.rest.callback;

import android.util.Log;

import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 26/02/2015.
 */
public class GetCirclesService_Callback implements Callback<CerclePojo[]>{

    public GetCirclesService_Callback() {
    }


    @Override
    public void success(CerclePojo[] cerclePojo, Response response) {

        Log.i("ANTHO", "UPDATE CERCLE OK");

        MoveOnDB bdd = MoveOnDB.getInstance();
        ArrayList<CerclePojo> datas;
        String circles = new String();

        if(cerclePojo != null) {
            datas = new ArrayList<CerclePojo>(Arrays.asList(cerclePojo));

            for(CerclePojo cp : datas){
                circles += cp.getId_cercle() + " ";
            }
            circles = circles.substring(0, circles.length()-1);
            Log.i("ANTHO", "UPDATE PARTICIPANTS SERVICE" + circles);

        }else{
            datas = new ArrayList<CerclePojo>();

        }

        bdd.updateCircles(datas);

        MoveOnService mos = new RestClient(true).getApiService();

        if(datas.size() > 0){
            mos.getAllParticipants(circles, new UpdateParticipants_Callback());
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("MOVEON CIRCLES", "NOT WORKING");
    }
}
