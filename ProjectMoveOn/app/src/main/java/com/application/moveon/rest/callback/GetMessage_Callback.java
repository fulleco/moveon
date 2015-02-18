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
public class GetMessage_Callback implements Callback<Integer> {
    private Activity activity;
    private ProgressDialog p;

    public GetMessage_Callback(Activity m_activity, ProgressDialog m_p){
        this.activity = m_activity;
        this.p = m_p;
    }

    @Override
    public void success(Integer aint, Response response) {
        if(aint == 1){

            Toast.makeText(activity, "Le message a bien été envoyé envoyée", Toast.LENGTH_SHORT).show();
            p.hide();
        }else if(aint == 2){
            Toast.makeText(activity, "Vous avez déjà demandé cet utilisateur en ami", Toast.LENGTH_SHORT).show();
            p.hide();
        }else if(aint == 3) {
            Toast.makeText(activity, "Ce compte n'existe pas", Toast.LENGTH_SHORT).show();
            p.hide();
        }else{
            Toast.makeText(activity, "Problème lors de la demande en ami", Toast.LENGTH_SHORT).show();
            p.hide();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(activity, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
        p.hide();
    }
}
