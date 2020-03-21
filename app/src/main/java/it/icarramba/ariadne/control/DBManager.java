package it.icarramba.ariadne.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Vector;

import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.entities.ItineraryMonument;
import it.icarramba.ariadne.entities.Monument;

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
            System.out.println("Created all tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertItinerary(String type, String departure, String meansOfTransp) throws SQLException{
        ContentValues cv = new ContentValues();
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

    private void insertMonument(String name, byte[] picture, String coordinates, String description) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Monuments.Name, name);
        cv.put(Constants.DBConstants.Monuments.Picture, picture);
        //use cursor and Cursor.getBlob(int columnNumber), in order to retrieve the picture from db
        cv.put(Constants.DBConstants.Monuments.Coordinates, coordinates);
        cv.put(Constants.DBConstants.Monuments.Description, description);

        long res = db.insert(Constants.DBConstants.Monuments.TableName, null, cv);

        if(res == -1){
            throw new SQLException("Could not insert Monument");
        }

    }

    private void insertItineraryMonument(int itinId, String monumName, int position, String expArrTime){
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

        //First of all the itinerary has to be saved
        this.insertItinerary(itinerary.getType(), itinerary.getDeparture(), itinerary.getMeansOfTransp());

        //retrieve the ID (auto)assigned to the itinerary
        Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
        int itID = cursor.getInt(0);
        cursor.close();
        System.out.println("LAST ID: "+itID);

        //Inserting all the monuments associated to the itinerary
        for (ItineraryMonument itiMon : itinerary.getItineraryMonuments()){
            this.insertMonument(itiMon.getMonument().getName(), itiMon.getMonument().getPicture(),
                                itiMon.getMonument().getCoordinates(), itiMon.getMonument().getDescription());
        }

        //Inserting now the itineraryMonuments
        for(ItineraryMonument itiMon : itinerary.getItineraryMonuments()) {
            this.insertItineraryMonument(itID, itiMon.getMonument().getName(), itiMon.getPosition(), itiMon.getExpectedArrTime());
        }

    }

    //this method will return saved itineraries or the last one requested
    public Itinerary[] getItineraries(String type){

        Vector<Itinerary> itineraries = new Vector<>();

        String query = "select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.name, M.Picture, M.Coordinates, M.Description " +
                        "from " +Constants.DBConstants.Itineraries.TableName+" I join "
                                +Constants.DBConstants.ItineraryMonuments.TableName+" IM on I.ID join "
                                +Constants.DBConstants.Monuments.TableName+" M " +
                         "where IM.ItineraryID == I.ID and I.Type == '"+type+"';";

        Cursor cursor = db.rawQuery(query, null);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //Retrieving all the itinerary requested
        Itinerary tempItin;
        Vector<ItineraryMonument> itineraryMonuments = new Vector<>();

        do{
            int currentItineraryID = cursor.getInt(0);

            //For each itinerary, find the monument associated and populate 'itineraryMonuments'
            while(true){
                itineraryMonuments.add(
                                        new ItineraryMonument(
                                                              new Monument(cursor.getString(6), cursor.getString(8),
                                                                            cursor.getBlob(7), cursor.getString(9)),
                                        cursor.getInt(4), cursor.getString(5)) );

                //i have to move the cursor to the next position,
                //if the cursor is empty, move back and break...
                boolean ret = cursor.moveToNext();
                if(!ret){
                    cursor.moveToPrevious();
                    break;
                }
                //if the new itinerary id is different from the last one
                //i have to break this cycle and move back the cursor
                if(cursor.getInt(0) != currentItineraryID){
                    cursor.moveToPrevious();
                    break;
                }
            }

            //Adding the itinerary
            tempItin = new Itinerary(cursor.getInt(0), cursor.getString(1),
                                    cursor.getString(2), cursor.getString(3),
                                    (ItineraryMonument[])itineraryMonuments.toArray());
            itineraries.add(tempItin);
            itineraryMonuments = new Vector<>();

        }while(cursor.moveToNext());

        return (Itinerary[])itineraries.toArray();

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
