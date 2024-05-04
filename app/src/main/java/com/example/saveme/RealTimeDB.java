package com.example.saveme;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RealTimeDB {
    public static void pushLocationToDatabase(String key, double latitude, double longitude) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        long timestamp = System.currentTimeMillis();

        
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("latitude", latitude);
        locationMap.put("longitude", longitude);
        locationMap.put("timestamp", timestamp);

        // Push the map to the database 
        databaseReference.child("locations").child(key).setValue(locationMap);
    }

}
