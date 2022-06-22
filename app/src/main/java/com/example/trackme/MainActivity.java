package com.example.trackme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    String TAG = "Location MainAct";

    private ArrayList<LatLng> locationArrayList;
    private ArrayList<String> locationNoteArraylist;

    Bundle args, args2;
    Intent TrackMeIntent, TrackCurrIntent;

    Button View, add, ViewCurr;
    AlertDialog AddNoteDialog;
    String n;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TrackMeIntent = new Intent(MainActivity.this, TrackMeActivity.class);
        TrackCurrIntent = new Intent(MainActivity.this, TrackCurrActivity.class);
        args = new Bundle();
        args2 = new Bundle();

        locationArrayList = new ArrayList<>();
        locationNoteArraylist = new ArrayList<>();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        View = findViewById( R.id.button );
        add = findViewById( R.id.button2 );
        ViewCurr = findViewById( R.id.button3 );

        View.setOnClickListener( new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveLocations();
            }
        });

        ViewCurr.setOnClickListener( new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentLocation();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Enter the note" );
        View view = getLayoutInflater().inflate( R.layout.note,null );

        EditText note;
        note = view.findViewById( R.id.note );

        Button add1 = view.findViewById( R.id.add1 );
        add1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                n = note.getText().toString();
                Toast.makeText(getBaseContext(), "Successfully add Note"
                       , Toast.LENGTH_LONG).show();

                AddtLocation( n );
                note.setText("");
                AddNoteDialog.dismiss();
            }
        } );
        builder.setView( view );
        AddNoteDialog = builder.create();
        add.setOnClickListener( new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNoteDialog.show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (!isGPSEnabled()) {
                    turnOnGPS();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    private void AddtLocation(String n) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        Log.i(TAG,"Latitude: "+ latitude + "  ,  " + "Longitude: "+ longitude);

                                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                                        ContentValues cv = new ContentValues();
                                        cv.put(dbHelper.KEY_NOTE, n);
                                        cv.put(dbHelper.KEY_LAT, latitude);
                                        cv.put(dbHelper.KEY_LON, longitude);

                                        long rowId = db.insert(dbHelper.TABLE_LOCATIONS, null, cv);
                                        if(rowId>0){
                                            Toast.makeText(getBaseContext(), "Your Place Added Successfully, Location ID is "
                                                    + rowId, Toast.LENGTH_LONG).show();
                                        }
                                        dbHelper.close();
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void CurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        Log.i(TAG,"Latitude: "+ latitude + "  ,  " + "Longitude: "+ longitude);

                                        TrackCurrIntent.putExtra("lat", latitude);
                                        TrackCurrIntent.putExtra("lon", longitude);

                                        startActivity(TrackCurrIntent);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void retrieveLocations(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + dbHelper.TABLE_LOCATIONS,null);
        while(cursor.moveToNext()){

            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID));
            @SuppressLint("Range")String note = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_NOTE));
            locationNoteArraylist.add(note);

            @SuppressLint("Range")Double lat = cursor.getDouble(cursor.getColumnIndex(dbHelper.KEY_LAT));
            @SuppressLint("Range")Double lon = cursor.getDouble(cursor.getColumnIndex(dbHelper.KEY_LON));
            locationArrayList.add(new LatLng(lat,lon));
        }
        dbHelper.close();

        args.putSerializable("LocList",(Serializable)locationArrayList);
        args2.putStringArrayList("LocNote", locationNoteArraylist );
        TrackMeIntent.putExtra("LocBUNDLE", args);
        TrackMeIntent.putExtra("noteBUNDLE", args2);
        startActivity(TrackMeIntent);
    }

    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }


}