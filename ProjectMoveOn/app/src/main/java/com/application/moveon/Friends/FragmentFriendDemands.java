package com.application.moveon.friends;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.R;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.friends.adapter.DemandsAdapter;
import com.application.moveon.friends.adapter.UserAdapter;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.UpdateDemandsUi_Callback;
import com.application.moveon.rest.modele.DemandsPojo;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;
import java.util.Collections;

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
    private ProgressDialog p;


    MoveOnService childmos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demands, container, false);

        final MoveOnService mos = new RestClient(true).getApiService();

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);
        TextView textv = (TextView)view.findViewById(R.id.no_demands);
        final ListView lv = (ListView) view.findViewById(R.id.list_demands);

        RestClient r = new RestClient(false);
        childmos = (new RestClient(false)).getApiService();

        String mail = session.getUserDetails().get(SessionManager.KEY_EMAIL);

        final MoveOnDB db = new MoveOnDB(getActivity().getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        db.open();
        ArrayList<DemandsPojo> datas = db.getDemands();
        db.close();

        ImageButton button = (ImageButton)view.findViewById(R.id.refresh_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomProgressDialog p = new CustomProgressDialog(getActivity());
                p.show();
                mos.getdemands(session.getUserDetails().get(SessionManager.KEY_EMAIL), new UpdateDemandsUi_Callback(db,getActivity(),p,lv));
            }
        });

        if(datas.size() == 0){
            textv.setVisibility(View.VISIBLE);
            textv.setText("Vous n'avez pas de demande d'ami en attente");


        }else {
            textv.setVisibility(View.GONE);
            Collections.sort(datas);
            DemandsAdapter a = new DemandsAdapter(activity,datas, mail);
            lv.setAdapter(a);
        }


        if (!Connectivity.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Pas de connexion.",
                    Toast.LENGTH_LONG).show();
        } else {


        }

        return view;
    }
}
