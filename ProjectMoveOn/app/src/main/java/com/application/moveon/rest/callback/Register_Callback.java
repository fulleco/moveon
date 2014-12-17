package com.application.moveon.rest.callback;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;

import com.application.moveon.model.User;
import com.application.moveon.tools.ToolBox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 17/12/2014.
 */
public class Register_Callback implements Callback<String> {

    private String id = null;
    private Activity previousActivity;
    private User newUser;
    private ToolBox tools;
    private String error = "";
    private String picturePath = "";
    private AnimationDrawable mailAnimation;

    public Register_Callback(Activity i, User user, AnimationDrawable mailAnimation, String picturePath){
        this.previousActivity = i;
        this.newUser = user;
        this.mailAnimation = mailAnimation;
    }

    @Override
    public void success(String s, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {

    }
}
