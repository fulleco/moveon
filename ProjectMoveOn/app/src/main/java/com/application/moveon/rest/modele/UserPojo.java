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

    public int compareTo(UserPojo o) {
        return this.getFirstname().compareTo(o.getFirstname());
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


}
