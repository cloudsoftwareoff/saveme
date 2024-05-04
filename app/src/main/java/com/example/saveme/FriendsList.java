package com.example.saveme;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FriendsList extends AppCompatActivity {
    ListView listView;
    List<Contact> contacts;
    ContactAdapter adapter;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends_list);
        ImageView add_icon = findViewById(R.id.add_friend);
        ImageView go_back = findViewById(R.id.goback);
        listView = findViewById(R.id.friends);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Load existing contacts or create a new list if not exists
        loadContactsList();
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        saveContactsList(contacts);
        adapter = new ContactAdapter(this, contacts);

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        go_back.setOnClickListener(v -> {
            finish();
        });
        add_icon.setOnClickListener(v -> {
            showInputDialog();
        });

        List<Contact> finalContacts = contacts;

    }

    private void saveContactsList(List<Contact> contacts) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contacts")
                .child(currentUser.getUid());

        for (Contact contact : contacts) {
            String key = databaseReference.push().getKey();
            if (contact.getPhoneNumber() != null) {
                databaseReference.child(contact.getPhoneNumber()).setValue(contact);
            }

        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        editor.putString("contacts" + currentUser.getUid(), json);
        editor.apply();
    }

    private void loadContactsList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contacts")
                .child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String number = snapshot.child("phoneNumber").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    Boolean isOn = snapshot.child("activated").getValue(Boolean.class);

                    contacts.add(new Contact(name, number, isOn != null ? isOn : false));

                }
                saveContactsList(contacts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextNumber = dialogView.findViewById(R.id.editTextNumber);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = editTextName.getText().toString();
            String number = editTextNumber.getText().toString();
            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(FriendsList.this, "All fields are required",
                        Toast.LENGTH_SHORT).show();
                return;

            }
            contacts.add(new Contact(name, number, true));

            // Notify the adapter
            adapter.notifyDataSetChanged();
            saveContactsList(contacts);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class ContactAdapter extends ArrayAdapter<Contact> {
        public ContactAdapter(Context context, List<Contact> contacts) {
            super(context, 0, contacts);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.user, parent, false);
            }

            Contact currentContact = getItem(position);

            final TextView nameTextView = listItemView.findViewById(R.id.name);
            final TextView phoneNumberTextView = listItemView.findViewById(R.id.user_id);
            final CheckBox isActive = listItemView.findViewById(R.id.activation_box);
            final ImageView remove = listItemView.findViewById(R.id.remove);
            ImageView call = listItemView.findViewById(R.id.call);

            isActive.setChecked(currentContact.getActivated());
            // Change activation
            isActive.setOnClickListener(v -> {
                currentContact.activated = !currentContact.activated;
                contacts.set(position, currentContact);
                saveContactsList(contacts);
                adapter.notifyDataSetChanged();
            });

            // call a contact
            call.setOnClickListener(v -> {
                CallHelper.makePhoneCall(FriendsList.this, currentContact.getPhoneNumber());
            });

            nameTextView.setText(currentContact.getName());

            phoneNumberTextView.setText(currentContact.getPhoneNumber());

            // Delete contact
            remove.setOnClickListener(v -> {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contacts")
                        .child(currentUser.getUid());
                databaseReference.child(currentContact.getPhoneNumber()).removeValue();

                contacts.remove(currentContact);
                saveContactsList(contacts);
                adapter.notifyDataSetChanged();
            });

            return listItemView;
        }
    }

}