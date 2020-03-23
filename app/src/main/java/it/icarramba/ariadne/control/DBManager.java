package it.icarramba.ariadne.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

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

    private void insertItinerary(String type, String departure, String meansOfTransp) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Itineraries.Type, type);
        cv.put(Constants.DBConstants.Itineraries.Departure, departure);
        cv.put(Constants.DBConstants.Itineraries.MeansOfTransp, meansOfTransp);

        db.insert(Constants.DBConstants.Itineraries.TableName, null, cv);
    }

    private void insertMonument(String name, byte[] picture, String coordinates, String description) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.Monuments.Name, name);
        cv.put(Constants.DBConstants.Monuments.Picture, picture);
        cv.put(Constants.DBConstants.Monuments.Coordinates, coordinates);
        cv.put(Constants.DBConstants.Monuments.Description, description);

        db.insert(Constants.DBConstants.Monuments.TableName, null, cv);
    }

    private void insertItineraryMonument(int itinId, String monumName, int position, String expArrTime) throws SQLException{
        ContentValues cv = new ContentValues();
        cv.put(Constants.DBConstants.ItineraryMonuments.ItineraryID, itinId);
        cv.put(Constants.DBConstants.ItineraryMonuments.MonumentName, monumName);
        cv.put(Constants.DBConstants.ItineraryMonuments.Position, position);
        cv.put(Constants.DBConstants.ItineraryMonuments.ExpectedArrTime, expArrTime);

        db.insert(Constants.DBConstants.ItineraryMonuments.TableName, null, cv);
    }

    //The server into the cloud will return a list of itinerary,
    //some of them has to be saved (the last ones and saved ones).
    public void insertItinerary(Itinerary itinerary) throws SQLException{

        //First of all the itinerary has to be saved
        this.insertItinerary(itinerary.getType(), itinerary.getDeparture(), itinerary.getMeansOfTransp());
        //retrieve the ID (auto)assigned to the itinerary
        int itID = getLastItineraryID();

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

    //used to check result of queries
    private void printQuery(Cursor cursor){

        if (!(cursor.moveToFirst()) || cursor.getCount() == 0){
            cursor.close();
            System.out.println("Query has procuced nothing !");
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

        String query = "select I.ID, I.Type, I.Departure, I.MeansOfTransp, IM.Position, IM.ExpectedArrTime, M.Name, M.Picture, M.Coordinates, M.Description " +
                        "from " +Constants.DBConstants.Itineraries.TableName+" I join "
                                +Constants.DBConstants.ItineraryMonuments.TableName+" IM on I.ID join "
                                +Constants.DBConstants.Monuments.TableName+" M " +
                         "where IM.ItineraryID == I.ID and I.Type == '"+type+"' and IM.MonumentName == M.Name "+
                         "ORDER BY I."+Constants.DBConstants.Itineraries.ID+";";

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

            ItineraryMonument[] itinMonFound;
            if(itineraryMonuments.isEmpty()){
                itinMonFound = null;
            }else{
                itinMonFound = Arrays.copyOf(itineraryMonuments.toArray(), itineraryMonuments.toArray().length, ItineraryMonument[].class);
            }
            //Adding the itinerary
            tempItin = new Itinerary(cursor.getInt(0), cursor.getString(1),
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

    //retrieve the ID (auto)assigned of the last inserted itinerary
    private int getLastItineraryID() throws SQLException{
        String query = "SELECT "+ Constants.DBConstants.Itineraries.ID +
                        " FROM "+ Constants.DBConstants.Itineraries.TableName
                        + " ORDER BY "+Constants.DBConstants.Itineraries.ID+" DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int lastID = cursor.getInt(0);
        cursor.close();
        return lastID;
    }

    public boolean shouldRaiseDBException(String str){

        boolean raiseException = true;
        for(String e : Constants.DBConstants.ignoreDBExceptions){
            if(str.contains(e)){
                raiseException = false;
                break;
            }
        }

        return  raiseException;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
