package com.application.moveon.sqlitedb;

/**
 * Created by Hugo on 20/02/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SQLiteDB extends SQLiteOpenHelper {

    private static final String TABLE_FRIEND = "Friend";
    private static final String COL_ID = "id_friend";
    private static final String COL_FIRSTNAME = "firstname";
    private static final String COL_LASTNAME = "lastname";
    private static final String COL_EMAIL = "email";
    private static final String COL_IMAGE = "imageprofile";
    private static final String TABLE_CIRCLEPARTICIPANTS = "CirclesParticipants";
    private static final String COL_IDCERCLESP = "id_cercle";


    public static final String TABLE_CERCLES = "Cercle";
    private static final String COL_ID_CERCLE = "id_cercle";
    private static final String COL_ID_CREATOR = "id_creator";
    private static final String COL_DATEDEBUT = "date_debut";
    private static final String COL_DATEFIN = "date_fin";
    private static final String COL_LAT = "latitude";
    private static final String COL_LONG="longitude";
    private static final String COL_RAY = "rayon";

    public static final String TABLE_MESSAGES = "Message";
    private static final String COL_ID_MESSAGE = "id_message";
    private static final String COL_ID_CERCLEM = "id_cercle";
    private static final String COL_ID_SENDER="id_sender";
    private static final String COL_NAMESENDER = "name";
    private static final String COL_CONTENT="id_content";
    private static final String COL_DATESEND="date";
    private static final String COL_SEEN = "seen";

    private static final String TABLE_FRIENDDEMANDS = "Friendship";
    private static final String COL_ID_DEMAND = "id_demand";
    private static final String COL_IDSENDER = "id_sender";
    private static final String COL_MAILSENDER = "mail_sender";
    private static final String COL_NAMESENDERD = "name_sender";
    private static final String COL_STATUS = "demand_status";
    private static final String COL_IMAGESENDER = "image_sender";

    private static final String CREATE_TABLE_FRIEND = "CREATE TABLE " + TABLE_FRIEND + " (" + COL_ID + " INTEGER PRIMARY KEY, "
                                + COL_FIRSTNAME + " TEXT NOT NULL, " + COL_LASTNAME + " TEXT NOT NULL, " + COL_EMAIL + " TEXT NOT NULL, "
                                + COL_IMAGE + " TEXT NOT NULL); ";

    private static final String CREATE_TABLE_CIRCLEPARTICIPANTS = "CREATE TABLE " + TABLE_CIRCLEPARTICIPANTS + " (" + COL_ID + " INTEGER NOT NULL, "
            + COL_FIRSTNAME + " TEXT NOT NULL, " + COL_LASTNAME + " TEXT NOT NULL, " + COL_EMAIL + " TEXT NOT NULL, "
            + COL_IMAGE + " TEXT NOT NULL, "+ COL_IDCERCLESP+ " INTEGER NOT NULL, PRIMARY KEY("+COL_ID + "," + COL_IDCERCLESP+")); ";

    private static final String CREATE_TABLE_CERCLE = "CREATE TABLE " + TABLE_CERCLES + " (" + COL_ID_CERCLE + " INTEGER PRIMARY KEY, "
                                + COL_ID_CREATOR + " INTEGER NOT NULL, " + COL_DATEDEBUT + " TEXT NOT NULL, " + COL_DATEFIN + " TEXT NOT NULL, " + COL_LAT
                                + " REAL NOT NULL, " + COL_LONG + " REAL NOT NULL, " + COL_RAY + " INTEGER DEFAULT 1000); ";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" + COL_ID_MESSAGE + " INTEGER PRIMARY KEY, "
                                +COL_ID_CERCLEM +" INTEGER NOT NULL, "+ COL_ID_SENDER + " INTEGER NOT NULL, " + COL_NAMESENDER + " TEXT NOT NULL, " + COL_CONTENT + " TEXT NOT NULL, "
                                + COL_DATESEND + " TEXT NOT NULL, " + COL_SEEN + "INTEGER NOT NULL );";

    private static final String CREATE_TABLE_FRIENDDEMAND = "CREATE TABLE " + TABLE_FRIENDDEMANDS + " (" + COL_ID_DEMAND + " INTEGER PRIMARY KEY, "
                                + COL_IDSENDER + " INTEGER NOT NULL, " + COL_MAILSENDER + " TEXT NOT NULL, " + COL_NAMESENDERD
                                + " TEXT NOT NULL, " + COL_STATUS + " INTEGER NOT NULL, "+ COL_IMAGESENDER + " TEXT NOT NULL ); ";


    public SQLiteDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        Log.i("MOVEON DATABASE", "CREATION DE LA BASE DE DONNEES");
        db.execSQL(CREATE_TABLE_FRIEND);
        db.execSQL(CREATE_TABLE_CERCLE);
        db.execSQL(CREATE_TABLE_MESSAGES);
        db.execSQL(CREATE_TABLE_FRIENDDEMAND);
        db.execSQL(CREATE_TABLE_CIRCLEPARTICIPANTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        Log.i("MOVEON DATABASE", "MISE A JOUR DE LA BASE DE DONNEES");
        db.execSQL("DROP TABLE " + TABLE_FRIEND + ";");
        db.execSQL("DROP TABLE " + TABLE_MESSAGES + ";");
        db.execSQL("DROP TABLE " + TABLE_CERCLES + ";");
        db.execSQL("DROP TABLE " + TABLE_FRIENDDEMANDS + ";");
        db.execSQL("DROP TABLE " + TABLE_CIRCLEPARTICIPANTS + ";");
        onCreate(db);
    }

}

