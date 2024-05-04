package com.example.saveme;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ModeratorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moderator);
        TextView address = findViewById(R.id.address);
        TextView time = findViewById(R.id.time);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = getIntent().getStringExtra("id");
        DatabaseReference databaseReference = database.getReference("locations/" + userId);
        Button view_on_map = findViewById(R.id.view_on_map);
        view_on_map.setVisibility(View.GONE);
        final double[] latitude = { 0 };
        final double[] longitude = { 0 };
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                latitude[0] = snapshot.child("latitude").getValue(Double.class);
                longitude[0] = snapshot.child("longitude").getValue(Double.class);
                long timestamp = snapshot.child("timestamp").getValue(Long.class);
                String timeString = DateFormat.getDateTimeInstance().format(new Date(timestamp));
                address.setText(MapHelper.getAddressFromLocation(ModeratorActivity.this, latitude[0], longitude[0]));
                time.setText(timeString);
                view_on_map.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value

            }
        });

        view_on_map.setOnClickListener(v -> {
            String uri = "geo:" + latitude[0] + "," + longitude[0] + "?q=" + latitude[0] + "," + longitude[0];
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });
    }

}