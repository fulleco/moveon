package com.application.moveon.rest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.rest.modele.User;
import com.application.moveon.session.SessionManager;
import com.squareup.okhttp.Call;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 16/12/2014.
 */
public class Connect_Callback implements Callback<User> {


    private LoginActivity act;
    private String mail;
    private String password;
    private SessionManager session;
    private AnimationDrawable mailAnimation;

    public Connect_Callback(LoginActivity m_act, String m_mail, String m_password, SessionManager m_session, AnimationDrawable m_mailAnimation){
        act = m_act;
        mail = m_mail;
        password = m_password;
        session = m_session;
        mailAnimation = m_mailAnimation;
    }


    @Override
    public void success(User aUser, Response response) {
        if(aUser.getFirstname() == null){

            act.runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(act, "Login ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            });

            mailAnimation.stop();

        }else{
            session.createLoginSession(mail, password, aUser.getId_client(), aUser.getFirstname(),aUser.getLastname());
            mailAnimation.stop();

        }


    }

    @Override
    public void failure(RetrofitError error) {
        Log.i("Hugo", error.getMessage());
    }


}
