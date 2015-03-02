package com.application.moveon.cercle;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.cercle.Adapter.UserPickerAdapter;
import com.application.moveon.friends.adapter.UserAdapter;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.AddFriend_Callback;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.Connectivity;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hugo on 02/03/2015.
 */
public class FragmentPickFriends extends Fragment {

    private TextView name;

    private Button buttonEditer;

    SessionManager session;

    private ListView mainList;
    private UserAdapter mainAdapter;
    private ArrayList<UserPojo> contactsData;
    private ArrayList<UserPojo> selected;

    private FragmentActivity activity;
    private ToolBox tools;

    MoveOnService mainmos;
    MoveOnService childmos;

    private EditText add_friend;
    private Button addButton;
    private ProgressDialog progressDialog;

    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendspicker, container, false);


        final MoveOnDB bd = new MoveOnDB(getActivity().getBaseContext(),session.getUserDetails().get(SessionManager.KEY_EMAIL));
        bd.open();

        lv = (ListView)view.findViewById(R.id.list_contacts_check);
        final UserPickerAdapter adapter = new UserPickerAdapter(bd.getFriends(),selected,activity.getBaseContext());
        lv.setAdapter(adapter);

        bd.close();

        ImageButton valider = (ImageButton)view.findViewById(R.id.valider_picker);
        View.OnClickListener validerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity activity = (HomeActivity)getActivity();
                FragmentCreateCercle fcc = (FragmentCreateCercle)getTargetFragment();
                fcc.setSelected(adapter.getselected());
                HomeActivity home = (HomeActivity)getActivity();
                home.switchFragment(fcc);
            }
        };
        valider.setOnClickListener(validerClickListener);
        if (!Connectivity.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Pas de connexion.",
                    Toast.LENGTH_LONG).show();
        } else {


        }

        return view;
    }

    public void setSelected(ArrayList<UserPojo> selected) {
        this.selected = selected;
    }
}
