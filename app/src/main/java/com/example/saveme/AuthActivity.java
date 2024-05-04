package com.example.saveme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    private HashMap<String, Object> userData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.auth_screen);
        Button signup_btn = findViewById(R.id.signup);
        Button login_btn = findViewById(R.id.login);
        EditText email = findViewById(R.id.emailEdit);
        EditText password = findViewById(R.id.password_edit);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Login
        login_btn.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || !email.getText().toString().contains("@")) {
                email.setError("Enter Valid email");
                return;
            }
            if (password.getText().toString().length() < 8) {
                password.setError("Password at least 8 character");
                return;
            }
            ProgressDialog progressDialog = ProgressDialog.show(AuthActivity.this, "", "Signing in...", true);

            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {

                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // success
                            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }).addOnFailureListener(this, task -> {
                        Toast.makeText(AuthActivity.this, task.toString(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        // Sign Up
        signup_btn.setOnClickListener(v -> {
            // Show loading dialog
            ProgressDialog progressDialog = ProgressDialog.show(AuthActivity.this, "", "Signing up...", true);

            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, task -> {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Sign up success
                            FirebaseUser user = mAuth.getCurrentUser();
                            userData = new HashMap<>();
                            Calendar calendar = Calendar.getInstance();
                            userData.put("id",
                                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            userData.put("joined", String.valueOf(calendar.getTimeInMillis()));
                            ;
                            String[] parts = user.getEmail().split("@");
                            String username = parts[0];

                            userData.put("name", username);
                            userData.put("tel", "");
                            userData.put("address", "");
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AuthActivity.this, "Failed to add user data to Firestore.",
                                                Toast.LENGTH_SHORT).show();
                                    });
                            progressDialog.dismiss();

                        } else {
                            // Sign up failed
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    });

        });

    }
}