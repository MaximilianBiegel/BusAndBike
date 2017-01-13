package com.example.biegel.busandbike;

/**
 * Created by Biegel on 13.01.2017.
 */

public class BusStop {
    private String name;
    private Double latitude;
    private Double longitude;

    public BusStop(String name, Double latitude, Double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
