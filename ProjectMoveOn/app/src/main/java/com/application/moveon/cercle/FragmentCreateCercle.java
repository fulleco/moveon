package com.application.moveon.cercle;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.CreateCircle_Callback;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Quentin Bitschene on 17/12/2014.
 */
public class FragmentCreateCercle extends Fragment{

    SessionManager session;
    private FragmentActivity activity;
    private ToolBox tools;
    private ArrayList<String> users;

    private double longitude;
    private double latitude;
    private ArrayList<UserPojo> selected;

    private EditText nomCercle;
    private EditText editTimeDebut;
    private EditText editDateDebut;
    private EditText editTimeFin;
    private EditText editDateFin;
    private Calendar c;
    private FragmentPickFriends fpc;

    private ImageButton buttonLocalisation;
    private ImageButton buttonAjouterParticipants;
    private ImageButton buttonValider;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);
        if(selected == null){
            selected = new ArrayList<UserPojo>();
        }

        fpc = ((HomeActivity)activity).getFragmentPickFriends();
        fpc.setSelected(selected);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_cercle, container, false);
        RestClient r = new RestClient(true);
        final MoveOnService mos = r.getApiService();



        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);


        c = Calendar.getInstance();

        buttonLocalisation = (ImageButton)view.findViewById(R.id.buttonLocalisation);
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


        buttonAjouterParticipants = (ImageButton) view.findViewById(R.id.buttonParticipants);
        buttonAjouterParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fpc.setSelected(selected);
                ((HomeActivity)activity).switchFragment(fpc);
            }
        });
        buttonValider = (ImageButton) view.findViewById(R.id.buttonValider);
        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String susers = new String ();

                String date1 = editDateDebut.getText().toString() + " " + editTimeDebut.getText().toString();
                String date2 = editDateFin.getText().toString() + " " + editTimeFin.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date datedebut = new Date();
                Date datefin = new Date();

                try {
                    datedebut = sdf.parse(date1);
                    datefin = sdf.parse(date2);
                    if (datefin.after(datedebut)) {

                    } else {
                        Toast.makeText(getActivity(),"La date de fin doit être après la date de debut", Toast.LENGTH_SHORT).show();
                       return;
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(latitude == 0 && longitude == 0){
                    Toast.makeText(getActivity(),"Veuillez indiquer une localisation", Toast.LENGTH_SHORT).show();
                    return;
                }
               if(selected.size() > 0){

                   for(UserPojo s : selected){
                       susers += s.getLogin() + " ";
                   }
                   susers = susers.trim();
               }else{
                   Toast.makeText(getActivity(),"Veuillez inviter au moins un ami", Toast.LENGTH_SHORT).show();
                   return;
               }

                if(nomCercle.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Veuillez mettre un titre", Toast.LENGTH_SHORT).show();
                    return;
                }
                CustomProgressDialog p = new CustomProgressDialog(getActivity());
                p.show();
                mos.createcircle(nomCercle.getText().toString(), session.getUserDetails().get(SessionManager.KEY_EMAIL)
                        ,susers
                        ,date1
                        ,date2
                        ,latitude
                        ,longitude
                        ,0
                        , new CreateCircle_Callback(getActivity(),p));
            }
        });



        return view;
    }


    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<UserPojo> getSelected() {
        return selected;
    }

    public void setSelected(ArrayList<UserPojo> selected) {
        this.selected = selected;
    }
}
