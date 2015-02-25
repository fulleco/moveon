package com.application.moveon.rest.callback;

import android.app.Activity;
import android.widget.Toast;

import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.sqlitedb.MoveOnDB;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 25/02/2015.
 */
public class UpdateFriend_Callback implements Callback<UserPojo> {

    private Activity a;

    public UpdateFriend_Callback(Activity a) {
        this.a = a;
    }

    @Override
    public void success(UserPojo userPojo, Response response) {
        MoveOnDB db = MoveOnDB.getInstance();
        db.insertUser(userPojo);
    }

    @Override
    public void failure(RetrofitError error) {
       makeAToast("Connexion impossible");
    }

    private void makeAToast(final String message){
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(a, message, Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
