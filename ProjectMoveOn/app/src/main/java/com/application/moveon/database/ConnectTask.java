package com.application.moveon.database;

/**
 * Created by user on 24/11/2014.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.session.SessionManager;

public class ConnectTask extends AsyncTask<Void, Void, String> {

    LoginActivity activity;
    String loginTxt;
    String passTxt;
    SessionManager session;
    String id;
    ProgressDialog progressBar;

    public ConnectTask(LoginActivity activity, String loginTxt,
                       String passTxt, SessionManager session, String id, ProgressDialog progressBar) {
        this.activity = activity;
        this.loginTxt = loginTxt;
        this.passTxt = passTxt;
        this.session = session;
        this.id = id;
        this.progressBar = progressBar;
    }

    protected void onPreExecute()
    {
        progressBar.show();
    }

    protected String doInBackground(Void... args) {

        // Vérifier si l'un des deux champs est vide
        if (loginTxt.equals("") || passTxt.equals("")) {
            activity.runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(activity, "Veuillez remplir les deux champs.", Toast.LENGTH_SHORT).show();
                }
            });
            return new String("Erreur");
        }

        try {
            if(!userAlreadyExists(loginTxt, passTxt)){
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(activity, "Compte inexistant", Toast.LENGTH_SHORT).show();
                    }
                });
                return new String("Login ou mot de passe incorrect.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new String("Connexion");
    }

    protected void onPostExecute(String result) {
        if(result.equals(new String("Connexion"))){
            session.createLoginSession(loginTxt, passTxt, id);
            dismissBar();
        }
    }

	private void dismissBar(){
		if(progressBar != null && progressBar.isShowing())
        {
			progressBar.dismiss();
        }
	}

    @SuppressWarnings("unchecked")
    public boolean userAlreadyExists(String login, String password) throws InterruptedException, ExecutionException, JSONException{
        JSONArray jArray = null;
        String result = "";
        InputStream is = null;
        // the year data to send
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("login", login));
        nameValuePairs.add(new BasicNameValuePair("password", password));

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://mobile.linkibe.fr/appli_hiddenphoto/select_user_by_login.php?login="
                            + login + "&password=" + password);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }
        // convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        // parse json data
        try {
            jArray = new JSONArray(result);

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        if (jArray != null) {
            JSONObject jObject = jArray.getJSONObject(0);
            id = jObject.getString("id");
        }
        return (jArray!=null);
    }
}
