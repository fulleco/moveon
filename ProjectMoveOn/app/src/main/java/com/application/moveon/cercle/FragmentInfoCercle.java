package com.application.moveon.cercle;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.rest.modele.CerclePojo;

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

    private HomeActivity homeActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_infos_cercle, container, false);
        textViewTitre = (TextView) view.findViewById(R.id.text_view_titre);
        textViewCreateur = (TextView) view.findViewById(R.id.text_view_createur);
        listViewParticipants = (ListView) view.findViewById(R.id.list_view);
        homeActivity = (HomeActivity) getActivity();

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
        textViewCreateur.setText(currentCercle.getCreator().getFirstname()+ " " + currentCercle.getCreator().getLastname());
        homeActivity.getFragmentMap().initCercle();

    }

    public ListView getListViewParticipants() {
        return listViewParticipants;
    }

    public void setListViewParticipants(ListView listViewParticipants) {
        this.listViewParticipants = listViewParticipants;
    }
}
