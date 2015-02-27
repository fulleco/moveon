package com.application.moveon.rest.modele;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Hugo on 26/01/2015.
 */
@Parcel
public class DemandsPojo implements Comparable<DemandsPojo>{

    @SerializedName("id_demand")
    private int id_demand;

    @SerializedName("sender")
    private String sender;

    @SerializedName("id")
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public boolean equals(Object o){
        return o.getClass().equals(DemandsPojo.class) && (((DemandsPojo)o).getId_demand() == this.getId());
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getId_demand() {
        return id_demand;
    }

    public void setId_demand(int id_demand) {
        this.id_demand = id_demand;
    }

    public String getImagesender() {
        return imagesender;
    }

    public void setImagesender(String imagesender) {
        this.imagesender = imagesender;
    }
}
