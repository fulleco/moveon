package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by user on 18/02/2015.
 */
public class AddMessages_Callback implements Callback<Integer> {
    private Activity activity;
    private ProgressDialog p;

    public AddMessages_Callback(Activity m_activity, ProgressDialog m_p){
        this.activity = m_activity;
        this.p = m_p;
    }

    @Override
    public void success(Integer aint, Response response) {
        if(activity == null)
            return;
        if(aint == 1){

            Toast.makeText(activity, "Les message ont bien été envoyés", Toast.LENGTH_SHORT).show();
            p.hide();
        }else{
            Toast.makeText(activity, "Problème lors de l'envoi des messages", Toast.LENGTH_SHORT).show();
            p.hide();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if(activity == null)
            return;

        Toast.makeText(activity, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
        p.hide();
    }
}
