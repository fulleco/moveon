package com.application.moveon.model;

/**
 * Created by damota on 25/11/2014.
 */
public class User {
    private String id;
    private String login;
    private String password;
    private String mail;

    public User(String login, String password, String mail){
        this.login = login;
        this.password = password;
        this.mail = mail;
    }

    public User(String id, String login, String password, String mail){
        this.id = id;
        this.login = login;
        this.password = password;
        this.mail = mail;
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

    public String getMail(){
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}

