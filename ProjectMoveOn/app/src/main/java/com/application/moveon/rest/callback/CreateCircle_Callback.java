package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 18/02/2015.
 */
public class CreateCircle_Callback implements Callback<Integer> {

    private Activity activity;
    private ProgressDialog p;

    public CreateCircle_Callback(Activity activity, ProgressDialog p) {
        this.activity = activity;
        this.p = p;
    }

    @Override
    public void success(Integer aint, Response response) {

        if(aint == 0){
            Toast.makeText(activity,"Vous avez déjà créé un cercle possèdant ce titre",Toast.LENGTH_SHORT).show();
            p.hide();
        }
        else if(aint == 1){
            Toast.makeText(activity, "La création du cercle a été effectuée", Toast.LENGTH_SHORT).show();
            p.hide();
        }else if(aint == 2){
            Toast.makeText(activity, "Problème lors de la création du cercle", Toast.LENGTH_SHORT).show();
            p.hide();
        }else if(aint == 3) {
            Toast.makeText(activity, "Problème lors de l'envoie des invitations", Toast.LENGTH_SHORT).show();
            p.hide();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(activity, "Impossible de contacter le serveur", Toast.LENGTH_SHORT).show();
        p.hide();
    }
}
