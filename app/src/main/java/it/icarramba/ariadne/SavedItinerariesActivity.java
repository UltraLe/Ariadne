package it.icarramba.ariadne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.icarramba.ariadne.bean.DBQueryBean;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.entities.Itinerary;;
import it.icarramba.ariadne.tasks.DBTask;

public class SavedItinerariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerari_salvati);

        mockServerCall();

        //getting saved itineraries form DB
        Itinerary[] itinSaved = null;
        DBQueryBean queryBean = new DBQueryBean(DBQueryBean.GET, itinSaved, Constants.ItineraryType_Saved);
        (new DBTask(this)).execute(queryBean);

        //test if all was inserted/get correctly from DB
        int numItin = 1;
        for (Itinerary itin : itinSaved){
            System.out.println("\nItinerary number "+numItin);
            itin.showInfo();
            numItin++;
        }

        //make stuff with itinSaved.
        //.....


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
            itin.showInfo();
            numItin++;

            //SETTING itinerary type saved
            itin.setType(Constants.ItineraryType_Saved);
        }

        //Inserting itineraries into the DB
        DBQueryBean queryBean = new DBQueryBean(DBQueryBean.INSERT, serverItins, null);
        (new DBTask(this)).execute(queryBean);
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
