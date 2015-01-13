package com.application.moveon.model;

import android.graphics.Bitmap;

/**
 * Created by damota on 25/11/2014.
 */
public class User {
    private String id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private Bitmap profilePicture;
    public User(String login, String password, String firstName, String lastName){
        this.login = login;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public User(String id, String login, String password, String firstName, String lastName){
        this.id = id;
        this.login = login;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public Bitmap getProfilePicture(){
        return this.profilePicture;
    }

    public void setProfilePicture(Bitmap profilePicture){
        this.profilePicture = profilePicture;
    }
}

