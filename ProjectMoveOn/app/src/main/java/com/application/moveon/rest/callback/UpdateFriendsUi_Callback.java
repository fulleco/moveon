package com.application.moveon.rest.callback;

import android.app.Activity;
import android.widget.ListView;

import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.friends.adapter.DemandsAdapter;
import com.application.moveon.friends.adapter.UserAdapter;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hugo on 05/03/2015.
 */
public class UpdateFriendsUi_Callback implements Callback<UserPojo[]> {

    private MoveOnDB db;
    private Activity a;
    private CustomProgressDialog p;
    private ListView lv;
    private SessionManager sm;

    public UpdateFriendsUi_Callback(MoveOnDB db, Activity a, CustomProgressDialog p, ListView lv) {
        this.db = db;
        this.a = a;
        this.p = p;
        this.lv = lv;
        this.sm = new SessionManager(a);
    }
    @Override
    public void success(final UserPojo[] userPojos, Response response) {

        final ArrayList<UserPojo> datas;
        if(userPojos != null) {
            datas = new ArrayList<UserPojo>(Arrays.asList(userPojos));

        }else{
            datas = new ArrayList<UserPojo>();
        }

        db.open();
        db.updateFriends(datas);

        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lv.setAdapter(new UserAdapter(datas, a.getBaseContext()));
                p.dismiss();
            }
        });
        db.close();

    }

    @Override
    public void failure(RetrofitError error) {

    }
}
