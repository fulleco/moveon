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
 * Created by Hugo on 13/01/2015.
 */
public class UpdateFriends_Callback implements Callback<UserPojo[]> {

    private MoveOnDB db;

    public UpdateFriends_Callback(MoveOnDB db) {
        this.db = db;
    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {

        ArrayList<UserPojo> datas;

        if(userPojos == null) {
            datas =new ArrayList<UserPojo>();
        }else{
            datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
        }
        db.open();
        db.updateFriends(datas);
        db.close();

        Flags.setFriendflag(true);
        Flags.checkupdate();


    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("MOVEON UPDATEFRIEND", "NOT WORKING");
    }
}
