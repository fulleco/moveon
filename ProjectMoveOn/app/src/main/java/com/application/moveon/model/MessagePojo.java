package com.application.moveon.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by damota on 25/11/2014.
 */
public class MessagePojo implements Comparable<MessagePojo>{

    @SerializedName("id")
    private String id;

    @SerializedName("id_circle")
    private String id_circle;

    @SerializedName("id_sender")
    private String id_sender;

    @SerializedName("lastname_sender")
    private String lastname_sender;

    @SerializedName("firstname_sender")
    private String firstname_sender;

    @SerializedName("id_receiver")
    private String id_receiver;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private String date;

    @SerializedName("seen")
    private int seen;

    @SerializedName("id_image")
    private int id_image;

    public MessagePojo(){
        this.id = null;
        this.id_circle = null;
        this.id_sender = null;
        this.id_receiver = null;
        this.content = null;
        this.date = null;
        this.seen = 0;
        this.id_image = -1;
        this.setLastname_sender(null);
        this.setFirstname_sender(null);
    }

    public MessagePojo(String id, String id_circle, String id_sender, String id_receiver, String content, String date, int seen, String lastname_sender, String firstname_sender, int id_image){
        this.id = id;
        this.id_circle = id_circle;
        this.id_sender = id_sender;
        this.id_receiver = id_receiver;
        this.content = content;
        this.date = date;
        this.seen = seen;
        this.id_image = id_image;
        this.setLastname_sender(lastname_sender);
        this.setFirstname_sender(firstname_sender);
    }

    @Override
    public boolean equals(Object o){
        return o.getClass().equals(MessagePojo.class) && (((MessagePojo)o).getId() == this.getId());
    }

    public int compareTo(MessagePojo m) {
        return this.id_sender.compareTo(m.getId_sender());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_circle() {
        return id_circle;
    }

    public void setId_circle(String id_circle) {
        this.id_circle = id_circle;
    }

    public String getId_sender() {
        return id_sender;
    }

    public void setId_sender(String id_sender) {
        this.id_sender = id_sender;
    }

    public String getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(String id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getLastname_sender() {
        return lastname_sender;
    }

    public void setLastname_sender(String lastname_sender) {
        this.lastname_sender = lastname_sender;
    }

    public String getFirstname_sender() {
        return firstname_sender;
    }

    public void setFirstname_sender(String firstname_sender) {
        this.firstname_sender = firstname_sender;
    }

    public int getId_image() {
        return id_image;
    }

    public void setId_image(int id_image) {
        this.id_image = id_image;
    }
}

