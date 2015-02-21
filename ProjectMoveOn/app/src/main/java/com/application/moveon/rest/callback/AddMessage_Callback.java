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
public class AddMessage_Callback implements Callback<Integer> {
    private Activity activity;
    private ProgressDialog p;

    public AddMessage_Callback(Activity m_activity, ProgressDialog m_p){
        this.activity = m_activity;
        this.p = m_p;
    }

    @Override
    public void success(Integer aint, Response response) {
        if(aint == 1){

            Toast.makeText(activity, "Le message a bien été envoyé", Toast.LENGTH_SHORT).show();
            p.hide();
        }else{
            Toast.makeText(activity, "Problème lors de l'envoi du message", Toast.LENGTH_SHORT).show();
            p.hide();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(activity, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
        p.hide();
    }
}
