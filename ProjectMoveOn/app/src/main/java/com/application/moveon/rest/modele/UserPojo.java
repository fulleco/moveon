package com.application.moveon.rest.modele;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hugo on 02/12/2014.
 */
@Parcel
public class UserPojo {

    @SerializedName("id_client")
    private String id_client;

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

    public String getId_client() {
        return id_client;
    }

    public void setId_client(String id_client) {
        this.id_client = id_client;
    }
}