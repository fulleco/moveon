package com.application.moveon.rest.callback;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.ftp.FtpUploadTask;
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
    private HomeActivity homeactivity;
    private CustomProgressDialog cpd;

    public  EditUser_Callback(String picturePath, User newUser, ToolBox tools, HomeActivity homeactivity, CustomProgressDialog cpd){
        this.newUser = newUser;
        this.tools = tools;
        this.cpd = cpd;
        new FtpUploadTask(picturePath, "profile.jpg", String.valueOf(String.valueOf(newUser.getId()))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.homeactivity = homeactivity;
    }

    @Override
    public void success(Boolean aBoolean, Response response) {
        if(aBoolean){
            cpd.dismiss();
            Toast.makeText(homeactivity.getBaseContext(), "Modification du profil réussie", Toast.LENGTH_SHORT).show();
            homeactivity.switchFragment(homeactivity.getFragmentViewProfil());
        }else{
            cpd.dismiss();
            Toast.makeText(homeactivity.getBaseContext(), "Erreur lors de la modification du profil", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        cpd.dismiss();
        Toast.makeText(homeactivity.getBaseContext(), "Impossible de joindre le serveur", Toast.LENGTH_SHORT).show();

    }


}
