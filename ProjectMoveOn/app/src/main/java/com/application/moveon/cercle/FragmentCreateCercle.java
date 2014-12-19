package com.application.moveon.cercle;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.TimePickerFragment;
import com.application.moveon.tools.ToolBox;

/**
 * Created by Quentin Bitschene on 17/12/2014.
 */
public class FragmentCreateCercle extends Fragment{

    SessionManager session;
    private FragmentActivity activity;
    private ToolBox tools;

    private EditText nomCercle;

    private Button buttonLocalisation;
    private Button buttonDateDebut;
    private Button buttonDateFin;
    private Button buttonValider;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_cercle, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);

        tools = new ToolBox(activity);

        buttonLocalisation = (Button)view.findViewById(R.id.buttonLocalisation);
        buttonLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)activity).switchFragment(((HomeActivity)activity).getFragmentLocationChooser());
            }
        });

        buttonDateDebut = (Button) view.findViewById(R.id.buttonDateDebut);
        buttonDateFin = (Button) view.findViewById(R.id.buttonDateFin);

        View.OnClickListener dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        };

        buttonDateDebut.setOnClickListener(dateClickListener);
        buttonDateFin.setOnClickListener(dateClickListener);

        nomCercle = (EditText) view.findViewById(R.id.editNameCercle);
        buttonValider = (Button) view.findViewById(R.id.buttonValider);
        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //step 1 : check les champs
                //step 2 : envoyer en BDD - ou pop-up erreur
            }
        });



        return view;
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
