package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 28/01/2015.
 */
public class AnswerDemand_Callback implements Callback<Boolean> {

    private Context c;
    private ProgressDialog p;

    public AnswerDemand_Callback(Context c, ProgressDialog p){

        this.c = c;
        this.p = p;
    }
    @Override
    public void success(Boolean aBoolean, Response response) {
        if(aBoolean){
            Toast.makeText(c, "Réponse envoyée", Toast.LENGTH_SHORT);
            p.hide();
        }else{
            Toast.makeText(c, "Problème lors de l'envoie de la réponse", Toast.LENGTH_SHORT);
            p.hide();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(c, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT);
    }
}
