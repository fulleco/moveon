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
import com.application.moveon.session.SessionManager;

/**
 * Created by Quentin Bitschene on 19/02/2015.
 */
public class FragmentListCercle extends Fragment {

    private static View view;
    private LayoutInflater mInflater;
    private ListView list_cercles;

    SessionManager session;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_cercle, container, false);
        list_cercles = (ListView)view.findViewById(R.id.list_view);

        session = new SessionManager(getActivity());

        RestClient r = new RestClient(true);
        MoveOnService mos = r.getApiService();
        mos.getCercles(session.getUserDetails().get(SessionManager.KEY_EMAIL),new GetCercles_Callback(getActivity(),list_cercles,(FragmentInfoCercle)getTargetFragment()));

        return view;
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
