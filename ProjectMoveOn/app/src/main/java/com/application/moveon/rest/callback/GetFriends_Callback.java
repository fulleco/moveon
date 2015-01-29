package com.application.moveon.rest.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.friends.UserAdapter;
import com.application.moveon.rest.modele.UserPojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 13/01/2015.
 */
public class GetFriends_Callback implements Callback<UserPojo[]> {

    private String id;
    private Activity activity;
    private UserAdapter adapter;
    private ListView list;
    private ProgressDialog progressDialog;
    private ArrayList<UserPojo> formatedata;

    public GetFriends_Callback(Activity activity, ListView list, UserAdapter adapter) {
        this.adapter = adapter;
        this.activity = activity;
        this.list = list;
        this.formatedata = new ArrayList<UserPojo>();

    }

    @Override
    public void success(UserPojo[] userPojos, Response response) {

        if(userPojos == null) return;
        ArrayList<UserPojo> datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));
        Collections.sort(datas);
        final ListView lv = (ListView) activity.findViewById(R.id.list_contacts);
        final UserAdapter a = new UserAdapter(datas, activity.getBaseContext());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(a);
            }
        });




    }

    @Override
    public void failure(RetrofitError error) {

    }
}
