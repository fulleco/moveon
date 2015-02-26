package com.application.moveon;

import android.app.Activity;
import android.os.Bundle;

import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.UpdateCirlces_Callback;
import com.application.moveon.rest.callback.UpdateDemands_Callback;
import com.application.moveon.rest.callback.UpdateFriends_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.Flags;

/**
 * Created by Hugo on 24/02/2015.
 */
public class Splashscreen extends Activity {

    private MoveOnDB db;
    private SessionManager session;
    MoveOnService mos;
    MoveOnService mosonchild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new SessionManager(this);

        db = MoveOnDB.getInstance();
        db.initialize(this.getBaseContext(),session.getUserDetails().get(SessionManager.KEY_EMAIL));
        db.open();

        Flags.initialize(this);
        mos = new RestClient(true).getApiService();
        mosonchild = new RestClient(false).getApiService();

    }

    @Override
    public void onStart(){
        super.onStart();
        session.checkLogin(false);

        mos.getfriends(session.getUserDetails().get(SessionManager.KEY_EMAIL),new UpdateFriends_Callback());
        mos.getdemands(session.getUserDetails().get(SessionManager.KEY_EMAIL), new UpdateDemands_Callback());
        mosonchild.getCercles(session.getUserDetails().get(SessionManager.KEY_EMAIL), new UpdateCirlces_Callback());
    }
}
