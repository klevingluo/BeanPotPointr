package com.oscode.pointr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        double deltLat = latitude - myLat;
        double deltLong = longitude - myLong;
        distance = Math.sqrt(deltLat*deltLat + deltLong*deltLong);
        degrees = (deltLong > 0 ? Math.atan(deltLat/deltLong) : Math.PI - Math.atan(deltLat/deltLong))%(2*Math.PI);
    }
}
