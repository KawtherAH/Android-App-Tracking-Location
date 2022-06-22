package com.example.trackme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.trackme.databinding.ActivityTrackMeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class TrackCurrActivity extends FragmentActivity implements OnMapReadyCallback{
        private GoogleMap mMap;
        private ActivityTrackMeBinding binding;

        String TAG = "Location CurrentMap";
        private double CurrentLatitude, CurrentLongitude;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );

            Intent i = this.getIntent();
            CurrentLatitude = i.getDoubleExtra( "lat", 0.0 );
            CurrentLongitude = i.getDoubleExtra( "lon", 0.0 );

            binding = ActivityTrackMeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    @Override//For current location
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 14.0f;

        LatLng CurrentLocation = new LatLng(CurrentLatitude, CurrentLongitude);

        mMap.addMarker(new MarkerOptions().position(CurrentLocation).title("Your current Location!"));
        mMap.moveCamera( CameraUpdateFactory.newLatLng(CurrentLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLocation, zoomLevel));

        Log.i(TAG,"Latitude: "+ CurrentLatitude + "  ,  " + "Longitude: "+ CurrentLongitude);
    }
}
