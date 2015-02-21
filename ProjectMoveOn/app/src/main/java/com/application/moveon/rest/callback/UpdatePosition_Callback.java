package com.application.moveon.rest.callback;

import android.app.Activity;

import com.application.moveon.model.User;
import com.application.moveon.tools.ToolBox;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 21/12/2014.
 */
public class UpdatePosition_Callback implements Callback<Boolean> {

    public UpdatePosition_Callback(){
    }

    @Override
    public void success(Boolean aBoolean, Response response) {
    }

    @Override
    public void failure(RetrofitError error) {
    }


}
