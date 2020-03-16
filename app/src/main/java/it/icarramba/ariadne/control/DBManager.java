package it.icarramba.ariadne.control;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import it.icarramba.ariadne.constants.Constants;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance = null;
    private SQLiteDatabase db;

    private DBManager(@Nullable Context context) {
        super(context, Constants.DBConstants.DBName, null, Constants.DBConstants.DBVersion);
        db = this.getWritableDatabase();
        onCreate(db);
    }

    public DBManager getInstance(@Nullable Context context){
        if(instance == null){
            instance = new DBManager(context);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating DB Tables
        try {
            db.execSQL(Constants.DBConstants.Itineraries.CreateQuery);
            db.execSQL(Constants.DBConstants.Monuments.CreateQuery);
            db.execSQL(Constants.DBConstants.ItineraryMonuments.CreateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
