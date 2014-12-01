package com.application.moveon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.moveon.database.AddUserTask;
import com.application.moveon.database.ConnectTask;
import com.application.moveon.database.UpdateUserTask;
import com.application.moveon.model.User;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ProfilActivity extends Activity {

    private EditText editFirstName;
    private EditText editLastName;
    private ToolBox tools;
    private ImageView logo;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        tools = new ToolBox(this);
        logo = (ImageView) findViewById(R.id.logo);

        // Remplir les champs par leur valeur actuelle
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        session = new SessionManager(this);
        editFirstName.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME));

        editLastName = (EditText) findViewById(R.id.editLastName);
        editLastName.setText(session.getUserDetails().get(SessionManager.KEY_LASTNAME));

        Button validateButton = (Button) findViewById(R.id.buttonValidate);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> emptyFields = validFields();
                String message = "";
                if (emptyFields.size()==0) {

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

                    // mettre a jour la BDD avec la valeur dans les champs
                    User newUser = new User(session.getUserDetails().get(SessionManager.KEY_ID),session.getUserDetails().get(SessionManager.KEY_LOGIN),
                            session.getUserDetails().get(SessionManager.KEY_PASSWORD), editFirstName.getText().toString(), editLastName.getText().toString());
                    new UpdateUserTask(ProfilActivity.this, newUser, mailAnimation).execute();

                }else{
                    for (String field : emptyFields)
                        message += "-" + field + "\n";
                    tools.alertUser("Champs manquants", message);
                    return;
                }

            }
        });




    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();

        if (editLastName.getText().toString().equals(""))
            fieldsEmpty.add("Nom de famille");
        if (editFirstName.getText().toString().equals(""))
            fieldsEmpty.add("Prénom");
        return fieldsEmpty;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_disconnect) {
            SessionManager session = new SessionManager(this);
            session.logoutUser();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_settings) {
        }
        return super.onOptionsItemSelected(item);
    }
}
