package com.application.moveon.tools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.application.moveon.HomeActivity;

/**
 * Created by Hugo on 23/02/2015.
 */
public class Flags {

    private static boolean friendflag = false;
    private static boolean demandflag = false;
    private static boolean circleflag = false;
    private static boolean participantsflag = false;
    private static boolean messageflag = false;

    private static Context _context;


    public static void initialize(Context context){
        _context = context;
    }

    public static void setFriendflag(boolean friendflag) {
        Flags.friendflag = friendflag;
    }

    public static void setDemandflag(boolean demandflag){Flags.demandflag = demandflag;}

    public static void setCircleflag(boolean circleflag) {
        Flags.circleflag = circleflag;
    }

    public static void setMessageflag(boolean messageflag) {
        Flags.messageflag = messageflag;
    }

    public static void checkupdate(){
        if(friendflag && demandflag && circleflag && participantsflag && messageflag){

            Intent i = new Intent(_context, HomeActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            _context.startActivity(i);

            ((Activity) _context).finish();


        }
    }

    public static void setParticipantsflag(boolean participantsflag) {
        Flags.participantsflag = participantsflag;
    }
}
