package com.application.moveon.sqlitedb;

import android.database.Cursor;

import com.application.moveon.model.MessagePojo;
import com.application.moveon.rest.modele.CerclePojo;
import com.application.moveon.rest.modele.UserPojo;

import java.util.Date;

/**
 * Created by Hugo on 20/02/2015.
 */
public class MoveOnDB {

    private static final int VERSION_BDD = 4;
    private static final String NOM_BDD = "moveon.db";

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

    private UserPojo cursorToUser(Cursor c){

        UserPojo up = new UserPojo();

        up.setFirstname(c.getString(COL_FIRSTNAME_NUMBER));
        up.setLastname(c.getString(COL_LASTNAME_NUMBER));
        up.setId_client(c.getInt(COL_ID_NUMBER));
        up.setImageprofile(c.getString(COL_IMAGE_NUMBER));
        up.setLogin(c.getString(COL_EMAIL_NUMBER));
        up.setPassword("nopassword");

        return up;
    }

    private CerclePojo cursorToCercle(Cursor c){

        CerclePojo cp = new CerclePojo();

        cp.setDate_debut(new Date(c.getString(COL_DATEDEBUT_NUMBER)));
        cp.setDate_fin(new Date(c.getString(COL_DATEFIN_NUMBER)));
        cp.setGuests(new UserPojo[0]);
        cp.setId_cercle(c.getInt(COL_ID_CERLCE_NUMBER));
        cp.setId_creator(c.getInt(COL_ID_CREATOR_NUMBER));
        cp.setRayon(c.getInt(COL_RAY_NUMBER));
        cp.setLatitude(c.getFloat(COL_LAT_NUMBER));
        cp.setLongitude(c.getFloat(COL_LONG_NUMBER));

        return cp;
    }

    private boolean verifyCursor(Cursor c){
        if(c.getCount() == 0){
            return false;
        }else{
            c.moveToFirst();
            return  true;
        }
    }

}
