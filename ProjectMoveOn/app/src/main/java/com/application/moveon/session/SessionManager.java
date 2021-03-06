package com.application.moveon.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.application.moveon.LoginActivity;
import com.application.moveon.RegisterActivity;
import com.application.moveon.Splashscreen;
import com.application.moveon.rest.modele.UserPojo;

import java.util.HashMap;

/**
 * Created by damota on 24/11/2014.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    public static final int PRIVATE_MODE = 0;

    // Sharedpref file name
    public static final String PREF_NAME = "iDarePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ID = "id";
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String password, String id, String firstname, String lastname){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_FIRSTNAME, firstname);
        editor.putString(KEY_LASTNAME, lastname);

        // commit changes
        editor.commit();

        Intent i = new Intent(_context,
                Splashscreen.class);

        _context.startActivity(i);

        ((Activity) _context).finish();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(boolean redirect){

        // Check login status
        if(!this.isLoggedIn()){

            if(_context.getClass().equals(LoginActivity.class)
                    &&(_context.getClass().equals(RegisterActivity.class)))
                return;

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            ((Activity) _context).finish();
        }else if(redirect){
            Intent i = new Intent(_context, Splashscreen.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("caller", _context.getClass().getName());
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_FIRSTNAME, pref.getString(KEY_FIRSTNAME, null));
        user.put(KEY_LASTNAME, pref.getString(KEY_LASTNAME, null));

        // return user
        return user;
    }

    public UserPojo getUserPojo(){
        UserPojo up = new UserPojo();

        up.setLogin(pref.getString(KEY_EMAIL, null));
        up.setFirstname(pref.getString(KEY_FIRSTNAME, null));
        up.setLastname(pref.getString(KEY_LASTNAME, null));
        String idClient = pref.getString(KEY_ID, null);
        if(idClient!=null)
            up.setId_client(Integer.valueOf(idClient));
        return up;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, Splashscreen.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

        ((Activity) _context).finish();
    }

    public SharedPreferences getPref() {
        return pref;
    }

    public void setPref(SharedPreferences pref) {
        this.pref = pref;
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
