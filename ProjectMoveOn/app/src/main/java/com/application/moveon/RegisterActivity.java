package com.application.moveon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.application.moveon.custom.CustomProgressDialog;
import com.application.moveon.ftp.FtpUploadTask;
import com.application.moveon.model.User;
import com.application.moveon.rest.MoveOnService;
import com.application.moveon.rest.RestClient;
import com.application.moveon.rest.callback.Register_Callback;
import com.application.moveon.session.SessionManager;
import com.application.moveon.tools.ImageHelper;
import com.application.moveon.tools.ToolBox;

import junit.runner.Version;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    private MoveOnService mos;
    private Register_Callback rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/BebasNeue.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        RestClient r = new RestClient(false);
        mos = r.getApiService();

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



                User newUser = new User(editEmail.getText().toString().toLowerCase(),
                        editPassword1.getText().toString(), editFirstName.getText().toString().toLowerCase(), editLastName.getText().toString().toLowerCase());

                CustomProgressDialog p = new CustomProgressDialog(RegisterActivity.this);
                p.show();

                rc = new Register_Callback(RegisterActivity.this, newUser, p, picturePath, tools);
                mos.userexists(newUser.getLogin(), rc);

            }else{
                for (String field : emptyFields)
                    message += "-" + field + "\n";
                tools.alertUser("Champs manquants", message);
                return;
            }
        }
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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
        if (!validEmail(editEmail.getText().toString())) {
           fieldsEmpty.add("Email invalide");

        }
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

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();

            /*String[] filePathColumn = { MediaStore.Images.Media.DATA };

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

            Bitmap b_gallery = tools.decodeSampledBitmapFromResource(picturePath, 60, 60);*/
            Bitmap b_gallery = null;
            try {
                b_gallery = ImageHelper.getCorrectlyOrientedImage(this, selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap b_rounded = ImageHelper.getRoundedCornerBitmap(b_gallery, 15, 0);

            //create a file to write bitmap data
            File f = new File(this.getCacheDir(), "profile.png");
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
