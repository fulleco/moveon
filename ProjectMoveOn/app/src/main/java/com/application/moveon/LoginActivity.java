package com.application.moveon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.moveon.database.ConnectTask;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class LoginActivity extends Activity {

    private EditText editEmail;
    private EditText editPassword;

    private String id;

    private SessionManager session;

    private ProgressDialog progressDialog;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Connexion en cours");

        session = new SessionManager(this);

        logo = (ImageView) findViewById(R.id.logo);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);

        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

            Button loginButton = (Button) findViewById(R.id.buttonConnect);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {

                if(!Connectivity.isConnected(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "Pas de connexion.", Toast.LENGTH_LONG).show();
                    return;
                }

                final String loginTxt = editEmail.getText().toString();
                final String passTxt = editPassword.getText().toString();

                logo.setBackgroundResource(R.drawable.login_loader);

                final AnimationDrawable mailAnimation = (AnimationDrawable) logo.getBackground();
                logo.post(new Runnable() {
                    public void run() {
                        if ( mailAnimation != null ) mailAnimation.start();
                    }
                });

                //Calculate the total duration
                int duration = 0;
                for(int i = 0; i < mailAnimation.getNumberOfFrames(); i++){
                    duration += mailAnimation.getDuration(i);
                }

                int corePoolSize = 80;
                int maximumPoolSize = 90;
                int keepAliveTime = 20;

                BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
                Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

                new ConnectTask(LoginActivity.this, loginTxt, passTxt, session, id, mailAnimation).executeOnExecutor(threadPoolExecutor);
            }
        });
    }
}
