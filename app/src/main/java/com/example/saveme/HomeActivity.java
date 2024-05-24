package com.example.saveme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import android.Manifest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Handler handlerAnimation = new Handler();
    private boolean statusAnimation = false;
    double latitude, longitude;
    private ImageView imgAnimation1;
    private ImageView imgAnimation2;
    private Button button;
    TextView name;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView username, user_email;
    private List<Hospital> hospitalList = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener;
    List<Contact> contacts;
    boolean send = false;
    SharedPreferences sharedPreferences;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // init
        imgAnimation1 = findViewById(R.id.imgAnimation1);
        imgAnimation2 = findViewById(R.id.imgAnimation2);
        name = findViewById(R.id.name);
        TextView nearestDoctorCount = findViewById(R.id.nearest);
        button = findViewById(R.id.button);
        ImageView friends = findViewById(R.id.friends_btn);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout drawer = findViewById(R.id._nav_view);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.axon);
        MediaPlayer startSound = MediaPlayer.create(this, R.raw.started);
        username = drawer.findViewById(R.id.username);
        user_email = drawer.findViewById(R.id.user_email);
        LinearLayout log_out = drawer.findViewById(R.id.logout);
        LinearLayout settings = drawer.findViewById(R.id.setting_linear);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        LinearLayout about_linear = drawer.findViewById(R.id.about_linear);
        ImageView menu = findViewById(R.id.menu);
        LinearLayout user_profile = drawer.findViewById(R.id.linear_profile);
        final boolean[] startSoundPlayed = { false };
        button.setEnabled(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // load data
        LoadUser();
        contacts = loadContactsList();
        // min distance
        double min_distance = 50;
        // Listen for location
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (!startSoundPlayed[0] && !startSound.isPlaying() && sharedPreferences.getBoolean("sound", true)) {
                    startSound.start();
                    button.setEnabled(true);
                    startSoundPlayed[0] = true;
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                if (sharedPreferences.getBoolean("stream", true)) {
                    RealTimeDB.pushLocationToDatabase(FirebaseAuth.getInstance().getUid(), latitude, longitude);

                }
                if (hospitalList != null && (user.isDiabetes() || user.isHeartProblem())) {
                    int count = 0;
                    for (Hospital hospital : hospitalList) {
                        double distance = Util.calculateDistance(latitude, longitude, hospital.getLatitude(),
                                hospital.getLongitude());
                        if (distance < min_distance) {
                            count++;
                        }
                    }
                    nearestDoctorCount.setText(count + " hospitals near you");

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Check for permissions
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        } else {
            // Start listening for location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // EditProfile
        user_profile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EditUserActivity.class);
            startActivity(intent);
        });

        // Logout
        log_out.setOnClickListener(v -> {

            showLogoutDialog();
        });
        // Settings
        settings.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(i);
        });
        // Dawer
        menu.setOnClickListener(event -> {
            drawerLayout.openDrawer(GravityCompat.START);

        });
        friends.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FriendsList.class);
            startActivity(intent);
        });

        about_linear.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AboutusActivity.class);
            startActivity(intent);
        });
        nearestDoctorCount.setOnClickListener(event -> {
            Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(intent);
        });
        // Activate
        button.setOnClickListener(v -> {
            if (statusAnimation) {
                stopPulse();
                button.setText("SOS");
                send = false;

            } else {
                startPulse();
                button.setText("stop");
                if (!send) {
                    if (!mediaPlayer.isPlaying() && sharedPreferences.getBoolean("sound", true)) {
                        mediaPlayer.start();
                    }

                    // send to message to nearest hospital ?
                    // raduis 50Km
                    if (hospitalList != null && (user.isDiabetes() || user.isHeartProblem())) {
                        for (Hospital hospital : hospitalList) {
                            double distance = Util.calculateDistance(latitude, longitude, hospital.getLatitude(),
                                    hospital.getLongitude());
                            if (distance < min_distance) {
                                if (sharedPreferences.getBoolean("vibration", true)) {
                                    VibrationHelper.vibrate(this, 200);
                                }
                                SMSHelper.sendMapLocationSMS(HomeActivity.this, "civilian ", user.getName(),
                                        hospital.getPhone(), latitude, longitude);
                            }
                        }
                    }

                    // send message to his doctor
                    if (user.isDiabetes() || user.isHeartProblem()) {
                        if (sharedPreferences.getBoolean("vibration", true)) {
                            VibrationHelper.vibrate(this, 200);
                        }
                        SMSHelper.sendMapLocationSMS(HomeActivity.this, "Your patient ", user.getName(),
                                user.getPersonalDoctorPhone(), latitude, longitude);

                    }
                    if (contacts != null) {
                        // send message to his friend

                        for (Contact contact : contacts) {

                            if (contact.activated) {
                                if (sharedPreferences.getBoolean("vibration", true)) {
                                    VibrationHelper.vibrate(this, 200);
                                }
                                SMSHelper.sendMapLocationSMS(HomeActivity.this, "Your friend ", user.getName(),
                                        contact.getPhoneNumber(), latitude, longitude);

                            }

                        }
                    } else {

                        Log.d("Contact", "Contacts list is empty");
                    }
                }
            }
            statusAnimation = !statusAnimation;
        });
        // Fetch doctors data
        db.collection("hospitals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Hospital hospital = document.toObject(Hospital.class);
                    hospitalList.add(hospital);
                }

            } else {
                Toast.makeText(this, "Error fetching doctors.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Contact> loadContactsList() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("contacts" + currentUser.getUid(), null);

        Type type = new TypeToken<List<Contact>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start listening for location updates
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening for location updates when the activity is destroyed
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    // refresh
    private void LoadUser() {
        if (currentUser != null) {
            user_email.setText(currentUser.getEmail());
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        // DocumentSnapshot contains the data of the user
                        // String myname = document.getString("name");
                        name.setText(user.getName());
                        username.setText(user.getName());

                    } else {
                        // Document does not exist
                        Toast.makeText(HomeActivity.this, "Document does not exist",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error occurred while fetching data
                    Toast.makeText(HomeActivity.this, "Error occurred while fetching data.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            // No user is currently signed in
            Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Animation
    private void startPulse() {
        runnable.run();
    }

    private void stopPulse() {
        handlerAnimation.removeCallbacks(runnable);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            imgAnimation1.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imgAnimation1.setScaleX(1f);
                            imgAnimation1.setScaleY(1f);
                            imgAnimation1.setAlpha(1f);
                        }
                    });

            imgAnimation2.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(700)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imgAnimation2.setScaleX(1f);
                            imgAnimation2.setScaleY(1f);
                            imgAnimation2.setAlpha(1f);
                        }
                    });

            handlerAnimation.postDelayed(this, 1500);
        }
    };

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Logout", (dialog, which) -> logout());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User cancelled logout, dismiss the dialog
            dialog.dismiss();
        });
        builder.show();
    }

    private void logout() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadUser();
    }
}
