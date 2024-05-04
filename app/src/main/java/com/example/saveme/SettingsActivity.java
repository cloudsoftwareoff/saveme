package com.example.saveme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private CheckBox vibrationCheckbox;
    private CheckBox soundCheckbox;
    private CheckBox streamCheckbox;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Initialize
        vibrationCheckbox = findViewById(R.id.vibration_checkbox);
        soundCheckbox = findViewById(R.id.sound_checkbox);
        streamCheckbox = findViewById(R.id.stream_checkbox);
        ImageView go_back=findViewById(R.id.goback);


        vibrationCheckbox.setChecked(sharedPreferences.getBoolean("vibration", true));
        soundCheckbox.setChecked(sharedPreferences.getBoolean("sound", true));
        streamCheckbox.setChecked(sharedPreferences.getBoolean("stream", true));


        vibrationCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibration", isChecked);
            editor.apply();
        });

        soundCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("sound", isChecked);
            editor.apply();
        });

        streamCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("stream", isChecked);
            editor.apply();
        });

        go_back.setOnClickListener(v -> {
            finish();
        });
    }
}