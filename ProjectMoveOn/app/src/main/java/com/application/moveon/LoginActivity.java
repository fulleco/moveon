package com.application.moveon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
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

    private EditText editLogin;
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

        editLogin = (EditText) findViewById(R.id.editLogin);
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

                final String loginTxt = editLogin.getText().toString();
                final String passTxt = editPassword.getText().toString();

                logo.setBackgroundResource(R.drawable.loader_moveon);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
