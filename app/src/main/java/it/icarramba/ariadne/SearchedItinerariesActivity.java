package it.icarramba.ariadne;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

import it.icarramba.ariadne.adapters.AllItinerariesAdapter;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.CloudInteractor;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.listeners.CloudListener;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.listeners.DrawerListener;

public class SearchedItinerariesActivity extends AppCompatActivity implements CloudListener {

    private RecyclerView rv;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private ProgressBar pb;
    private TextView serverError;
    private ImageView imageError;
    private ArrayList<String> fogList;
    private int last = 0;
    private boolean lastTry = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvati_drawer);

        DrawerSetUp();
        pb = findViewById(R.id.progressBar);

        serverError = findViewById(R.id.tvServerError);
        imageError = findViewById(R.id.errorImage);

        ((TextView)findViewById(R.id.tvSITitle)).setText(getString(R.string.searched_iti_title));

        //Ask for fog list
        CloudInteractor ciboot = new CloudInteractor(null, this, this);
        ciboot.fogListRequest(3, "1234.2", "1233.3");


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
        pb.setVisibility(View.VISIBLE);

    }

    @Override
    public void afterCall(String response) {

        pb.setVisibility(View.INVISIBLE);

        Gson gson = new Gson();
        Itinerary[] serverItins;
        serverItins = gson.fromJson(response, Itinerary[].class);

        if(serverItins.length > 0 && serverItins[0].getID() != null){
            for(Itinerary itin : serverItins) {
                itin.setType(Constants.ItineraryType_LastSearched);
                DBManager.getInstance(this).insertItinerary(itin);
            }

            rv = findViewById(R.id.rv1);
            AllItinerariesAdapter adapter = new AllItinerariesAdapter(serverItins,this, true);
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rv.setAdapter(adapter);
        }else{
            serverError.setText(R.string.noitin_found);
            serverError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError() {

        if (lastTry) {
            System.out.println("Failed");
            //making invisible the loading animation
            serverError.setVisibility(View.VISIBLE);
            imageError.setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
            return;
        }
        last++;

        if (last < fogList.size()) {
            System.out.println("tried " + fogList.get(last) + last);
            call_fog(fogList.get(last), Constants.Cloud.SERVER_PORT_HERMES);
        } else {
            lastTry = true;
            call_fog(Constants.Cloud.ULIXES_IP, Constants.Cloud.SERVER_PORT_ULIXES);

        }
    }

    @Override
    public void afterBootstrapCall(ArrayList<String> fogIpList) {

        fogList = fogIpList;

        //Make some calls fail
        //fogList.set(0, "123.123.123.123");
        //fogList.set(1, "123.123.123.123");

        System.out.println("Nodes online ("+fogIpList.size()+"):");
        for(String ip : fogIpList){
            System.out.println(ip);
        }

        if (fogList.size() != 0)
            call_fog(fogList.get(0), Constants.Cloud.SERVER_PORT_HERMES);
        else {
            lastTry = true;
            call_fog(Constants.Cloud.ULIXES_IP, Constants.Cloud.SERVER_PORT_ULIXES);
        }

    }

    public void call_fog(String ip, int PORT) {

        CloudInteractor ci = new CloudInteractor(ip, this, this);

        ArrayList<String> info = getIntent().getExtras().getStringArrayList("info");
        System.out.println(info.get(0) + " "+ info.get(1)+" "+ info.get(2)+" "+ info.get(3));
        ci.sendRequest(info.get(0), info.get(1), info.get(2), info.get(3), PORT);
    }
    @Override
    public void beforeBootstrapCall() {
        //making visible the loading animation
        pb.setVisibility(View.VISIBLE);
    }
}
