package com.application.moveon.rest.callback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.ftp.FtpUploadTask;
import com.application.moveon.model.User;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.tools.ToolBox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 17/12/2014.
 */
public class Register_Callback implements Callback<Boolean> {

    private String id = null;
    private Activity previousActivity;
    private User newUser;
    private ToolBox tools;
    private String picturePath = "";
    private AnimationDrawable mailAnimation;
    private MoveOnService mos;

    public Register_Callback(Activity i, User user, AnimationDrawable mailAnimation, String picturePath, ToolBox tools){
        this.previousActivity = i;
        this.newUser = user;
        this.mailAnimation = mailAnimation;
        this.picturePath = picturePath;
        this.tools = tools;
        RestClient r = new RestClient(false);
        mos = r.getApiService();
    }

    @Override
    public void success(final Boolean b, Response response) {

        if(b){
            msgOnUiThread("Inscription impossible","L'email saisie est déjà utilisé");

        }else{
            int id = mos.adduser(newUser.getFirstName(),newUser.getLastName(),newUser.getLogin(),newUser.getPassword(),picturePath);
            if(id!= 0){
                new FtpUploadTask(picturePath, "profile.jpg", Integer.toString(id)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                msgOnUiThread("Inscription reussie",  "Vous êtes maintenant inscript sur moveon");
                Intent i = new Intent(previousActivity, LoginActivity.class);
                previousActivity.startActivity(i);
            }else{
                msgOnUiThread("Inscription impossible",  "Impossible de contacter le serveur");
            }
        }

        mailAnimation.stop();



    }

    @Override
    public void failure(RetrofitError error) {
        msgOnUiThread("Inscription impossible", "Connexion au serveur impossible");

    }

    private void msgOnUiThread(final String title, final String msg){
        try {
            previousActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(previousActivity, msg, Toast.LENGTH_SHORT).show();
                }
            });
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
