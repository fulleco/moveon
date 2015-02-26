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
import android.widget.ListView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.CreateCircle_Callback;
import com.application.moveon.rest.callback.GetFriendsPicker_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;
import java.util.Calendar;

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

    private EditText nomCercle;
    private EditText editTimeDebut;
    private EditText editDateDebut;
    private EditText editTimeFin;
    private EditText editDateFin;
    private Calendar c;

    private Button buttonLocalisation;
    private Button buttonAjouterParticipants;
    private Button buttonValider;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_cercle, container, false);
        RestClient r = new RestClient(true);
        final MoveOnService mos = r.getApiService();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

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


        buttonAjouterParticipants = (Button) view.findViewById(R.id.buttonParticipants);

        mos.getfriends(session.getUserDetails().get(SessionManager.KEY_EMAIL), new GetFriendsPicker_Callback(getActivity(),buttonAjouterParticipants, this, getFragmentManager() ));
        buttonValider = (Button) view.findViewById(R.id.buttonValider);
        buttonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String susers = new String ();
               if(users != null){

                   for(String s : users){
                       susers += s + " ";
                   }
                   susers = susers.substring(0, susers.length() -1);
               }
                Log.i("HUGO", editTimeDebut.getText().toString());
                Log.i("HUGO", String.valueOf(latitude));
                Log.i("HUGO", String.valueOf(longitude));
                progressDialog.setMessage("Création en cours");
                progressDialog.show();
                mos.createcircle(nomCercle.getText().toString(), session.getUserDetails().get(SessionManager.KEY_EMAIL)
                        ,susers
                        ,editDateDebut.toString() +" " + editTimeDebut.toString()
                        , editDateFin.toString() + " " + editTimeFin
                        ,latitude
                        ,longitude
                        ,0
                        , new CreateCircle_Callback(getActivity(),progressDialog));
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
}
