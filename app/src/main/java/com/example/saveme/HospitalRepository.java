package com.example.saveme;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HospitalRepository {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference hospitalRef = db.collection("hospitals");

    public void fetchHospitals(final HospitalCallback callback) {
        hospitalRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Hospital> hospitalList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Hospital hospital = document.toObject(Hospital.class);
                        hospitalList.add(hospital);
                    }
                    callback.onCallback(hospitalList);
                } else {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public interface HospitalCallback {
        void onCallback(List<Hospital> hospitalList);
        void onFailure(Exception e);
    }
}
