package com.application.moveon.profil;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.moveon.R;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;



/**
 * Created by Quentin Bitschene on 16/12/2014.
 */
public class FragmentViewProfil extends Fragment {

    private TextView name;

    private Button buttonEditer;

    SessionManager session;

    private FragmentActivity activity;

    private String picturePath;
    private String namePicture;
    private ImageView profilePicture;
    private ToolBox tools;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profil, container, false);

        profilePicture = (ImageView)view.findViewById(R.id.imageProfil);
        buttonEditer = (Button) view.findViewById(R.id.buttonModifier);

        View.OnClickListener btEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        };

        activity = (FragmentActivity)getActivity();

        buttonEditer.setOnClickListener(btEditClickListener);
        session = new SessionManager(activity);

        // Remplir les champs par leur valeur actuelle
        name = (TextView) view.findViewById(R.id.textViewName);
        name.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " " + session.getUserDetails().get(SessionManager.KEY_LASTNAME));

        tools = new ToolBox(activity);

        return view;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getNamePicture() {
        return namePicture;
    }

    public void setNamePicture(String namePicture) {
        this.namePicture = namePicture;
    }

    public ImageView getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageView profilePicture) {
        this.profilePicture = profilePicture;
    }

}
