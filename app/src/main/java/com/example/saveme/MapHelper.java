package com.example.saveme;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapHelper {
    public static String getAddressFromLocation(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                return address.getAddressLine(0);

            } else {
                return  "Address not found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return  "Error: " + e.getMessage();
        }
    }
    public static void openMap(Context context, double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // If there is no map application installed, you can handle this case accordingly
            Toast.makeText(context, "No map application found", Toast.LENGTH_SHORT).show();
        }
    }
}
