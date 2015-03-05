package com.application.moveon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.rest.callback.Connect_Callback;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


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
        final Button registerButton = (Button) findViewById(R.id.buttonRegister);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),
                "fonts/BebasNeue.otf");


        editEmail.setTypeface(custom_font);
        editPassword.setTypeface(custom_font);
        registerButton.setTypeface(custom_font);


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

                    final String mail = editEmail.getText().toString().toLowerCase();
                    final String password = editPassword.getText().toString();

                    //logo.setBackgroundResource(R.drawable.login_loader);

                    CustomProgressDialog p = new CustomProgressDialog(LoginActivity.this);
                    p.show();

                    Connect_Callback c = new Connect_Callback(LoginActivity.this, mail,password,session,p);
                    mos.selectuser(mail,password,c);


                }else{
                    Toast.makeText(LoginActivity.this, "Veuillez remplir les deux champs.", Toast.LENGTH_SHORT).show();
                }



            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/BebasNeue.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();
        if (editEmail.getText().toString().equals(""))
            fieldsEmpty.add("Email");
        if (editPassword.getText().toString().equals(""))
            fieldsEmpty.add("Mot de passe");
        return fieldsEmpty;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
