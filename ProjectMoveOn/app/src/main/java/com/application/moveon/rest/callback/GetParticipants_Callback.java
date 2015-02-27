package com.application.moveon.rest.callback;

import android.app.Activity;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.HomeActivity;
import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class GetParticipants_Callback implements Callback<UserPojo[]> {


    private Activity activity;
    private ListView list;
    private ArrayList<UserPojo> formatedata;
    private FragmentInfoCercle fragmentInfoCercle;

    public GetParticipants_Callback(Activity activity, ListView list, FragmentInfoCercle fragmentInfoCercle) {

        this.activity = activity;
        this.list = list;
        this.formatedata = new ArrayList<UserPojo>();
        this.fragmentInfoCercle = fragmentInfoCercle;

    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {

        if(userPojos == null) return;
        ArrayList<UserPojo> datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));

        HomeActivity homeActivity = (HomeActivity) fragmentInfoCercle.getActivity();

        UserPojo[] filled = new UserPojo[0];
        homeActivity.getCurrentCercle().setParticipants(datas.toArray(filled));
        CerclePojo c = homeActivity.getCurrentCercle();
        fragmentInfoCercle.updateContent();
    }


    @Override
    public void failure(RetrofitError error) {
        Log.d("GET_PARTICIPANTS : ",error.toString());
    }
}
