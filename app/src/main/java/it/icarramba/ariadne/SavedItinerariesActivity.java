package it.icarramba.ariadne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.entities.ItineraryMonument;

public class SavedItinerariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerari_salvati);

        mockServerCall();

        //TODO once here implement DB+Task getItineray, and make the activity
    }

    private void mockServerCall(){

        //this method reads the mock itineraries json file,
        //simulating the interaction with the server into the cloud,
        //transforms it into an array of 'MonumentsReturnedServer' class
        //and adds the data into the local DB.
        Gson gson = new Gson();

        String jsonFile = readJson();

        System.out.println("JsonFile:\n"+jsonFile);

        Itinerary[] serverItins;

        serverItins = gson.fromJson(jsonFile, Itinerary[].class);

        int numItin = 1;
        for (Itinerary itin : serverItins){
            System.out.println("\nItinerary number "+numItin);
            for (ItineraryMonument itiMon : itin.getItineraryMonuments()){
                System.out.println("Name: "+itiMon.getMonument().getName());
                System.out.println("Coordinates: "+itiMon.getMonument().getCoordinates());
                System.out.println("Expected Arrival Time: "+itiMon.getExpectedArrTime());
                System.out.println("Position: "+itiMon.getPosition());
                System.out.println("Picture: todo later...");
            }
            numItin++;
        }

        for (Itinerary itin : serverItins) {
            //TODO let it make somehow by the asynk task DBTask !!!
            DBManager.getInstance(this).insertItinerary(itin);

        }

    }

    private String readJson(){

        String jsonTesterFile = "MockItineraries.json";
        BufferedReader reader = null;
        String jsonFile = "";

        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(jsonTesterFile), "UTF-8"));

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
