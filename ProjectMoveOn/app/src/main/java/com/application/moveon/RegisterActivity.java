package com.application.moveon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.application.moveon.database.AddUserTask;
import com.application.moveon.model.User;
import com.application.moveon.session.SessionManager;

import java.util.ArrayList;

/**
 * Created by damota on 25/11/2014.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private Button buttonRegister;
    private Button buttonConnect;
    private EditText editLogin;
    private EditText editPassword1;
    private EditText editPassword2;
    private EditText editMail;
    private ToolBox tools;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SessionManager session = new SessionManager(this);
        //session.checkLogin(true);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(this);

        editLogin = (EditText) findViewById(R.id.editLogin);
        editPassword1 = (EditText) findViewById(R.id.editPassword1);
        editPassword2 = (EditText) findViewById(R.id.editPassword2);
        editMail = (EditText) findViewById(R.id.editMail);

        tools = new ToolBox(this);
    }

    @Override
    public void onClick(View v) {

        //progressDialog = ProgressDialog.show(this, "", "Connexion en cours...", true);

        if (v == buttonConnect) {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            return;
        }

        if (v == buttonRegister) {
            if (!checkPassword()) {
                tools.alertUser("Vérification du mot de passe",
                        "Les deux champs mot de passe ne correspondent pas");
                return;
            }

            ArrayList<String> emptyFields = validFields();
            String message = "";
            if (emptyFields.size()==0) {
                User newUser = new User(editLogin.getText().toString(),
                        editPassword1.getText().toString(), editMail.getText()
                        .toString());
                new AddUserTask(this, newUser).execute();
            }else{
                for (String field : emptyFields)
                    message += "-" + field + "\n";
                tools.alertUser("Champs manquants", message);
                return;
            }
        }
    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();
        if (editLogin.getText().toString().equals(""))
            fieldsEmpty.add("Login");
        if (editMail.getText().toString().equals(""))
            fieldsEmpty.add("Mail");
        if (editPassword1.getText().toString().equals(""))
            fieldsEmpty.add("Mot de passe");
        return fieldsEmpty;
    }

    public boolean checkPassword() {
        if (!editPassword1.getText().toString()
                .equals(editPassword2.getText().toString()))
            return false;
        return true;
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
        return super.onOptionsItemSelected(item);
    }
}
