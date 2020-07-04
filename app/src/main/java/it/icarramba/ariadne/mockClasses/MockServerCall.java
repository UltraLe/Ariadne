package it.icarramba.ariadne.mockClasses;

import android.app.Activity;
import android.database.SQLException;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.enums.Trasport;

public class MockServerCall {

    private WeakReference<Activity> reference;

    //Fake HTTP Request to server
    public String sendReqeust(LatLng position, int interval, Trasport transChoice) throws Exception {
        //Traspoto = 1, a piedi
        //Trasporto = 2, macchina

        //Struttura della richiesta
        //http get /ip-server?latitude=...&longitute=...&interval=...&trans=...

        //Wait here for response
        int response = 200;
        if (response != 200) {
            throw new Exception("Cannot complete request");
        }
        //Useless http header
        String itineraryInfo = "dummy";
        return itineraryInfo;

    }

    public MockServerCall(Activity activity){
        this.reference = new WeakReference<>(activity);
    }

    public void mockServerCall(String typeToSave){

        //this method reads the mock itineraries json file,
        //simulating the interaction with the server into the cloud,
        //transforms it into an array of 'MonumentsReturnedServer' class
        //and adds the data into the local DB.

        //if there are itineraries, do not insert others
        if(DBManager.getInstance(reference.get()).isThereItinerary(typeToSave)){
            return;
        }

        Gson gson = new Gson();

        String jsonFile = readJson();

        Itinerary[] serverItins;

        serverItins = gson.fromJson(jsonFile, Itinerary[].class);

        int numItin = 1;
        for (Itinerary itin : serverItins){
            itin.setMeansOfTransp("Macchina");
            //System.out.println("\nItinerary number "+numItin);
            //itin.showInfo();
            numItin++;

            //SETTING itinerary type
            itin.setType(typeToSave);
        }

        //Inserting itineraries into the DB
        //DBQueryBean queryBean = new DBQueryBean(DBQueryBean.INSERT, serverItins, null);
        //(new DBTask(this)).execute(queryBean);
        for( Itinerary itin : serverItins){
            try {
                DBManager.getInstance(reference.get()).insertItinerary(itin);
            }catch (SQLException e){
                Toast.makeText(reference.get(), R.string.db_exception, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readJson(){

        String jsonTesterFile = "MockItineraries.json";
        BufferedReader reader = null;
        String jsonFile = "";

        try {
            reader = new BufferedReader(
                    new InputStreamReader(reference.get().getAssets().open(jsonTesterFile), "UTF-8"));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                jsonFile += mLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonFile;
    }
}
