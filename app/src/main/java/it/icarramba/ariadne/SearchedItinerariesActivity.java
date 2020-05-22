package it.icarramba.ariadne;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import it.icarramba.ariadne.adapters.AllItinerariesAdapter;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.CloudInteractor;
import it.icarramba.ariadne.control.CloudListener;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.listeners.DrawerListener;
import it.icarramba.ariadne.mockClasses.MockServerCall;

public class SearchedItinerariesActivity extends AppCompatActivity implements CloudListener {

    private RecyclerView rv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvati_drawer);

        DrawerSetUp();

        ((TextView)findViewById(R.id.tvSITitle)).setText(getString(R.string.searched_iti_title));

        CloudInteractor ci = new CloudInteractor("160.80.131.74", this, this);

        //TODO here the real Server call
        ci.sendRequest("1","2","10","bici");
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

    @Override
    public void beforeCall() {
        //making visible the loading animation

    }

    @Override
    public void afterCall(String response) {

        System.out.println("Reposnse: "+response);

        Gson gson = new Gson();
        Itinerary[] serverItins;
        serverItins = gson.fromJson(response, Itinerary[].class);
        //TODO once searched save into DB as 'Last Searched', and check if inserted new

        rv = findViewById(R.id.rv1);
        AllItinerariesAdapter adapter = new AllItinerariesAdapter(serverItins,this, true);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
    }

    @Override
    public void onError() {
        //makeing invisible the loading animation

        //and displaying an error message
    }
}
