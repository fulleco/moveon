package com.application.moveon.rest.callback;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.HomeActivity;
import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class DeleteParticipant_Callback implements Callback<UserPojo[]> {


    private Activity activity;
    private ListView list;
    private ArrayList<UserPojo> formatedata;
    private FragmentInfoCercle fragmentInfoCercle;

    public DeleteParticipant_Callback(Activity activity) {

        this.activity = activity;
        this.formatedata = new ArrayList<UserPojo>();
        this.fragmentInfoCercle = fragmentInfoCercle;

    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {
        Log.d("DELETE : ","DAT DELETE BOBY");
        //TODO
        //STEP 1 :
        //charger le liste des cercles

        //STEP 2 :
        //Prendre le premier dans la liste
        //setCurrentCercle

        //STEP 3 :
        //Mettre a jour le fragmentInfoCercle


    }


    @Override
    public void failure(RetrofitError error) {
        Log.d("GET_PARTICIPANTS : ",error.toString());
    }
}
