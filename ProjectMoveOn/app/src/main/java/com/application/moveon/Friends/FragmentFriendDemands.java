package com.application.moveon.friends;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.GetDemands_Callback;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

/**
 * Created by Hugo on 26/01/2015.
 */
public class FragmentFriendDemands extends Fragment {
    private TextView name;

    private Button buttonEditer;

    SessionManager session;
    private UserAdapter mainAdapter;

    private FragmentActivity activity;
    private ToolBox tools;

    MoveOnService childmos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demands, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);

        RestClient r = new RestClient(false);
        childmos = (new RestClient(false)).getApiService();

        String mail = session.getUserDetails().get(SessionManager.KEY_EMAIL);

        childmos.getdemands(mail, new GetDemands_Callback(mail, activity));

        if (!Connectivity.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Pas de connexion.",
                    Toast.LENGTH_LONG).show();
        } else {


        }

        return view;
    }
}
