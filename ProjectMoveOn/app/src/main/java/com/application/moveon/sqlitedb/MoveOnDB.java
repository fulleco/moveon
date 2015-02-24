package com.application.moveon.sqlitedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.application.moveon.model.MessagePojo;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;
import com.application.moveon.session.SessionManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Hugo on 20/02/2015.
 */
public class MoveOnDB {

    private static final int VERSION_BDD = 4;
    private static final String NOM_BDD = "moveon_";
    private static final String TAG = "MOVEON DATABASE";

    private static final String TABLE_FRIEND = "Friend";
    private static final String COL_ID = "id_friend";
    private static final int COL_ID_NUMBER = 0;
    private static final String COL_FIRSTNAME = "firstname";
    private static final int COL_FIRSTNAME_NUMBER = 1;
    private static final String COL_LASTNAME = "lastname";
    private static final int COL_LASTNAME_NUMBER = 2;
    private static final String COL_EMAIL = "email";
    private static final int COL_EMAIL_NUMBER = 3;
    private static final String COL_IMAGE = "imageprofile";
    private static final int COL_IMAGE_NUMBER = 4;

    public static final String TABLE_CERCLES = "Cercle";
    private static final String COL_ID_CERCLE = "id_cercle";
    private static final int COL_ID_CERLCE_NUMBER = 0;
    private static final String COL_ID_CREATOR = "id_creator";
    private static final int COL_ID_CREATOR_NUMBER = 1;
    private static final String COL_DATEDEBUT = "date_debut";
    private static final int COL_DATEDEBUT_NUMBER = 2;
    private static final String COL_DATEFIN = "date_fin";
    private static final int COL_DATEFIN_NUMBER = 3;
    private static final String COL_LAT = "latitude";
    private static final int COL_LAT_NUMBER = 4;
    private static final String COL_LONG="longitude";
    private static final int COL_LONG_NUMBER = 5;
    private static final String COL_RAY = "rayon";
    private static final int COL_RAY_NUMBER = 6;

    public static final String TABLE_MESSAGES = "Message";
    private static final String COL_ID_MESSAGE = "id_message";
    private static final int COL_ID_MESSAGE_NUMBER = 0;
    private static final String COL_ID_CERCLEM = "id_cercle";
    private static final int COL_ID_CERCLEM_NUMBER = 1;
    private static final String COL_ID_SENDER="id_sender";
    private static final int COL_ID_SENDER_NUMBER = 2;
    private static final String COL_NAMESENDER = "name";
    private static final int COL_NAMESENDER_NUMBER = 3;
    private static final String COL_CONTENT="id_content";
    private static final int COL_CONTENT_NUMBER = 4;
    private static final String COL_DATESEND="date";
    private static final int COL_DATESEND_NUMBER = 5;
    private static final String COL_SEEN = "seen";
    private static final int COL_SEEN_NUMBER = 6;

    private SQLiteDatabase bdd;
    private SQLiteDB maBaseSQLite;
    private static MoveOnDB mInstance;

    private MoveOnDB(){

    }

    public void initialize(Context context, String login){

        maBaseSQLite = new SQLiteDB(context, NOM_BDD + login + ".db" , null, VERSION_BDD);
    }

    public static MoveOnDB getInstance(){
        if(mInstance == null)
        {
            mInstance = new MoveOnDB();
        }
        return mInstance;
    }


    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public UserPojo cursorToUser(Cursor c){

        UserPojo up = new UserPojo();

        up.setFirstname(c.getString(COL_FIRSTNAME_NUMBER));
        up.setLastname(c.getString(COL_LASTNAME_NUMBER));
        up.setId_client(c.getInt(COL_ID_NUMBER));
        up.setImageprofile(c.getString(COL_IMAGE_NUMBER));
        up.setLogin(c.getString(COL_EMAIL_NUMBER));
        up.setPassword("nopassword");

        return up;
    }

    public  CerclePojo cursorToCercle(Cursor c){

        CerclePojo cp = new CerclePojo();

        cp.setDate_debut(c.getString(COL_DATEDEBUT_NUMBER));
        cp.setDate_fin(c.getString(COL_DATEFIN_NUMBER));
        cp.setId_cercle(c.getInt(COL_ID_CERLCE_NUMBER));
        cp.setId_creator(c.getString(COL_ID_CREATOR_NUMBER));
        cp.setRayon(c.getInt(COL_RAY_NUMBER));
        cp.setLatitude(c.getFloat(COL_LAT_NUMBER));
        cp.setLongitude(c.getFloat(COL_LONG_NUMBER));

        return cp;
    }

    public  MessagePojo cursorToMessage(Cursor c){

        MessagePojo mp = new MessagePojo();

        mp.setContent(c.getString(COL_CONTENT_NUMBER));
        mp.setDate(c.getString(COL_DATESEND_NUMBER));
        mp.setFirstname_sender(c.getString(COL_NAMESENDER_NUMBER));
        mp.setLastname_sender(c.getString(COL_NAMESENDER_NUMBER));
        mp.setId_circle(String.valueOf(c.getInt(COL_ID_CERCLEM_NUMBER)));
        mp.setId_sender(String.valueOf(c.getInt(COL_ID_SENDER_NUMBER)));
        mp.setId(String.valueOf(c.getInt(COL_ID_MESSAGE_NUMBER)));
        mp.setSeen(c.getInt(COL_SEEN_NUMBER));

        return mp;
    }

    public  ArrayList<UserPojo> getFriends(){

        ArrayList<UserPojo> ret = new ArrayList<UserPojo>();
        Cursor cursor = bdd.rawQuery("SELECT * FROM " +TABLE_FRIEND, null);
        Log.i(TAG, "Loaded " + cursor.getCount() + " friends.");

        if(verifyCursor(cursor)) {
            while (!cursor.isAfterLast()) {
                UserPojo up = cursorToUser(cursor);
                ret.add(up);
                cursor.moveToNext();
            }
            Log.i(TAG, "Friends loaded successfully.");
        }

        return ret;
    }
    private boolean verifyCursor(Cursor c){
        if(c.getCount() == 0){
            return false;
        }else{
            c.moveToFirst();
            return  true;
        }
    }

    public long insertUser(UserPojo up){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();

        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_ID, up.getId_client());
        values.put(COL_FIRSTNAME, up.getFirstname());
        values.put(COL_LASTNAME, up.getLastname());
        values.put(COL_EMAIL, up.getLogin());
        values.put(COL_IMAGE, up.getImageprofile());

        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_FRIEND, null, values);
    }

    public void updateFriends(ArrayList<UserPojo> upfromdb){
        ArrayList<UserPojo> cache = this.getFriends();
        for(UserPojo up : upfromdb){
            if(!cache.contains(up)){
                cache.add(up);
                this.insertUser(up);
            }
        }
        for(UserPojo up : cache){
            if(!upfromdb.contains(up)){
                this.deleteFriend(up.getLogin());
            }
        }
    }

    public boolean deleteFriend(String login)
    {
        return bdd.delete(TABLE_FRIEND, COL_EMAIL + "=" + login, null) > 0;
    }


}
