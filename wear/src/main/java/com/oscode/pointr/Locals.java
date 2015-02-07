package com.oscode.pointr;

import org.json.JSONException;
import org.json.JSONObject;

public class Locals {
    private String name;
    private double rating;
    private double distance;
    private double latitude;
    private double longitude;
    private String description;
    private double degrees;

    public Locals(JSONObject obj) {
        try {
            this.name = obj.getString("name");
            this.rating = obj.getDouble("avg_rating");
            this.distance = obj.getDouble("distance");
            this.description = obj.getString("snippet_text");
            this.latitude = obj.getJSONObject("region").getJSONObject("center").getDouble("latitude");
            this.longitude = obj.getJSONObject("region").getJSONObject("center").getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateDistance(double myLat, double myLong) {
        double R = 6371000;
        double lat1 = Math.toRadians(myLat);
        double lat2 = Math.toRadians(latitude);
        double deltLat = Math.toRadians(latitude - myLat);
        double deltLong = Math.toRadians(longitude - myLong);
        double a = (Math.sin(deltLat / 2) * Math.sin(deltLat / 2)) + (Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltLong / 2) * Math.sin(deltLong / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c * 0.62137;//in miles
        distance = d;
        degrees = Math.atan((latitude - myLat) / (longitude - myLong));

    }
}
