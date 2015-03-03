package com.application.moveon.profil;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.application.moveon.tools.ToolBox;
import com.squareup.picasso.Picasso;


/**
 * Created by Quentin Bitschene on 16/12/2014.
 */
public class FragmentViewProfil extends Fragment {

    private TextView name;

    private ImageButton buttonEditer;

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

        MoveOnDB db = new MoveOnDB(activity.getBaseContext(), session.getUserDetails().get(SessionManager.KEY_EMAIL));
        db.open();

        profilePicture = (ImageView)view.findViewById(R.id.imageProfil);
        buttonEditer = (ImageButton) view.findViewById(R.id.buttonModifier);

        HomeActivity h = (HomeActivity)getActivity();

        // Remplir les champs par leur valeur actuelle
        name = (TextView) view.findViewById(R.id.textViewName);
        name.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME) + " " + session.getUserDetails().get(SessionManager.KEY_LASTNAME));

        TextView mail = (TextView) view.findViewById(R.id.textViewMail);
        mail.setText(session.getUserDetails().get(SessionManager.KEY_EMAIL));

        TextView nombrecercle = (TextView) view.findViewById(R.id.text_nombre_cercle);
        nombrecercle.setText("Cercles : " + db.getCircles().size());
        TextView nombreamis = (TextView) view.findViewById(R.id.text_nombre_amis);
        nombreamis.setText("Amis : " + db.getFriends().size());


        //TODO
        //profilPicture = get picture here

        View.OnClickListener btEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)activity).switchFragment(((HomeActivity)activity).getFragmentEditProfil());
            }
        };

        buttonEditer.setOnClickListener(btEditClickListener);

        Picasso.with(getActivity()).load("http://martinezhugo.com/pfe/images/"+
                session.getUserDetails().get(SessionManager.KEY_ID)+"/profile.jpg").into(profilePicture);

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
