package com.application.moveon.rest.modele;

import com.application.moveon.model.Cercle;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Hugo on 18/02/2015.
 */
@Parcel
public class CerclePojo {

    @SerializedName("id_creator")
    private String id_creator;

    @SerializedName("list_users")
    private UserPojo[] guests;

    @SerializedName("titre")
    private String titre;

    @SerializedName("date_debut")
    private Date date_debut;

    @SerializedName("date_fin")
    private Date date_fin;

    @SerializedName("latitude")
    private float latitude;

    @SerializedName("longitude")
    private float longitude;

    @SerializedName("rayon")
    private int rayon;

    public CerclePojo(String id_creator, UserPojo[] guests, String titre, Date date_debut, Date date_fin, float latitude, float longitude, int rayon) {
        this.id_creator = id_creator;
        this.guests = guests;
        this.titre = titre;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rayon = rayon;
    }

    public CerclePojo(){

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

    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public UserPojo[] getGuests() {
        return guests;
    }

    public void setGuests(UserPojo[] guests) {
        this.guests = guests;
    }
}
