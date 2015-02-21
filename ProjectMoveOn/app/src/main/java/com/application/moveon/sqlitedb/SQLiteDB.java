package com.application.moveon.sqlitedb;

/**
 * Created by Hugo on 20/02/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class SQLiteDB extends SQLiteOpenHelper {

    private static final String TABLE_FRIEND = "Friend";
    private static final String COL_ID = "id_friend";
    private static final String COL_FIRSTNAME = "firstname";
    private static final String COL_LASTNAME = "lastname";
    private static final String COL_EMAIL = "email";
    private static final String COL_IMAGE = "imageprofile";

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

    private static final String CREATE_TABLE_FRIEND = "CREATE TABLE " + TABLE_FRIEND + " (" + COL_ID + " INTEGER PRIMARY KEY, "
                                + COL_FIRSTNAME + " TEXT NOT NULL, " + COL_LASTNAME + " TEXT NOT NULL, " + COL_EMAIL + " TEXT NOT NULL, "
                                + COL_IMAGE + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_CERCLE = "CREATE TABLE " + TABLE_CERCLES + " (" + COL_ID_CERCLE + " INTEGER PRIMARY KEY, "
                                + COL_ID_CREATOR + " INTEGER NOT NULL, " + COL_DATEDEBUT + " TEXT NOT NULL, " + COL_DATEFIN + "TEXT NOT NULL, " + COL_LAT
                                + " REAL NOT NULL, " + COL_LONG + " REAL NOT NULL, " + COL_RAY + " INTEGER DEFAULT='1000');";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" + COL_ID_MESSAGE + " INTEGER PRIMARY KEY, "
                                +COL_ID_CERCLEM +" INTEGER NOT NULL, "+ COL_ID_SENDER + " INTEGER NOT NULL, " + COL_NAMESENDER + " TEXT NOT NULL, " + COL_CONTENT + " TEXT NOT NULL, "
                                + COL_DATESEND + " TEXT NOT NULL, " + COL_SEEN + "INTEGER NOT NULL );";



    private static final String CREATE_BDD = CREATE_TABLE_CERCLE + CREATE_TABLE_FRIEND + CREATE_TABLE_MESSAGES;

    public SQLiteDB(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_FRIEND + "; DROP TABLE " + TABLE_MESSAGES + "; DROP TABLE " + TABLE_CERCLES + ";");
        onCreate(db);
    }

}

