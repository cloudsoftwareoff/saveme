package com.example.saveme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.saveme.models.Hospital;
import com.example.saveme.models.PoliceStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private HospitalRepository hospitalRepository = new HospitalRepository();
    private List<Hospital> nearHospitals = new ArrayList<>();
    private List<PoliceStation> nearPoliceStations = new ArrayList<>();
    private Circle radiusCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fetchNearbyLocations();
    }

    private void fetchNearbyLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            drawRadiusCircle(location);
                            double myLatitude = location.getLatitude();
                            double myLongitude = location.getLongitude();
                            calculateNearbyLocations(myLatitude, myLongitude);
                        }
                    }
                });

    }

    private void calculateNearbyLocations(double myLatitude, double myLongitude) {
        hospitalRepository.fetchHospitals(new HospitalRepository.HospitalCallback() {
            @Override
            public void onCallback(List<Hospital> hospitalList) {
                for (Hospital hospital : hospitalList) {
                    double hospitalLat = hospital.getLatitude();
                    double hospitalLng = hospital.getLongitude();
                    float[] results = new float[1];
                    Location.distanceBetween(myLatitude, myLongitude, hospitalLat, hospitalLng, results);
                    float distance = results[0] / 1000; // Convert to kilometers
                    if (distance <= 30) { // Adjust the radius as needed
                        nearHospitals.add(hospital);
                    }
                }
                displayNearbyHospitalsOnMap();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("MapsActivity", "Error fetching hospitals", e);
            }
        });

        List<PoliceStation> policeStations = PoliceStationData.getPoliceStations();
        for (PoliceStation policeStation : policeStations) {
            double stationLat = policeStation.getLatitude();
            double stationLng = policeStation.getLongitude();
            float[] results = new float[1];
            Location.distanceBetween(myLatitude, myLongitude, stationLat, stationLng, results);
            float distance = results[0] / 1000; // Convert to kilometers
            if (distance <= 30) { // Adjust the radius as needed
                nearPoliceStations.add(policeStation);
            }
        }
        displayNearbyPoliceStationsOnMap();
    }

    private void displayNearbyHospitalsOnMap() {
        for (Hospital hospital : nearHospitals) {
            LatLng hospitalLocation = new LatLng(hospital.getLatitude(), hospital.getLongitude());
            int width = 80;
            int height = 80;
            Bitmap smallMarker = Bitmap.createScaledBitmap((
                    (BitmapDrawable) getResources().getDrawable(R.drawable.hospital)).getBitmap(), width, height, false); // Scale down the icon
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(hospitalLocation)
                    .title(hospital.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            mMap.addMarker(markerOptions);
        }

        // Set up marker click listener to show hospital name in info window
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
    }
    private void displayDirections(double destLatitude, double destLongitude) {
        String origin = "origin=" + mMap.getMyLocation().getLatitude() + "," + mMap.getMyLocation().getLongitude();
        String destination = "destination=" + destLatitude + "," + destLongitude;
        String mode = "mode=driving";
        String uri = "https://www.google.com/maps/dir/?api=1&" + origin + "&" + destination + "&" + mode;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
    private void displayNearbyPoliceStationsOnMap() {
        for (PoliceStation policeStation : nearPoliceStations) {
            LatLng stationLocation = new LatLng(policeStation.getLatitude(), policeStation.getLongitude());
            int width = 80;
            int height = 80;
            Bitmap smallMarker = Bitmap.createScaledBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.policeman)).getBitmap(), width, height, false); // Scale down the icon
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(stationLocation)
                    .title(policeStation.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)); // Set the scaled icon
            mMap.addMarker(markerOptions);
        }

        // Set up marker click listener to show police station name in info window
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchNearbyLocations();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Move camera to the user's location (if permission granted and location available)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void drawRadiusCircle(Location userLocation) {
        LatLng latLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        radiusCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(30000) // 30 km in meters
                .strokeColor(getResources().getColor(R.color.colorAccent))
                .strokeWidth(2));
    }
}
