package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.friends.UserAdapter;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 13/01/2015.
 */
public class UpdateFriends_Callback implements Callback<UserPojo[]> {

    private ArrayList<UserPojo> formatedata;

    public UpdateFriends_Callback() {
        this.formatedata = new ArrayList<UserPojo>();
    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {

        MoveOnDB bdd = MoveOnDB.getInstance();
        ArrayList<UserPojo> datas;

        if(userPojos == null) {
            datas =new ArrayList<UserPojo>();
        }else{
            datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
        }

        bdd.updateFriends(datas);

        Flags.setFriendflag(true);
        Flags.checkupdate();


    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("MOVEON UPDATEFRIEND", "NOT WORKING");
    }
}
