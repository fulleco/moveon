package com.application.moveon.rest.callback;

import android.app.Activity;

import com.application.moveon.model.User;
import com.application.moveon.tools.ToolBox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 21/12/2014.
 */
public class EditUser_Callback implements Callback<Boolean> {

    private Activity previousActivity;
    private User newUser;
    private ToolBox tools;

    public  EditUser_Callback(User newUser,ToolBox tools){

        this.newUser = newUser;
        this.tools = tools;
    }

    @Override
    public void success(Boolean aBoolean, Response response) {
        if(aBoolean){
            tools.alertUser("Edition réussie",
                    "Votre profil a été modifié avec succès");
        }else{
            tools.alertUser("Edition impossible",
                    "Erreur lors de l'édition du profil");
        }
    }

    @Override
    public void failure(RetrofitError error) {
        tools.alertUser("Edition impossible",
                "Impossible de joindre le serveur");
    }


}
