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

import com.application.moveon.rest.callback.Connect_Callback;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;

import java.util.ArrayList;


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

        final RestClient r = new RestClient(true);
        final MoveOnService mos = r.getApiService();

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

                ArrayList<String> emptyfields = validFields();

                if(validFields().size() == 0){

                    final String mail = editEmail.getText().toString();
                    final String password = editPassword.getText().toString();

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

                    Connect_Callback c = new Connect_Callback(LoginActivity.this, mail,password,session,mailAnimation);
                    mos.selectuser(mail,password,c);


                }else{
                    Toast.makeText(LoginActivity.this, "Veuillez remplir les deux champs.", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();
        if (editEmail.getText().toString().equals(""))
            fieldsEmpty.add("Email");
        if (editPassword.getText().toString().equals(""))
            fieldsEmpty.add("Mot de passe");
        return fieldsEmpty;
    }
}
