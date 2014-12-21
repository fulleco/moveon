package com.application.moveon.model;

/**
 * Created by Quentin Bitschene on 21/12/2014.
 */
public class Cercle {
    private String id;
    private User admin;
    private String dateDebut;
    private String dateFin;
    private String timeDebut;
    private String timeFin;

    //TODO remplace String by class
    private String localisation;

    public Cercle(String id, User admin, String dateDebut, String dateFin, String timeDebut, String timeFin, String localisation) {
        this.id = id;
        this.admin = admin;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.timeDebut = timeDebut;
        this.timeFin = timeFin;
        this.localisation = localisation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public String getTimeDebut() {
        return timeDebut;
    }

    public void setTimeDebut(String timeDebut) {
        this.timeDebut = timeDebut;
    }

    public String getTimeFin() {
        return timeFin;
    }

    public void setTimeFin(String timeFin) {
        this.timeFin = timeFin;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }
}
