package com.example.saveme;

import com.example.saveme.models.Doctor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DoctorGenerator {
    private static Random random = new Random();

    private static List<String> firstNames = Arrays.asList(
            "Mohammed", "Maha", "Ali", "Youssef", "Fatma", "Ibrahim", "Khaled", "Marwan", "Ayman", "Imen",
            "Amine", "Sofiane", "Nadia", "Leila", "Hana", "Omar", "Hedi", "Salma", "Rim", "Zied"
    );

    private static List<String> lastNames = Arrays.asList(
            "Zuhairi", "Mejri", "Sayed", "Hamidi", "Saleh", "Balkhi", "Amiri", "Sharif", "Mahdi", "Hashimi",
            "Ben Ali", "Chaouch", "Ghannouchi", "Jaziri", "Khemiri", "Mansouri", "Nasri", "Ouertani", "Riahi", "Trabelsi"
    );

    public static List<Doctor> generateDoctors(int count) {
        List<Doctor> doctors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String lastName = lastNames.get(random.nextInt(lastNames.size()));
            String fullName = firstName + " " + lastName;
            String phone = "+216 7" + String.format("%01d %03d %03d", random.nextInt(10), random.nextInt(1000), random.nextInt(1000));
            double latitude = 33.0 + (37.0 - 33.0) * random.nextDouble();
            double longitude = 7.0 + (12.0 - 7.0) * random.nextDouble();

            doctors.add(new Doctor(
                    generateRandomId(),
                    fullName,
                    phone,
                    latitude,
                    longitude
            ));
        }
        return doctors;
    }

    private static String generateRandomId() {
        return Long.toString(Math.abs(random.nextLong()), 36);
    }
}

