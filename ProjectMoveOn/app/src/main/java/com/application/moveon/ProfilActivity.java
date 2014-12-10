package com.application.moveon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.moveon.database.UpdateUserTask;
import com.application.moveon.model.User;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.application.moveon.tools.ToolBox;

import java.util.ArrayList;


public class ProfilActivity extends Activity implements View.OnClickListener{

    private EditText editFirstName;
    private EditText editLastName;
    private ToolBox tools;
    private ImageView logo;
    private Button buttonModifier;

    SessionManager session;

    private int RESULT_LOAD_IMAGE = 0;
    private String picturePath;
    private String namePicture;
    private ImageView profilePicture;
    private Button buttonBrowse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        tools = new ToolBox(this);
        logo = (ImageView) findViewById(R.id.logo);

        profilePicture = (ImageView)findViewById(R.id.imageProfil);

        buttonBrowse = (Button)findViewById(R.id.buttonParcourir);
        buttonBrowse.setOnClickListener(this);

        buttonModifier = (Button) findViewById(R.id.buttonModifier);
        buttonModifier.setOnClickListener(this);

        // Remplir les champs par leur valeur actuelle
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        session = new SessionManager(this);
        editFirstName.setText(session.getUserDetails().get(SessionManager.KEY_FIRSTNAME));

        editLastName = (EditText) findViewById(R.id.editLastName);
        editLastName.setText(session.getUserDetails().get(SessionManager.KEY_LASTNAME));


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

    @Override
    public void onClick(View v) {

        if( v== buttonBrowse){
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }

        if (v==buttonModifier) {

            ArrayList<String> emptyFields = validFields();
            String message = "";
            if (emptyFields.size() == 0) {

                // mettre a jour la BDD avec la valeur dans les champs
                User newUser = new User(session.getUserDetails().get(SessionManager.KEY_ID), session.getUserDetails().get(SessionManager.KEY_LOGIN),
                        session.getUserDetails().get(SessionManager.KEY_PASSWORD), editFirstName.getText().toString(), editLastName.getText().toString());
                new UpdateUserTask(ProfilActivity.this, newUser).execute();

            } else {
                for (String field : emptyFields)
                    message += "-" + field + "\n";
                tools.alertUser("Champs manquants", message);
                return;
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
        if (id == R.id.action_disconnect) {
            SessionManager session = new SessionManager(this);
            session.logoutUser();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_settings) {
        }
        return super.onOptionsItemSelected(item);
    }
}
