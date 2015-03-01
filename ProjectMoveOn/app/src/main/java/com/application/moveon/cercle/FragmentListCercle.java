package com.application.moveon.cercle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetCercles_Callback;
import com.application.moveon.rest.callback.GetFriendsPicker_Callback;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Quentin Bitschene on 19/02/2015.
 */
public class FragmentListCercle extends Fragment {

    private static View view;
    private LayoutInflater mInflater;
    private ListView list_cercles;
    MoveOnDB moveOnDB;

    SessionManager session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_cercle, container, false);
        list_cercles = (ListView)view.findViewById(R.id.list_view);

        session = new SessionManager(getActivity());

        moveOnDB = new MoveOnDB(this.getActivity().getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        moveOnDB.open();

        ArrayList<CerclePojo> cerclePojos = moveOnDB.getCircles();
        moveOnDB.close();

        updateView(cerclePojos);

        return view;
    }

    public void updateView(ArrayList<CerclePojo> cerclePojos) {

        if(cerclePojos==null)
            return;

        final ListCercleAdapter a = new ListCercleAdapter(cerclePojos, getActivity(), (FragmentInfoCercle)getTargetFragment());
        list_cercles.setAdapter(a);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // this is really important in order to save the state across screen
        // configuration changes for example
        setRetainInstance(true);

        mInflater = LayoutInflater.from(getActivity());
    }
}
