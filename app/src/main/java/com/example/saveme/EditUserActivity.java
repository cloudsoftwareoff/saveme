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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);

        TextView userId = findViewById(R.id.user_id);
        EditText username = findViewById(R.id.name_edit);
        EditText phone = findViewById(R.id.phone_edit);
        EditText address = findViewById(R.id.address_edit);
        Button submit = findViewById(R.id.submit);
        ImageView go_back = findViewById(R.id.go_back);
        ImageView copy = findViewById(R.id.copy);

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
                        // DocumentSnapshot contains the data of the user
                        String myname = document.getString("name");
                        username.setText(myname);
                        phone.setText(document.getString("tel"));
                        address.setText(document.getString("address"));
                    } else {
                        // Document does not exist
                        Toast.makeText(EditUserActivity.this, "Document does not exist",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error occurred while fetching data
                    Toast.makeText(EditUserActivity.this, "Error occurred while fetching data.",
                            Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            // No user is currently signed in
            Intent intent = new Intent(EditUserActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }

        // go_back
        go_back.setOnClickListener(v -> {
            finish();
        });
        // copy
        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {

                ClipData clip = ClipData.newPlainText("Copied Text", userId.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "id copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                // Show a toast message indicating failure
                Toast.makeText(this, "Clipboard service not available", Toast.LENGTH_SHORT).show();
            }
        });
        // submit
        submit.setOnClickListener(v -> {
            hideKeyboard();
            String newName = username.getText().toString().trim();
            String newPhone = phone.getText().toString().trim();
            String newAddress = address.getText().toString().trim();

            // Validate input fields
            if (newName.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
                Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update user data in Firestore
                progressDialog.show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(currentUser.getUid());

                userRef.update("name", newName,
                        "tel", newPhone,
                        "address", newAddress)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditUserActivity.this, "User data updated successfully", Toast.LENGTH_SHORT)
                                    .show();
                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditUserActivity.this, "Failed to update user data", Toast.LENGTH_SHORT)
                                    .show();
                        });
            }
        });

    }

    private void hideKeyboard() {
        // Check if no view has focus
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}