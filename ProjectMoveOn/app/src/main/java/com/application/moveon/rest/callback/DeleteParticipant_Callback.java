package com.application.moveon.rest.callback;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;

import com.application.moveon.HomeActivity;
import com.application.moveon.cercle.FragmentInfoCercle;
import com.application.moveon.cercle.FragmentListCercle;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Quentin Bitschene on 13/01/2015.
 */
public class DeleteParticipant_Callback implements Callback<Boolean> {


    private HomeActivity activity;
    private ListView list;
    private ArrayList<UserPojo> formatedata;
    private FragmentListCercle fragmentListCercle;
    private SessionManager session;

    public DeleteParticipant_Callback(Activity activity,Fragment fragmentListCercle) {

        this.activity = (HomeActivity)activity;
        this.formatedata = new ArrayList<UserPojo>();
        session = new SessionManager(activity.getBaseContext());
        this.fragmentListCercle =(FragmentListCercle) fragmentListCercle;

    }

    @Override
    public void success(Boolean found, Response response) {

        //TODO

        MoveOnDB moveOnDB = new MoveOnDB(activity.getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        moveOnDB.open();

        //STEP 1 :
        //charger le liste des cercles
        moveOnDB.deleteParticipant(session.getUserDetails().get(SessionManager.KEY_EMAIL), activity.getCurrentCercle().getId_cercle());

        ArrayList<CerclePojo> cerclePojos= moveOnDB.getCircles();
        moveOnDB.close();

        //STEP 2 : recharger la liste des cercles
        fragmentListCercle.updateView(cerclePojos);

        //STEP 3 :
        //SET LE CURRENT CERCLE
        activity.initCurrentCercle();


        //STEP 4 :
        //Mettre a jour le fragmentInfoCercle
        if(((FragmentInfoCercle)fragmentListCercle.getTargetFragment())!=null)
            ((FragmentInfoCercle)fragmentListCercle.getTargetFragment()).updateContent();


    }


    @Override
    public void failure(RetrofitError error) {
        Log.d("DELETE_PARTICIPANT : ",error.toString());
    }
}
