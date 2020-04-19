package it.icarramba.ariadne;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private MapView mapView;
    private GoogleMap map;
    private LatLng currLocation = null;
    private Marker currMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        //Prendo la posizione corrente
        double lat = getIntent().getDoubleExtra("Lat", 0);
        double longi = getIntent().getDoubleExtra("Long", 0);

        if (lat != 0) {
            currLocation = new LatLng(lat,longi);
        }

        //Imposto la mappa
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapSetUp();

    }

    //Chiedo di attivare la mappa
    private void mapSetUp() {
        mapView.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(true);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);

        //Sposto la telecamera sulla posizione corrente se c'è
        //altrimenti su Roma
        CameraUpdate cameraUpdate;
        if (currLocation != null)
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.latitude, currLocation.longitude), 15);
        else
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(41.890251, 12.492373), 100);

        map.animateCamera(cameraUpdate);

        map.setOnMapClickListener(this);

        mapView.onResume();

    }


    public void onMapClick(LatLng clickPos) {

        if (currMarker == null) {
            currMarker = map.addMarker(new MarkerOptions().position(clickPos).title(getString(R.string.start_str)).draggable(true));
        } else {
            currMarker.remove();
            currMarker = map.addMarker(new MarkerOptions().position(clickPos).title(getString(R.string.start_str)).draggable(true));
        }

    }

    @Override
    public void onClick(View v) {

        if (currMarker != null) {
            Intent intent = new Intent(getBaseContext(), RicercaActivity.class);
            intent.putExtra("Long", currMarker.getPosition().longitude);
            intent.putExtra("Lat", currMarker.getPosition().latitude);
            startActivity(intent);
        } else
            Toast.makeText(this, getString(R.string.no_loc_string), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

}
