package com.example.saveme.models;

public class User {
    private String id;
    private String joined;
    private String name;
    private String tel;
    private String address;
    private boolean diabetes;
    private boolean heartProblem;
    private String personalDoctorPhone;
    private Double latitude;
    private Double longitude;
    private Boolean streamMyLocation;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String id, String joined, String name, String tel, String address, boolean diabetes, boolean heartProblem, String personalDoctorPhone, Double latitude, Double longitude, Boolean streamMyLocation) {
        this.id = id;
        this.joined = joined;
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.diabetes = diabetes;
        this.heartProblem = heartProblem;
        this.personalDoctorPhone = personalDoctorPhone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.streamMyLocation = streamMyLocation;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean isHeartProblem() {
        return heartProblem;
    }

    public void setHeartProblem(boolean heartProblem) {
        this.heartProblem = heartProblem;
    }

    public String getPersonalDoctorPhone() {
        return personalDoctorPhone;
    }

    public void setPersonalDoctorPhone(String personalDoctorPhone) {
        this.personalDoctorPhone = personalDoctorPhone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getStreamMyLocation() {
        return streamMyLocation;
    }

    public void setStreamMyLocation(Boolean streamMyLocation) {
        this.streamMyLocation = streamMyLocation;
    }
}
