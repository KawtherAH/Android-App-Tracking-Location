package com.example.trackme;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.trackme.databinding.ActivityTrackMeBinding;

import java.util.ArrayList;

public class TrackMeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTrackMeBinding binding;

    String TAG = "Location MapAct";
    private ArrayList<LatLng> locationArrayList;
    private ArrayList<String> locationNoteArraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationArrayList = new ArrayList<>();
        locationNoteArraylist = new ArrayList<>();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("LocBUNDLE");
        Bundle args2 = intent.getBundleExtra("noteBUNDLE");

        locationArrayList = (ArrayList<LatLng>) args.getSerializable("LocList");
        locationNoteArraylist = (ArrayList<String>) args2.getStringArrayList("LocNote");

        binding = ActivityTrackMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override//For visited location
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        float zoomLevel = 14.0f;

        for (int i = 0; i < locationArrayList.size(); i++) {

            if(i ==locationArrayList.size()-1) {

                Log.i( "LocationList", String.valueOf( locationArrayList.get( i ) ) );
                mMap.addMarker( new MarkerOptions().position( locationArrayList.get( i ) ).title( "Your Note: " + locationNoteArraylist.get( i ) ) );
                mMap.moveCamera( CameraUpdateFactory.newLatLng(locationArrayList.get( i )));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationArrayList.get( i ), zoomLevel));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

            }
            else{
                Log.i( "LocationList", String.valueOf( locationArrayList.get( i ) ) );
                mMap.addMarker( new MarkerOptions().position( locationArrayList.get( i ) ).title( "Your Note: " + locationNoteArraylist.get( i ) ) );
            }

        }



    }
}