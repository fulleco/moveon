package com.application.moveon.cercle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;

import org.w3c.dom.Text;

/**
 * Created by suparjam on 19/02/2015.
 */
public class FragmentInfoCercle extends Fragment {

    private static View view;

    private LayoutInflater mInflater;

    private TextView textViewTitre;
    private TextView textViewCreateur;
    private ListView listViewParticipants;
    private Button btQuitterCercle;
    private MoveOnDB moveOnDB;
    private SessionManager session;

    private HomeActivity homeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_infos_cercle, container, false);
        homeActivity = (HomeActivity) getActivity();
        moveOnDB = MoveOnDB.getInstance();

        textViewTitre = (TextView) view.findViewById(R.id.text_view_titre);
        textViewCreateur = (TextView) view.findViewById(R.id.text_view_createur);
        listViewParticipants = (ListView) view.findViewById(R.id.list_view);
        btQuitterCercle = (Button) view.findViewById(R.id.bt_quitter_cercle);
        session = new SessionManager(homeActivity);

        updateContent();

        btQuitterCercle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //STEP 1 : Quitter le cercle en BDD
                /*
                moveOnDB.deleteParticipants(session.getUserDetails().get(SessionManager.KEY_EMAIL), homeActivity.getCurrentCercle().getId_cercle());
                RestClient r = new RestClient(true);
                MoveOnService mos = r.getApiService();
                mos.
                */

                //STEP 2 : Mettre a jour l'UI
            }
        });

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


    public void updateContent() {
        CerclePojo currentCercle =((HomeActivity)getActivity()).getCurrentCercle();
        if(currentCercle==null)
            return;

        textViewTitre.setText(currentCercle.getTitre());
        textViewCreateur.setText(currentCercle.getCreator().getFirstname() + " " + currentCercle.getCreator().getLastname());

        final InfoCercleAdapter a = new InfoCercleAdapter(currentCercle.getParticipants(), getActivity().getBaseContext(), this);
        listViewParticipants.setAdapter(a);

    }

    public ListView getListViewParticipants() {
        return listViewParticipants;
    }

    public void setListViewParticipants(ListView listViewParticipants) {
        this.listViewParticipants = listViewParticipants;
    }
}
