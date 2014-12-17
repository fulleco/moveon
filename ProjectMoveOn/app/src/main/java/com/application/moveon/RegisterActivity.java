package com.application.moveon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.moveon.database.AddUserTask;
import com.application.moveon.ftp.FtpUploadTask;
import com.application.moveon.model.User;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by damota on 25/11/2014.
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private Button buttonRegister;
    private Button buttonConnect;
    private EditText editEmail;
    private EditText editPassword1;
    private EditText editPassword2;
    private EditText editLastName;
    private EditText editFirstName;
    private ToolBox tools;

    private ImageView logo;

    private int RESULT_LOAD_IMAGE = 0;
    private String picturePath;
    private String namePicture;
    private ImageView profilePicture;
    private Button buttonBrowse;

    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SessionManager session = new SessionManager(this);
        HashMap<String, String> infos = session.getUserDetails();
        idUser = infos.get(SessionManager.KEY_ID);
        //session.checkLogin(true);

        logo = (ImageView) findViewById(R.id.logo);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        profilePicture = (ImageView)findViewById(R.id.imageProfil);
        buttonBrowse = (Button)findViewById(R.id.buttonParcourir);
        buttonBrowse.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword1 = (EditText) findViewById(R.id.editPassword1);
        editPassword2 = (EditText) findViewById(R.id.editPassword2);
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editLastName = (EditText) findViewById(R.id.editLastName);

        tools = new ToolBox(this);
    }

    @Override
    public void onClick(View v) {

        //progressDialog = ProgressDialog.show(this, "", "Connexion en cours...", true);

        if (v == buttonConnect) {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            return;
        }

        if( v== buttonBrowse){
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        if (v == buttonRegister) {
            if (!checkPassword()) {
                tools.alertUser("Vérification du mot de passe",
                        "Les deux champs mot de passe ne correspondent pas");
                return;
            }

            ArrayList<String> emptyFields = validFields();
            String message = "";
            if (emptyFields.size()==0) {
                logo.setBackgroundResource(R.drawable.inscription_loader);

                final AnimationDrawable mailAnimation = (AnimationDrawable) logo.getBackground();
                logo.post(new Runnable() {
                    public void run() {
                        if ( mailAnimation != null ) mailAnimation.start();
                    }
                });

                //Calculate the total duration
                int duration = 0;
                for(int i = 0; i < mailAnimation.getNumberOfFrames(); i++){
                    duration += mailAnimation.getDuration(i);
                }

                User newUser = new User(editEmail.getText().toString(),
                        editPassword1.getText().toString(), editFirstName.getText().toString(), editLastName.getText().toString());

                new AddUserTask(this, newUser, mailAnimation, picturePath).execute();
            }else{
                for (String field : emptyFields)
                    message += "-" + field + "\n";
                tools.alertUser("Champs manquants", message);
                return;
            }
        }
    }

    public ArrayList<String> validFields() {
        ArrayList<String> fieldsEmpty = new ArrayList<String>();
        if (editEmail.getText().toString().equals(""))
            fieldsEmpty.add("Email");
        if (editPassword1.getText().toString().equals(""))
            fieldsEmpty.add("Mot de passe");
        if (editLastName.getText().toString().equals(""))
            fieldsEmpty.add("Nom de famille");
        if (editFirstName.getText().toString().equals(""))
            fieldsEmpty.add("Prénom");
        return fieldsEmpty;
    }

    public boolean checkPassword() {
        if (!editPassword1.getText().toString()
                .equals(editPassword2.getText().toString()))
            return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            String extension = picturePath.substring(picturePath.lastIndexOf("."));
            namePicture = tools.getFileName(selectedImage)+extension;
            cursor.close();

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.outHeight = 8;
            //mainPicture.setImageBitmap(BitmapFactory.decodeFile(picturePath, options));

            Bitmap b_gallery = tools.decodeSampledBitmapFromResource(picturePath, 60, 60);
            Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b_gallery, 15, 0);

            profilePicture.setBackground(null);
            profilePicture.setImageBitmap(b_rounded);
        }

    }
}
