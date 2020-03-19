package it.icarramba.ariadne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Ricerca_Activity extends AppCompatActivity implements OnSuccessListener {


    private static final int PERMISSIONS_REQUEST_RESULT = 1;
    private TextView positionView;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca_);

        //Controllo se i permessi di locazione sono stati concessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Sono stati concessi avvio la ricerca dell'ultima locazione
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                    Toast.makeText(this, R.string.premission_required_str, Toast.LENGTH_LONG).show();                }
            }
        }
    }

    //Funzione che si attiva qunado una posizione viene trovata
    @Override
    public void onSuccess(Object o) {
        Location location = (Location) o;
        positionView = findViewById(R.id.positionText);
        if (location != null) {
            positionView.setText(R.string.position_str);
            Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

        } else {
            //Se non trovo la posizione metto un suggerimento nella box
            positionView.setHint(R.string.position_hint_str);
        }
    }
}
