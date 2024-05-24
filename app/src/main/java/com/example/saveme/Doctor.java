package com.example.saveme;

public class Doctor {
    private String id;
    private String name;
    private String phone;
    private Double latitude;
    private Double longitude;


    public Doctor() {}

    public Doctor(String id, String name, String phone, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

