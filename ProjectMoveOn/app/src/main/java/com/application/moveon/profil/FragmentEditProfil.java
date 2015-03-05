package com.application.moveon.profil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.moveon.HomeActivity;
import com.application.moveon.R;
import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.model.User;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.EditUser_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.application.moveon.tools.ToolBox;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        String image = "http://martinezhugo.com/pfe/images/"+ session.getUserDetails().get(SessionManager.KEY_ID)+"/profile.jpg";
        Picasso.with(getActivity()).load(image).resize(100, 100).into(profilePicture);

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
                  CustomProgressDialog cpd = new CustomProgressDialog(getActivity());

                  if (emptyFields.size() == 0) {
                      cpd.show();
                      // mettre a jour la BDD avec la valeur dans les champs
                      User newUser = new User(session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_EMAIL),
                              session.getUserDetails().get(SessionManager.KEY_PASSWORD), editFirstName.getText().toString(), editLastName.getText().toString());
                      mos.updateuser(newUser.getFirstName(),newUser.getLastName(),newUser.getPassword(), newUser.getLogin(), newUser.getId(), new EditUser_Callback(picturePath,newUser,tools, (HomeActivity)getActivity(),cpd));
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


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();

            /*String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            String extension = picturePath.substring(picturePath.lastIndexOf("."));
            namePicture = tools.getFileName(selectedImage)+extension;
            cursor.close();

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.outHeight = 8;*/
            //mainPicture.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));

            //Bitmap b_gallery = tools.decodeSampledBitmapFromResource(picturePath, 60, 60);
            Bitmap b_gallery = null;
            try {
                b_gallery = ImageHelper.getCorrectlyOrientedImage(getActivity(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b_gallery, 15, 0);

            //create a file to write bitmap data
            File f = new File(getActivity().getCacheDir(), "profile.png");
            try {
                f.createNewFile();


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                b_rounded.compress(Bitmap.CompressFormat.JPEG, 90 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);

                //write the bytes in file
                picturePath = f.getAbsolutePath();

                profilePicture.setBackground(null);
                profilePicture.setImageBitmap(b_rounded);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
