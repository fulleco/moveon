package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 12/01/2015.
 */
public class AddFriend_Callback implements Callback<Integer> {

    private Context c;
    private ProgressDialog p;
    String iduser;

    public AddFriend_Callback(Context m_activity, ProgressDialog m_p, String iduser){
        this.c = m_activity;
        this.p = m_p;
        this.iduser = iduser;
    }

    @Override
    public void success(Integer aint, Response response) {
        if(aint == 1){
            makeAToast("La demande d'ajout a bien été envoyée");
        }else if(aint == 2){
            makeAToast("Vous avez déjà demandé cet utilisateur en ami");
        }else if(aint == 3) {
            makeAToast("Ce compte n'existe pas");
        }else if(aint == 4){
            makeAToast(iduser + " a été ajouté à vos amis");
            MoveOnService mos = new RestClient(true).getApiService();
            mos.addfriendtodb(iduser, new UpdateFriend_Callback(c));
        }else{
           makeAToast("Problème lors de la demande en ami");
        }
    }

    @Override
    public void failure(RetrofitError error) {
       makeAToast("Impossible de contacter le serveur");
    }

    public void makeAToast(final String message){
        ((Activity)c).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                p.hide();
            }
        });
    }
}
