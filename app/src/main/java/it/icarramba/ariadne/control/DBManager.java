package it.icarramba.ariadne.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void onCreate(SQLiteDatabase db){
        //Creating DB Tables, if they does not exist
        try {
            db.execSQL(Constants.DBConstants.Itineraries.CreateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(Constants.DBConstants.Monuments.CreateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(Constants.DBConstants.ItineraryMonuments.CreateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertItinerary(String ID, String type, String departure, String meansOfTransp) throws SQLException{
        ContentValues cv = new ContentValues();

        cv.put(Constants.DBConstants.Itineraries.ID, ID);
        cv.put(Constants.DBConstants.Itineraries.Type, type);
        cv.put(Constants.DBConstants.Itineraries.Departure, departure);
        cv.put(Constants.DBConstants.Itineraries.MeansOfTransp, meansOfTransp);

        db.insertOrThrow(Constants.DBConstants.Itineraries.TableName, null, cv);
    }

    private void insertMonument(String name, String picture, String coordinates, String description) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Monuments.Name, name);
        cv.put(Constants.DBConstants.Monuments.Picture, picture);
        cv.put(Constants.DBConstants.Monuments.Coordinates, coordinates);
        cv.put(Constants.DBConstants.Monuments.Description, description);

        db.insertOrThrow(Constants.DBConstants.Monuments.TableName, null, cv);
    }

    private void insertItineraryMonument(String itinId, String monumName, int position, String expArrTime) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.ItineraryMonuments.ItineraryID, itinId);
        cv.put(Constants.DBConstants.ItineraryMonuments.MonumentName, monumName);
        cv.put(Constants.DBConstants.ItineraryMonuments.Position, position);
        cv.put(Constants.DBConstants.ItineraryMonuments.ExpectedArrTime, expArrTime);

        db.insertOrThrow(Constants.DBConstants.ItineraryMonuments.TableName, null, cv);
    }

    //stupid method that checks if any itinerary of a selected type
    //is present in the db. Implemented for testing purpose
    public boolean isThereItinerary(String type){
        String query = "select ID " +
                        "from " +Constants.DBConstants.Itineraries.TableName+" "+
                        "where Type == '"+type+"';";

        Cursor cursor = db.rawQuery(query, null);
        //printQuery(cursor);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
            cursor.close();
            return false;
        }

        //if i'm here, there is some itinerary
        //to be used for testing purpose
        cursor.close();
        return true;
    }


    public void updateItineraryType(String type, String itinID){
        String query = "UPDATE "+Constants.DBConstants.Itineraries.TableName+" SET "+
                Constants.DBConstants.Itineraries.Type+" = '"+type+"' WHERE "+
                Constants.DBConstants.Itineraries.ID+" = '"+ itinID+"';";

        db.execSQL(query);

        //System.out.println("Updated Type!");
    }

    //The server into the cloud will return a list of itinerary,
    //some of them has to be saved (the last ones and saved ones).
    public void insertItinerary(Itinerary itinerary) throws SQLException {

        try {

            //First of all the itinerary has to be saved
            System.out.println("ID: "+itinerary.getID());
            try {
                this.insertItinerary(itinerary.getID(), itinerary.getType(), itinerary.getDeparture(), itinerary.getMeansOfTransp());
            }catch (SQLiteConstraintException s) {
                //itin is already in db, return
                return;
            }
            //Inserting all the monuments associated to the itinerary
            for (ItineraryMonument itiMon : itinerary.getItineraryMonuments()) {
                try {
                    this.insertMonument(itiMon.getMonument().getName(), itiMon.getMonument().getPicture(),
                            itiMon.getMonument().getCoordinates(), itiMon.getMonument().getDescription());
                } catch (SQLiteConstraintException s) {
                    //ignore
                }
            }
            //Inserting now the itineraryMonuments
            for (ItineraryMonument itiMon : itinerary.getItineraryMonuments()) {
                this.insertItineraryMonument(itinerary.getID(), itiMon.getMonument().getName(), itiMon.getPosition(), itiMon.getExpectedArrTime());
            }

        }catch (SQLException e){
            throw e;
        }

        System.out.println("Inserted: "+itinerary.getID()+" that has "+itinerary.getItineraryMonuments().length+" Monuments ("+itinerary.getType()+").");

    }

    //this method delete only the itinerary
    public void deleteItinerary(Itinerary itinerary){

        db.delete(Constants.DBConstants.ItineraryMonuments.TableName,
                Constants.DBConstants.ItineraryMonuments.ItineraryID+"='"+itinerary.getID()+"'",null);

        db.delete(Constants.DBConstants.Itineraries.TableName,
                Constants.DBConstants.Itineraries.ID+"='"+itinerary.getID()+"'",null);

    }

    //used to check result of queries
    private void printQuery(Cursor cursor){

        if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
            cursor.close();
            System.out.println("Query has produced nothing !");
            return;
        }
        int numCol = cursor.getColumnCount();
        for(int i = 0; i < numCol; ++i) {
            System.out.print(" | "+cursor.getColumnName(i));
        }
        System.out.print("\n");
        do{
            for(int i = 0; i < numCol; ++i) {
                switch(cursor.getType(i)){
                    case 0:
                        System.out.print(" | null");
                        break;
                    case 1:
                        System.out.print(" | "+cursor.getInt(i));
                        break;
                    case 3:
                        System.out.print(" | "+cursor.getString(i));
                        break;
                    case 4:
                        System.out.print(" | blob things....");
                        break;
                }

            }
            System.out.print("\n");
        }while(cursor.moveToNext());
    }

    //this method will return saved itineraries or the last one requested
    public Itinerary[] getItineraries(String type) throws SQLException{

        Vector<Itinerary> itineraries = new Vector<>();
        String query;

        if(type.equals(Constants.ItineraryType_LastSearched)){
            //mostra gli ultimo 10 itinerari
            query =  "select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.Name, 'pict', M.Coordinates, M.Description " +
                    //"select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.Name, M.Picture, M.Coordinates, M.Description " +
                    "from " +Constants.DBConstants.Itineraries.TableName+" I join "
                    +Constants.DBConstants.ItineraryMonuments.TableName+" IM on I.ID = IM."+Constants.DBConstants.ItineraryMonuments.ItineraryID+" join "
                    +Constants.DBConstants.Monuments.TableName+" M on IM.MonumentName = M."+Constants.DBConstants.Monuments.Name+
                    " where I.Type = '"+type+"' "+
                    "ORDER BY I."+Constants.DBConstants.Itineraries.ID+" DESC, IM."+Constants.DBConstants.ItineraryMonuments.Position+" limit 10;";
        }else{
            query =  "select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.Name, 'pict', M.Coordinates, M.Description " +
                    //"select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.Name, M.Picture, M.Coordinates, M.Description " +
                    "from " +Constants.DBConstants.Itineraries.TableName+" I join "
                    +Constants.DBConstants.ItineraryMonuments.TableName+" IM on I.ID = IM."+Constants.DBConstants.ItineraryMonuments.ItineraryID+" join "
                    +Constants.DBConstants.Monuments.TableName+" M on IM.MonumentName = M."+Constants.DBConstants.Monuments.Name+
                    " where I.Type = '"+type+"' "+
                    "ORDER BY I."+Constants.DBConstants.Itineraries.ID+", IM."+Constants.DBConstants.ItineraryMonuments.Position+";";
        }

        Cursor cursor = db.rawQuery(query, null);
        //printQuery(cursor);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
            cursor.close();
            return null;
        }

        //Retrieving all the itinerary requested
        Itinerary tempItin;
        Vector<ItineraryMonument> itineraryMonuments = new Vector<>();

        do{
            String currentItineraryID = cursor.getString(0);
            //For each itinerary, find the monument associated and populate 'itineraryMonuments'
            while(true){
                int start = 1;
                int step = 1000000;

                String picture = "";
                String subpicture;
                do {
                    subpicture = "";
                    String pictureQuery = "select SUBSTR(Picture, " + start + ", " + step + ") from " + Constants.DBConstants.Monuments.TableName +
                            " where " + Constants.DBConstants.Monuments.Name + " = '" + cursor.getString(6) + "';";
                    Cursor pictureCursor = db.rawQuery(pictureQuery, null);
                    if (!(pictureCursor.moveToFirst()) || pictureCursor.getCount() == 0) {
                        pictureCursor.close();
                        subpicture = "";
                    } else {
                        subpicture = pictureCursor.getString(0);
                        pictureCursor.close();
                    }
                    start += step;
                    picture += subpicture;
                }while(subpicture.length() > 0);

                itineraryMonuments.add(
                                        new ItineraryMonument(
                                                              new Monument(cursor.getString(6), cursor.getString(8),
                                                                            picture, cursor.getString(9)),
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
                if(!cursor.getString(0).equals(currentItineraryID)){
                    cursor.moveToPrevious();
                    break;
                }
            }
            ItineraryMonument[] itinMonFound;
            if(itineraryMonuments.isEmpty()){
                itinMonFound = null;
            }else{
                itinMonFound = Arrays.copyOf(itineraryMonuments.toArray(), itineraryMonuments.toArray().length, ItineraryMonument[].class);
            }
            //Adding the itinerary
            tempItin = new Itinerary(cursor.getString(0), cursor.getString(1),
                                    cursor.getString(2), cursor.getString(3),
                                    itinMonFound);
            itineraries.add(tempItin);
            itineraryMonuments = new Vector<>();

        }while(cursor.moveToNext());

        if(itineraries.isEmpty()){
            return null;
        }else{
            return Arrays.copyOf(itineraries.toArray(), itineraries.toArray().length, Itinerary[].class);
        }
    }

    private void shouldRaiseDBException(String str) throws SQLException{

        boolean thr = true;
        for(String e : Constants.DBConstants.ignoreDBExceptions){
            if(str.contains(e)){
                thr = false;
                break;
            }
        }
        if(thr){
            throw new SQLException(str);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
