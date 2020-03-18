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

        //TODO, metti l'avviamento dell'activity nella main activity non nel manifest,
        //TODO, (se metto la startActivity nella main mi apre sempre la tua)
        //TODO non tocco nulla nel manifest per non fare danni
        startActivity(new Intent(this, SavedItinerariesActivity.class));
        //TODO appena puoi rimodifica, non mi andava di scrivere sul tastierino del telefono

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "I have permissions", Toast.LENGTH_LONG).show();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            startSearchLocation();
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_RESULT);

        }

    }

    private void startSearchLocation() {
        positionView = findViewById(R.id.positionText);
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(this);
    }

    //Prendo il risultato della richiesta dei permessi
    //Se si, cerca la posizione
    //Se no, torna lla precedente activity
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RESULT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permesso dato", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permesso non dato", Toast.LENGTH_LONG).show();                }
            }
        }
    }

    @Override
    public void onSuccess(Object o) {
        // Got last known location. In some rare situations this can be null.
        Location location = (Location) o;
        positionView = findViewById(R.id.positionText);
        if (location != null) {
            positionView.setText( location.toString());
            Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, "Unable to find location", Toast.LENGTH_LONG).show();
        }
    }
}
