package com.application.moveon.friends;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.moveon.R;
import com.application.moveon.custom.CustomProgressDialog;
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
 * Created by damota on 12/01/2015.
 */
public class FragmentFriends extends Fragment {
    private TextView name;

    private Button buttonEditer;

    SessionManager session;

    private ListView mainList;
    private UserAdapter mainAdapter;
    private ArrayList<UserPojo> contactsData;

    private FragmentActivity activity;
    private ToolBox tools;

    MoveOnService mainmos;
    MoveOnService childmos;

    private EditText add_friend;
    private Button addButton;
    private ProgressDialog progressDialog;

    private ListView lv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);

        RestClient r = new RestClient(false);
        mainmos = (new RestClient(true)).getApiService();
        childmos = (new RestClient(false)).getApiService();

        add_friend = (EditText)view.findViewById(R.id.edit_add_friend);
        addButton = (Button)view.findViewById(R.id.buttonAdd);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        final MoveOnDB bd = new MoveOnDB(getActivity().getBaseContext(),session.getUserDetails().get(SessionManager.KEY_EMAIL));
        bd.open();

        lv = (ListView)view.findViewById(R.id.list_contacts);
        lv.setAdapter(new UserAdapter(bd.getFriends(),activity.getBaseContext()));

        bd.close();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ADD

                CustomProgressDialog p = new CustomProgressDialog(getActivity().getBaseContext());
                p.show();

                String mail = session.getUserDetails().get(SessionManager.KEY_EMAIL);



                String val = add_friend.getText().toString();
                if(val.equals("")){
                    Toast.makeText(activity, "Veuillez entrer un identifiant", Toast.LENGTH_SHORT).show();
                    p.dismiss();
                }else if(val.equals(mail)){
                    Toast.makeText(activity, "Vous êtes déjà votre ami", Toast.LENGTH_SHORT).show();
                    p.dismiss();
                }
                else{
                    mainmos.addfriend( mail, val.toLowerCase(), new AddFriend_Callback(activity, p, val,bd));
                }
            }
        });

        contactsData = new ArrayList<UserPojo>();
        mainList = (ListView) view.findViewById(R.id.list_contacts);
        add_friend = (EditText) view.findViewById(R.id.edit_add_friend);

        HashMap<String, String> infos = session.getUserDetails();
        String idUser = infos.get(SessionManager.KEY_ID);

        if (!Connectivity.isConnected(getActivity())) {
            Toast.makeText(getActivity(), "Pas de connexion.",
                    Toast.LENGTH_LONG).show();
        } else {


        }

        return view;
    }


}
