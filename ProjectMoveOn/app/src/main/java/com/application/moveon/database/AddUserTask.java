package com.application.moveon.database;

/**
 * Created by damota on 25/11/2014.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.ToolBox;
import com.application.moveon.model.User;

public class AddUserTask extends AsyncTask<Void, Void, String> {

    private String id = null;
    private Activity previousActivity;
    private User newUser;
    private ToolBox tools;
    private String error = "";

    public AddUserTask(Activity i, User user) {
        this.previousActivity = i;
        this.newUser = user;
        tools = new ToolBox(i);
    }

    protected String doInBackground(Void... args) {
        error = userExists();
        if(!error.equals("")){
            previousActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(previousActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }else{
            try {
                addUser();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void onPostExecute(String result) {
        // Intent myIntent = new Intent(previousIntent, Edition.class);
        // myIntent.putExtra("id", id);
        // startActivity(myIntent);
        if(this.id!=null){
            tools.alertUser("Inscription terminée",
                    "Vous pouvez maintenant utiliser MoveOn !");
            Intent i = new Intent(previousActivity, LoginActivity.class);
            previousActivity.startActivity(i);
        }
    }

    private String addUser() throws JSONException {

        InputStream is = null;

        // http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://mobile.linkibe.fr/appli_hiddenphoto/add_user.php");
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("login",new StringBody(newUser.getLogin()));
            mpEntity.addPart("mail",new StringBody(newUser.getMail()));
            mpEntity.addPart("password",new StringBody(newUser.getPassword()));
            httppost.setEntity(mpEntity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = "";

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

        JSONObject jobject = null;

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
            jobject = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        id = jobject.getString("id");
        Log.i("ANTHO", "idrecupere" + id);

        return id;
    }

    private String userExists(){

        String error = "";

        InputStream is = null;

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(
                    "http://mobile.linkibe.fr/appli_hiddenphoto/account_exists.php?login=" + newUser.getLogin() + "&mail=" + newUser.getMail());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String resultString = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            resultString = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        JSONArray jArray = null;
        JSONObject jObject;
        try {
            jArray = new JSONArray(resultString);
            Log.i("ANTHO","testarray"+jArray.toString());
            jObject = jArray.getJSONObject(0);
            String nbLogin = jObject.getString("nbLogin");
            String nbEmail = jObject.getString("nbEmail");

            Log.i("ANTHO", "nblogin"+nbLogin);
            Log.i("ANTHO", "nbEmail"+nbEmail);

            if(!nbLogin.equals("0")){
                error+="Login déjà existant.\n";
            }

            if(!nbEmail.equals("0")){
                error+="Email déjà utilisé par un autre compte.\n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }
}
