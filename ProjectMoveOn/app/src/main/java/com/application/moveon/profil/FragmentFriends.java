package com.application.moveon.profil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

/**
 * Created by damota on 12/01/2015.
 */
public class FragmentFriends extends Fragment {
    private TextView name;

    private Button buttonEditer;

    SessionManager session;

    private FragmentActivity activity;
    private ToolBox tools;

    private EditText editLogin;
    private Button addButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);

        editLogin = (EditText)view.findViewById(R.id.edit_add_login);
        addButton = (Button)view.findViewById(R.id.buttonAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO ADD
                editLogin.setText("");
            }
        });

        return view;
    }
}
