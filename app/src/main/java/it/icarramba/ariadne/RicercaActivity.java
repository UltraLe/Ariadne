package it.icarramba.ariadne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.CloudInteractor;
import it.icarramba.ariadne.enums.Trasport;
import it.icarramba.ariadne.listeners.CloudListener;
import it.icarramba.ariadne.listeners.DrawerListener;
import it.icarramba.ariadne.mockClasses.MockServerCall;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.FallbackServiceBroker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class RicercaActivity extends AppCompatActivity implements TextView.OnEditorActionListener,OnSuccessListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener{


    private static final int PERMISSIONS_REQUEST_RESULT = 1;
    private TextView positionView;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currLocation = null;
    private Trasport currChoice = null;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_drawer);

        //Set up drawer
        DrawerSetUp();

        //Impost i listener dei toggle button
        ToggleButton footButton = findViewById(R.id.footButton);
        ToggleButton carButton = findViewById(R.id.carButton);

        footButton.setOnCheckedChangeListener(this);
        carButton.setOnCheckedChangeListener(this);

        //Prendo la posizione selezionata
        double lat = getIntent().getDoubleExtra("Lat", 0);
        double longi = getIntent().getDoubleExtra("Long", 0);

        if (lat != 0) {
            currLocation = new LatLng(lat,longi);
            positionView = findViewById(R.id.positionText);
            positionView.setText(R.string.selected_pos_str);
        }

        //Controllo se i permessi di locazione sono stati concessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Sono stati concessi avvio la ricerca dell'ultima locazione
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (lat == 0)
                startSearchLocation();
        } else {
            //Chedo i permessi se non mi sono stati dati
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_RESULT);

        }

    }

    //Funzione per l'inizio della ricerca della posizione
    private void startSearchLocation() {
        positionView = findViewById(R.id.positionText);
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(this);
    }

    //Prendo il risultato della richiesta dei permessi
    //Se si, cerca la posizione
    //Se no, torna alla precedente activity
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RESULT) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Sono stati concessi avvio la ricerca dell'ultima locazione
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                startSearchLocation();
                return;
            } else {
                Toast.makeText(this, R.string.premission_required_str, Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(this, R.string.premission_required_str, Toast.LENGTH_LONG).show();
        //Se non mi vengono dati i permessi metto un hint
        positionView = findViewById(R.id.positionText);
        positionView.setHint(R.string.position_hint_str);

    }

    //Funzione che si attiva qunado una posizione viene trovata
    @Override
    public void onSuccess(Object o) {

        //Il metodo è stato chiamato dal location listener
        Location location = (Location) o;
        positionView = findViewById(R.id.positionText);
        if (location != null) {
            positionView.setText(R.string.position_str);
            currLocation = new LatLng(location.getLatitude(), location.getLongitude());

        } else {
            //Se non trovo la posizione metto un suggerimento nella box
            positionView.setHint(R.string.position_hint_str);
        }

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.positionText) {
            //Passo la posizione corrente alla place picker
            //0,0 se non ce l'ho
            Intent intent = new Intent(getBaseContext(), PlacePickerActivity.class);
            if (currLocation != null) {
                intent.putExtra("Long", currLocation.longitude);
                intent.putExtra("Lat", currLocation.latitude);
            }

            startActivity(intent);
        } else if (v.getId() == R.id.confirmButton) {

            //Controlla se la posizione è inserita
            positionView = findViewById(R.id.positionText);
            if (positionView.getText() == "") {
                Toast.makeText(this, R.string.no_position_err_str, Toast.LENGTH_LONG).show();
                return;
            }

            //Controllo se l'intervallo è stato inserito
            EditText timeView = findViewById(R.id.intervalloText);
            String timeString = timeView.getText().toString();
            if (!timeString.matches("\\d+:\\d{2}")) {
                Toast.makeText(this, R.string.bad_time_str, Toast.LENGTH_LONG).show();
                return;
            }
            if (currChoice == null) {
                Toast.makeText(this, R.string.no_transport_str, Toast.LENGTH_LONG).show();
                return;
            }
            //in minutes
            String[] splitted = timeString.split(":");
            int chosenIntervall = Integer.parseInt(splitted[0])*60*60+Integer.parseInt(splitted[1])*60;

            //Send info to other activity
            ArrayList<String> lista = new ArrayList<>();
            lista.add(String.valueOf(currLocation.latitude));
            lista.add(String.valueOf(currLocation.longitude));
            lista.add(String.valueOf(chosenIntervall));
            lista.add(currChoice.toString());

            Intent i = new Intent(this, SearchedItinerariesActivity.class);
            i.putExtra("info", lista);
            startActivity(i);
        }

    }

    //Chiamata per i toggle
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //Attivo uno disattivo l'altro
        //Imposto la current choice
        //Toast.makeText(this, String.valueOf(buttonView.getId()), Toast.LENGTH_LONG).show();
        if (buttonView.getId() == R.id.footButton && isChecked) {

            ToggleButton carsButton = findViewById(R.id.carButton);

            buttonView.setChecked(true);
            carsButton.setChecked(false);
            currChoice = Trasport.Foot;

        } else if (isChecked){

            ToggleButton footButton = findViewById(R.id.footButton);

            buttonView.setChecked(true);
            footButton.setChecked(false);

            currChoice = Trasport.Car;

        }

    }
    //Drawer functions
    private void DrawerSetUp() {

        dl = findViewById(R.id.activity_ricerca_drawer_in);
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
    //Chiudi tastiera quando clicchi "done"
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }

}
