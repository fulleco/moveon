package com.application.moveon.profil;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.moveon.R;
import com.application.moveon.model.User;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.EditUser_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;


/**
 * Created by Quentin Bitschene on 16/12/2014.
 */
public class FragmentEditProfil extends Fragment {

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editEmail;
    private ToolBox tools;
    private ImageView logo;
    private Button buttonModifier;

    SessionManager session;
    private FragmentActivity activity;
    private MoveOnService mos;

    private int RESULT_LOAD_IMAGE = 0;
    private String picturePath;
    private String namePicture;
    private ImageView profilePicture;
    private Button buttonBrowse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil, container, false);

        activity = (FragmentActivity)getActivity();
        session = new SessionManager(activity);
        RestClient r = new RestClient(true);
        mos = r.getApiService();

        tools = new ToolBox(activity);
        logo = (ImageView) view.findViewById(R.id.logo);

        profilePicture = (ImageView)view.findViewById(R.id.imageProfil);

        buttonBrowse = (Button)view.findViewById(R.id.buttonParcourir);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        buttonModifier = (Button) view.findViewById(R.id.buttonModifier);
        buttonModifier.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  ArrayList<String> emptyFields = validFields();
                  String message = "";
                  if (emptyFields.size() == 0) {

                      // mettre a jour la BDD avec la valeur dans les champs
                      User newUser = new User(session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_EMAIL),
                              session.getUserDetails().get(SessionManager.KEY_PASSWORD), editFirstName.getText().toString(), editLastName.getText().toString());
                      mos.updateuser(newUser.getFirstName(),newUser.getLastName(),newUser.getPassword(), newUser.getLogin(), newUser.getId(), new EditUser_Callback(newUser,tools));

                  } else {
                      for (String field : emptyFields)
                          message += "-" + field + "\n";
                      tools.alertUser("Champs manquants", message);
                      return;
                  }

              }
          });

        // Remplir les champs par leur valeur actuelle
        editFirstName = (EditText) view.findViewById(R.id.editFirstName);
        editFirstName.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME));

        editLastName = (EditText) view.findViewById(R.id.editLastName);
        editLastName.setText(session.getUserDetails().get(SessionManager.KEY_LASTNAME));

        editEmail = (EditText) view.findViewById(R.id.editEmail);
        editEmail.setText(session.getUserDetails().get(SessionManager.KEY_EMAIL));

        return view;
    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();

        if (editLastName.getText().toString().equals(""))
            fieldsEmpty.add("Nom de famille");
        if (editFirstName.getText().toString().equals(""))
            fieldsEmpty.add("Prénom");
        return fieldsEmpty;
    }

}
