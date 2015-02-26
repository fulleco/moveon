package com.application.moveon.rest.modele;

import android.os.Parcelable;

import com.application.moveon.model.User;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Comparator;

/**
 * Created by Hugo on 02/12/2014.
 */
@Parcel
public class UserPojo implements Comparable<UserPojo>{

    @SerializedName("id_client")
    private int id_client;

    @SerializedName("firstName")
    private String firstname;

    @SerializedName("lastName")
    private String lastname;

    @SerializedName("login")
    private String login;

    @SerializedName("password")
    private String password;

    @SerializedName("imageprofile")
    private String imageprofile;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("id_cercle")
    private Integer id_cercle;

    public int compareTo(UserPojo o) {

        return this.getFirstname().compareTo(o.getFirstname());
    }

    @Override
    public boolean equals(Object o){
        if(id_cercle == null){
            return o.getClass().equals(UserPojo.class) && ((UserPojo) o).getLogin().equals(this.getLogin());
        }else{
           if(o.getClass().equals(UserPojo.class)){
               UserPojo up = (UserPojo)o;
               return up.getLogin().equals(this.getLogin()) && up.getId_cercle().equals(this.getId_cercle());
           }else return false;

        }

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageprofile() {
        return imageprofile;
    }

    public void setImageprofile(String imageprofile) {
        this.imageprofile = imageprofile;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getId_cercle() {
        return id_cercle;
    }

    public void setId_cercle(Integer id_cercle) {
        this.id_cercle = id_cercle;
    }
}
