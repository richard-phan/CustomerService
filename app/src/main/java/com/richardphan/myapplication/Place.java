package com.richardphan.myapplication;

public class Place {
    private String name;
    private String location;

    public Place(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
