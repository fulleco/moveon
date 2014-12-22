package com.application.moveon.cercle;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Quentin Bitschene on 17/12/2014.
 */
public class FragmentCreateCercle extends Fragment{

    SessionManager session;
    private FragmentActivity activity;
    private ToolBox tools;

    private EditText nomCercle;
    private EditText editTimeDebut;
    private EditText editDateDebut;
    private EditText editTimeFin;
    private EditText editDateFin;
    private Calendar c;

    private Button buttonLocalisation;
    private Button buttonValider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_cercle, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        c = Calendar.getInstance();
        tools = new ToolBox(activity);

        buttonLocalisation = (Button)view.findViewById(R.id.buttonLocalisation);
        buttonLocalisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)activity).switchFragment(((HomeActivity)activity).getFragmentLocationChooser());
            }
        });


        editTimeDebut = (EditText) view.findViewById(R.id.editTimeDebut);
        editDateDebut =(EditText) view.findViewById(R.id.editDateDebut);
        editTimeFin = (EditText) view.findViewById(R.id.editTimeFin);
        editDateFin = (EditText) view.findViewById(R.id.editDateFin);

        tools.setCurrentDateOnView(editDateDebut,c);
        tools.setCurrentTimeOnView(editTimeDebut,c);

        tools.setCurrentDateOnView(editDateFin,c);
        tools.setCurrentTimeOnView(editTimeFin,c);


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

}
