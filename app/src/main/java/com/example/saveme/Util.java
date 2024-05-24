package com.example.saveme;

import android.location.Location;

public class Util {
    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0] / 1000; // Convert meters to kilometers
    }
}
