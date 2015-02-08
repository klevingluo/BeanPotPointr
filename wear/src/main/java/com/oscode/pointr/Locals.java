package com.oscode.pointr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Locals {
    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public double getDistance() {
        return distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    private String name;
    private float rating;
    private double distance;
    private double latitude;
    private double longitude;
    private String description;
    private float degrees;

    public Locals(JSONObject obj) {
        try {
            this.name = obj.getString("name");
            this.rating = (float) obj.getDouble("avg_rating");
            this.distance = obj.getDouble("distance");
            this.description = obj.getString("snippet_text");
            this.latitude = obj.getJSONObject("region").getJSONObject("center").getDouble("latitude");
            this.longitude = obj.getJSONObject("region").getJSONObject("center").getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Locals(String name, float rating, double latitude, double longitude, String description) {
        this.name = name;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public void updateDistance(double myLat, double myLong) {
        double R = 6371000;
        double lat1 = Math.toRadians(myLat);
        double lat2 = Math.toRadians(latitude);
        double deltLat = Math.toRadians(latitude - myLat);
        double deltLong = Math.toRadians(longitude - myLong);
        double a = (Math.sin(deltLat / 2) * Math.sin(deltLat / 2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltLong / 2) * Math.sin(deltLong / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        distance = d;
        degrees = (float) ((deltLong < 0 ? Math.atan(deltLat/deltLong) : Math.PI + Math.atan(deltLat/deltLong))%(2*Math.PI));
        degrees *= 180/Math.PI;
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    @Override
    public String toString() {
        return name + "\n" + rating + "\n" + distance + "\n" + latitude + "\n" + longitude + "\n" + description + "\n" + degrees;
    }
}
