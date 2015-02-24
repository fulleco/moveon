package com.application.moveon.rest.modele;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hugo on 26/01/2015.
 */
@Parcel
public class DemandsPojo implements Comparable<DemandsPojo>{

    @SerializedName("sender")
    private String sender;

    @SerializedName("id")
    private String id;

    @SerializedName("mail")
    private String mail;

    @SerializedName("status")
    private int status;

    @SerializedName("imageprofile")
    private String imagesender;


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(DemandsPojo demandsPojo) {
        return (this.getSender().compareTo(demandsPojo.getSender()));
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
