package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.sqlitedb.MoveOnDB;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 28/01/2015.
 */
public class AnswerDemand_Callback implements Callback<Boolean> {

    private Context c;
    private ProgressDialog p;
    private String iduser;
    private boolean adding;
    private MoveOnDB db;

    public AnswerDemand_Callback(Context c, ProgressDialog p, String iduser, boolean adding, MoveOnDB db){

        this.c = c;
        this.p = p;
        this.iduser = iduser;
        this. adding = adding;
        this.db = db;
    }
    @Override
    public void success(Boolean aBoolean, Response response) {
        if(aBoolean){
            makeAToast( "Réponse envoyée");
            if(adding){
                Log.i("MOVEON ANWERDEMAND", iduser);
                MoveOnService mos = new RestClient(true).getApiService();
                mos.addfriendtodb(iduser, new UpdateFriend_Callback(c,db));
            }


        }else{
            makeAToast("Problème lors de l'envoie de la réponse");
        }
    }

    @Override
    public void failure(RetrofitError error) {
        makeAToast("Impossible de se connecter au serveur");
    }

    private void makeAToast(final String message){
        ((Activity)c).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                p.hide();
            }
        });
    }
}
