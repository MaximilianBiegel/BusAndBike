package com.example.biegel.busandbike;

import java.util.Vector;

/**
 * Created by Biegel on 13.01.2017.
 */

public class BusStop {
    private String name;
    private Double latitude;
    private Double longitude;
    private Vector<String> buslines;
    private Vector<String> directions;
    private  int counter;


    public BusStop(String name, Double latitude, Double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.buslines = new Vector<String>();
        this.directions = new Vector<String>();
        this.counter = 0;
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

    public Vector<String> getDirections() {
        return directions;
    }

    public Vector<String> getBuslines() {
        return buslines;
    }

    public void addBuslines(String busline) {
        this.buslines.addElement(busline);
    }

    public void addDirections (String direction) {
        this.directions.addElement(direction);
    }

    public int getCounter(){
        return this.counter;
    }

    public void setCounter(int value){
        this.counter= value;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", counter=" +counter+
                '}';
    }
}
