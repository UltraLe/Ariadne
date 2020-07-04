package it.icarramba.ariadne;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import it.icarramba.ariadne.adapters.AllItinerariesAdapter;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.listeners.DrawerListener;

public class SavedItinerariesActivity extends AppCompatActivity {

    private RecyclerView rv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvati_drawer);

        //Set up drawer
        DrawerSetUp();

        //inserting mock 'saved' itineraries
        //(new MockServerCall(this)).mockServerCall(Constants.ItineraryType_Saved);

        //getting saved itineraries form DB
        Itinerary[] itinSaved = DBManager.getInstance(this).getItineraries(Constants.ItineraryType_Saved);

        rv = findViewById(R.id.rv1);
        AllItinerariesAdapter adapter = new AllItinerariesAdapter(itinSaved,this, false);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
    }

    private void DrawerSetUp() {

        dl = findViewById(R.id.activity_saved_drawer_in);
        t = new ActionBarDrawerToggle(this, dl,R.string.app_name, R.string.app_name);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new DrawerListener(this));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}
