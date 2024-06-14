package com.example.saveme;

import com.example.saveme.models.PoliceStation;
import java.util.ArrayList;
import java.util.List;

public class PoliceStationData {
    public static List<PoliceStation> getPoliceStations() {
        List<PoliceStation> policeStations = new ArrayList<>();

        policeStations.add(new PoliceStation("1", "Central Police Station of Tunis", "+216 71 341 666", 36.8008, 10.1844));
        policeStations.add(new PoliceStation("2", "La Marsa Police Station", "+216 71 749 999", 36.8780, 10.3247));
        policeStations.add(new PoliceStation("3", "Sousse Police Station", "+216 73 222 333", 35.8256, 10.6360));
        policeStations.add(new PoliceStation("4", "Monastir Police Station", "+216 73 460 000", 35.7776, 10.8266));
        policeStations.add(new PoliceStation("5", "Sfax Police Station", "+216 74 201 111", 34.7406, 10.7600));
        policeStations.add(new PoliceStation("6", "Nabeul Police Station", "+216 72 280 000", 36.4489, 10.7376));
        policeStations.add(new PoliceStation("7", "Bizerte Police Station", "+216 72 420 000", 37.2744, 9.8739));
        policeStations.add(new PoliceStation("8", "Gafsa Police Station", "+216 76 220 000", 34.4250, 8.7842));
        policeStations.add(new PoliceStation("9", "Gabès Police Station", "+216 75 270 000", 33.8815, 10.0982));
        policeStations.add(new PoliceStation("10", "Kairouan Police Station", "+216 77 230 000", 35.6781, 10.0963));

        // Adding Police Stations in Sidi Bouzid
        policeStations.add(new PoliceStation("11", "Police Station - Sidi Bouzid Centre", "Not Available", 35.0365, 9.4844));
        policeStations.add(new PoliceStation("12", "National Guard - Sidi Bouzid", "Not Available", 35.0372, 9.4884));

        return policeStations;
    }
}
