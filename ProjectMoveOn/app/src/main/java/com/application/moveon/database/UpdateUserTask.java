package com.application.moveon.database;

/**
 * Created by damota on 25/11/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.application.moveon.LoginActivity;
import com.application.moveon.model.User;
import com.application.moveon.tools.ToolBox;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class UpdateUserTask extends AsyncTask<Void, Void, String> {

    private String id = null;
    private Activity previousActivity;
    private User newUser;
    private ToolBox tools;
    private String error = "";


    public UpdateUserTask(Activity i, User user) {
        this.previousActivity = i;
        this.newUser = user;
        tools = new ToolBox(i);
    }

    protected String doInBackground(Void... args) {

            try {
                updateUser();
            } catch (JSONException e) {
                e.printStackTrace();
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

    private void updateUser() throws JSONException {

        InputStream is = null;

        // http post
        try {
            Log.i("Quentin", "USER firstname " + newUser.getFirstName() + " last " + newUser.getLastName());

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://martinezhugo.com/pfe/update_user.php");
            MultipartEntity mpEntity = new MultipartEntity();
            mpEntity.addPart("id",new StringBody(newUser.getId()));
            mpEntity.addPart("email",new StringBody(newUser.getLogin()));
            mpEntity.addPart("password",new StringBody(newUser.getPassword()));
            mpEntity.addPart("firstname",new StringBody(newUser.getFirstName()));
            mpEntity.addPart("lastname",new StringBody(newUser.getLastName()));

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





        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();


        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }


        return ;
    }

    private String userExists(){

        String error = "";

        InputStream is = null;

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(
                    "http://martinezhugo.com/pfe/account_exists.php?login=" + newUser.getLogin());
            HttpResponse response = httpclient.execute(httpget);
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
            jObject = jArray.getJSONObject(0);
            String nbLogin = jObject.getString("nbLogin");

            if(!nbLogin.equals("0")){
                error+="Login déjà existant.\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return error;
    }
}
