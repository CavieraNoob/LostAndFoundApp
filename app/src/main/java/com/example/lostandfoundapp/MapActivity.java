package com.example.lostandfoundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper databaseHelper;
    private Location currentLocation;

    private EditText editTextRadius;
    private Button buttonApplyRadius;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private double radiusKm = 10.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextRadius = findViewById(R.id.editTextRadius);
        buttonApplyRadius = findViewById(R.id.buttonApplyRadius);

        editTextRadius.setText("10");

        buttonApplyRadius.setOnClickListener(v -> {
            String radiusText = editTextRadius.getText().toString().trim();

            if (!radiusText.isEmpty()) {
                try {
                    radiusKm = Double.parseDouble(radiusText);
                    loadCurrentLocation();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid radius", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a radius", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        loadCurrentLocation();
    }

    private void loadCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                LatLng currentLatLng = new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));

                showItemsWithinRadius();

            } else {
                Toast.makeText(
                        this,
                        "Unable to get current location. Please set emulator location and try again.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void showItemsWithinRadius() {
        googleMap.clear();

        LatLng currentLatLng = new LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );

        googleMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title("Your Current Location"));

        List<Advert> adverts = databaseHelper.getAllAdverts();

        int shownCount = 0;

        for (Advert advert : adverts) {
            double itemLat = advert.getLatitude();
            double itemLng = advert.getLongitude();

            if (itemLat == 0.0 && itemLng == 0.0) {
                continue;
            }

            float[] results = new float[1];

            Location.distanceBetween(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    itemLat,
                    itemLng,
                    results
            );

            double distanceKm = results[0] / 1000.0;

            if (distanceKm <= radiusKm) {
                LatLng itemLocation = new LatLng(itemLat, itemLng);

                googleMap.addMarker(new MarkerOptions()
                        .position(itemLocation)
                        .title(advert.getName())
                        .snippet(advert.getType() + " - " + String.format("%.2f km away", distanceKm)));

                shownCount++;
            }
        }

        Toast.makeText(
                this,
                "Showing " + shownCount + " item(s) within " + radiusKm + " km",
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}