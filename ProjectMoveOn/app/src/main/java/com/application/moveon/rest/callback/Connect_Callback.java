package com.application.moveon.rest.callback;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 16/12/2014.
 */
public class Connect_Callback implements Callback<UserPojo> {

    private LoginActivity act;
    private String mail;
    private String password;
    private SessionManager session;
    private CustomProgressDialog p;

    public Connect_Callback(LoginActivity m_act, String m_mail, String m_password, SessionManager m_session, CustomProgressDialog p) {
        act = m_act;
        mail = m_mail;
        password = m_password;
        session = m_session;
        this.p = p;
    }


    @Override
    public void success(UserPojo aUser, Response response) {
        if (aUser.getFirstname() == null) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(act, "Login ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            });
            p.dismiss();
        } else {
            session.createLoginSession(mail, password, String.valueOf(aUser.getId_client()), aUser.getFirstname(), aUser.getLastname());
            p.dismiss();
        }
    }
    @Override
    public void failure(RetrofitError error) {
        Toast.makeText(act, "Impossible de se connecter au serveur", Toast.LENGTH_SHORT).show();
        p.dismiss();
    }


}
