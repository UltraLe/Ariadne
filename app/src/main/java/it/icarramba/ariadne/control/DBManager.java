package it.icarramba.ariadne.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.Itinerary;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager instance = null;
    private SQLiteDatabase db;

    private DBManager(@Nullable Context context) {
        super(context, Constants.DBConstants.DBName, null, Constants.DBConstants.DBVersion);
        db = this.getWritableDatabase();
        onCreate(db);
    }

    public static DBManager getInstance(@Nullable Context context){
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

    private void insertItinerary(String id, String type, String departure, String meansOfTransp) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Itineraries.ID, id);
        cv.put(Constants.DBConstants.Itineraries.Type, type);
        cv.put(Constants.DBConstants.Itineraries.Departure, departure);
        cv.put(Constants.DBConstants.Itineraries.MeansOfTransp, meansOfTransp);

        /* delete if content values work
        String query = query = "INSERT INTO " + Constants.DBConstants.Itineraries.TableName +
                        "("+Constants.DBConstants.Itineraries.ID+","+Constants.DBConstants.Itineraries.Type+","
                            +Constants.DBConstants.Itineraries.Departure+","+Constants.DBConstants.Itineraries.MeansOfTransp+")VALUES('"
                            +id+ "','" + type + "','" + departure + "','" + meansOfTransp + "');";

         this.db.execSQL(query);
         */

        long res = db.insert(Constants.DBConstants.Itineraries.TableName, null, cv);

        if(res == -1){
            throw new SQLException("Could not insert Itinerary");
        }

    }

    private void insertMonument(String name, byte[] picture, String coordinates) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Monuments.Name, name);
        cv.put(Constants.DBConstants.Monuments.Picture, picture);
        //use cursor and Cursor.getBlob(int columnNumber), in order to retrieve the picture from db
        cv.put(Constants.DBConstants.Monuments.Coordinates, coordinates);

        long res = db.insert(Constants.DBConstants.Monuments.TableName, null, cv);

        if(res == -1){
            throw new SQLException("Could not insert Monument");
        }

    }

    private void insertItineraryMonument(String itinId, String monumName, int position, String expArrTime){
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.ItineraryMonuments.ItineraryID, itinId);
        cv.put(Constants.DBConstants.ItineraryMonuments.MonumentName, monumName);
        cv.put(Constants.DBConstants.ItineraryMonuments.Position, position);
        cv.put(Constants.DBConstants.ItineraryMonuments.ExpectedArrTime, expArrTime);

        long res = db.insert(Constants.DBConstants.ItineraryMonuments.TableName, null, cv);

        if(res == -1){
            throw new SQLException("Could not insert ItineraryMonument");
        }
    }

    //The server into the cloud will return a list of itinerary,
    //some of them has to be saved (the last ones and saved ones).
    public void insertItinerary(Itinerary itinerary){

    }

    //this method will return saved itineraries or the last one 'searched'
    public Itinerary[] getItineraries(int type){

        Itinerary[] itineraries = null;
        return itineraries;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
