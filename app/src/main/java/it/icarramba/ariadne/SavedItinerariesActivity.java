package it.icarramba.ariadne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.mockClasses.MockServerCall;

public class SavedItinerariesActivity extends AppCompatActivity {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerari_salvati);

        title = findViewById(R.id.tvSITitle);
        //title.setBackground(getDrawable(R.drawable.colosseo));

        //inserting mock 'saved' itineraries
        (new MockServerCall(this)).mockServerCall(Constants.ItineraryType_Saved);

        //getting saved itineraries form DB
        Itinerary[] itinSaved = null;
        itinSaved = DBManager.getInstance(this).getItineraries(Constants.ItineraryType_Saved);

        //test if all was inserted/get correctly from DB
        int numItin = 1;
        for (Itinerary itin : itinSaved){
            System.out.println("\t\tITINERARY n." + numItin);
            itin.showInfo();
            numItin++;
            System.out.print("\n\n");
        }

        //TODO make a cool activity

    }
}
