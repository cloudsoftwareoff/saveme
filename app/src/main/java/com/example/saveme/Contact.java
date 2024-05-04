package com.example.saveme;

public class Contact {
    private String name;
    boolean activated;
    private String phoneNumber;

    public Contact(String name, String phoneNumber,boolean activated) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.activated=activated;
    }



    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public  boolean getActivated(){return  activated;}
}
