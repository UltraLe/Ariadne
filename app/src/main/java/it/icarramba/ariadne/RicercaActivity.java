package it.icarramba.ariadne;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import it.icarramba.ariadne.enums.Trasport;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

public class RicercaActivity extends AppCompatActivity implements OnSuccessListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    private static final int PERMISSIONS_REQUEST_RESULT = 1;
    private TextView positionView;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currLocation = null;
    private Trasport currChoice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_);

        //Impost i listener dei toggle button
        ToggleButton footButton = findViewById(R.id.footButton);
        ToggleButton carButton = findViewById(R.id.carButton);

        footButton.setOnCheckedChangeListener(this);
        carButton.setOnCheckedChangeListener(this);

        //Controllo se i permessi di locazione sono stati concessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Sono stati concessi avvio la ricerca dell'ultima locazione
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            //Prendo la posizione selezionata
            double lat = getIntent().getDoubleExtra("Lat", 0);
            double longi = getIntent().getDoubleExtra("Long", 0);

            if (lat != 0) {
                currLocation = new LatLng(lat,longi);
                positionView = findViewById(R.id.positionText);
                positionView.setText(R.string.selected_pos_str);
            } else
                startSearchLocation();
        } else {
            //Chedo i permessi se non mi sono stati dati
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_RESULT);

        }

    }

    //Funzione per l'inizio della ricerca dell posizione
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
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startSearchLocation();
                } else {
                    Toast.makeText(this, R.string.premission_required_str, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(this, MainActivity.class));
                }

            }
            default: {
                Toast.makeText(this, R.string.premission_required_str, Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        }


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

            Toast.makeText(this, currChoice.toString(), Toast.LENGTH_LONG).show();

        }

    }

    //Chiamata epr i toggle
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //Attivo uno disattivo l'altro
        //Impost la current choice
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
}