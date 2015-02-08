package com.oscode.pointr;

import org.json.JSONException;
import org.json.JSONObject;

public class Locals {
    private String name;
    private double rating;
    private double latitude;
    private double longitude;
    private String description;

    public Locals(JSONObject obj) {
        try {
            this.name = obj.getString("name");
            this.rating = obj.getDouble("rating");
            this.description = obj.getString("snippet_text");
            this.latitude = obj.getJSONObject("location").getJSONObject("coordinate").getDouble("latitude");
            this.longitude = obj.getJSONObject("location").getJSONObject("coordinate").getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name + "\t" + rating + "\t" + latitude + "\t" + longitude + "\t" + description;
    }
}
