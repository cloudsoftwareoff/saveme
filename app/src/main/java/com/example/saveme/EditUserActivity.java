package com.example.saveme;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saveme.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);

        // Initialize UI components
        ImageView goBack = findViewById(R.id.go_back);
        ImageView copy = findViewById(R.id.copy);
        TextView userId = findViewById(R.id.user_id);
        EditText userName = findViewById(R.id.name_edit);
        EditText userPhone = findViewById(R.id.phone_edit);
        EditText userAddress = findViewById(R.id.address_edit);
        CheckBox diabeteBox = findViewById(R.id.diabete_box);
        CheckBox heartBox = findViewById(R.id.heart_box);
        EditText personalDoctor = findViewById(R.id.personal_doctor_edit);
        TextView error = findViewById(R.id.error);
        Button saveButton = findViewById(R.id.submit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating user data...");
        progressDialog.setCancelable(false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId.setText(currentUser.getUid());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        if (user != null) {
                            userName.setText(user.getName());
                            userPhone.setText(user.getTel());
                            userAddress.setText(user.getAddress());
                            diabeteBox.setChecked(user.isDiabetes());
                            heartBox.setChecked(user.isHeartProblem());
                            personalDoctor.setText(user.getPersonalDoctorPhone());
                        } else {
                            Toast.makeText(EditUserActivity.this, "User data is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditUserActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditUserActivity.this, "Error occurred while fetching data.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(failure -> {
                error.setText(failure.toString());
            });
        } else {
            Intent intent = new Intent(EditUserActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        heartBox.setOnClickListener(event -> user.setHeartProblem(heartBox.isChecked()));
        diabeteBox.setOnClickListener(event -> user.setDiabetes(diabeteBox.isChecked()));

        goBack.setOnClickListener(v -> finish());
        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("Copied Text", userId.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "ID copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Clipboard service not available", Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(v -> {
            hideKeyboard();
            String newName = userName.getText().toString().trim();
            String newPhone = userPhone.getText().toString().trim();
            String newAddress = userAddress.getText().toString().trim();
            String newPersonalDoctor = personalDoctor.getText().toString().trim();

            if ((heartBox.isChecked() || diabeteBox.isChecked()) && newPersonalDoctor.isEmpty()) {
                personalDoctor.setError("Field required");
                return;
            }

            if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
                Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            } else {
                progressDialog.show();
                user.setName(newName);
                user.setTel(newPhone);
                user.setAddress(newAddress);
                user.setPersonalDoctorPhone(newPersonalDoctor);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(currentUser.getUid());

                userRef.set(user)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditUserActivity.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditUserActivity.this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
