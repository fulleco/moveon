package com.application.moveon.rest.callback;

import android.util.Log;

import com.application.moveon.rest.modele.UserPojo;
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
public class UpdateParticipants_Callback implements Callback<UserPojo[]> {

    private MoveOnDB db;

    public UpdateParticipants_Callback(MoveOnDB db){
        this.db = db;
    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {
        ArrayList<UserPojo> datas;

        if(userPojos == null) {
            datas =new ArrayList<UserPojo>();
        }else{
            datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
            db.open();
            db.updateParticipants(datas);
            db.close();
        }

        //for( UserPojo u)


        Flags.setParticipantsflag(true);
        Flags.checkupdate();

    }

    @Override
    public void failure(RetrofitError error) {
        //Log.i("MOVEON UPDATEPARTICIPANTS", "NOT WORKING");
        //Log.i("MOVEON UPDATEPARTICIPANTS", error.toString());
    }
}
