package com.application.moveon.profil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
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

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        tools = new ToolBox(activity);

        profilePicture = (ImageView)view.findViewById(R.id.imageProfil);
        buttonEditer = (Button) view.findViewById(R.id.buttonModifier);

        View.OnClickListener btEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* activity.getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .remove(((HomeActivity)activity).getLastFragment())
                        .add(R.id.content_frame, ((HomeActivity)activity).getFragmentEditProfil())
                        .commit();
                        */

            }
        };

        buttonEditer.setOnClickListener(btEditClickListener);

        // Remplir les champs par leur valeur actuelle
        name = (TextView) view.findViewById(R.id.textViewName);
        name.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " " + session.getUserDetails().get(SessionManager.KEY_LASTNAME));



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
