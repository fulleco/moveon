package com.application.moveon.model;

import android.graphics.Bitmap;

/**
 * Created by damota on 25/11/2014.
 */
public class Message {
    private String id;
    private String id_circle;
    private String id_sender;
    private String id_receiver;
    private String content;
    private String date;
    private int seen;

    public Message(String id, String id_circle, String id_sender, String id_receiver, String content, String date, int seen){
        this.id = id;
        this.id_circle = id_circle;
        this.id_sender = id_sender;
        this.id_receiver = id_receiver;
        this.content = content;
        this.date = date;
        this.seen = seen;
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
}

