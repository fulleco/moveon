package com.application.moveon.rest.modele;

import android.util.Log;

import com.application.moveon.model.User;
import com.application.moveon.session.SessionManager;
import com.application.moveon.sqlitedb.MoveOnDB;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Hugo on 18/02/2015.
 */
@Parcel
public class CerclePojo {

    @SerializedName("id_cercle")
    private int id_cercle;

    @SerializedName("id_creator")
    private String id_creator;

    @SerializedName("titre")
    private String titre;

    @SerializedName("date_debut")
    private String date_debut;

    @SerializedName("date_fin")
    private String date_fin;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("rayon")
    private int rayon;

    @SerializedName("creator")
    private UserPojo creator;

    @SerializedName("participants")
    private UserPojo[] participants;

    public CerclePojo(int id_cercle, String id_creator, String titre, String date_debut, String date_fin, double latitude, double longitude, int rayon, UserPojo creator, UserPojo[] participants) {
        this.id_cercle = id_cercle;
        this.id_creator = id_creator;
        this.titre = titre;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rayon = rayon;
        this.creator = creator;
        this.participants = participants;
    }

    public CerclePojo(){

    }

    @Override
    public boolean equals(Object o){
        return o.getClass().equals(CerclePojo.class) && (((CerclePojo)o).getId_cercle() == this.getId_cercle());
    }

    public String getId_creator() {
        return id_creator;
    }

    public void setId_creator(String id_creator) {
        this.id_creator = id_creator;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(String date_debut) {
        this.date_debut = date_debut;
    }

    public String getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(String date_fin) {
        this.date_fin = date_fin;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public int getId_cercle() {
        return id_cercle;
    }

    public void setId_cercle(int id_cercle) {
        this.id_cercle = id_cercle;
    }

    public UserPojo getCreator() {
        return creator;
    }

    public void setCreator(UserPojo creator) {
        this.creator = creator;
    }

    public UserPojo[] getParticipants() {
        return participants;
    }

    public void setParticipants(UserPojo[] participants) {
        this.participants = participants;
    }

    public void addParticipant(UserPojo creator) {

        final int N = participants.length;
        participants = Arrays.copyOf(participants, N + 1);
        participants[N] = creator;

    }

    public void setAllInfo(SessionManager session)
    {
        MoveOnDB moveOnDB = MoveOnDB.getInstance();

        if(session.getUserDetails().get(SessionManager.KEY_EMAIL).equals(this.getId_creator()))
            this.setCreator(session.getUserPojo());
        else
            this.setCreator(moveOnDB.getCreator(this.getId_creator()));

        ArrayList<UserPojo> participants =  moveOnDB.getParticipants(this.getId_cercle());
        UserPojo[] participantsToArray =  participants.toArray(new UserPojo[participants.size()]);

        this.setParticipants(participantsToArray);
        this.addParticipant(this.getCreator());

       for(int i = 0 ; i<this.getParticipants().length;i++)
        {
            Log.d("QUENTIN LATITUDE : ",this.getParticipants()[i].getLatitude());
            Log.d("QUENTIN LONGITUDE : ",this.getParticipants()[i].getLongitude());
        }
    }
}
