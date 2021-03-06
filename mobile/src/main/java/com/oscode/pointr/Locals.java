package com.oscode.pointr;

import org.json.JSONArray;
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
            if (obj.has("name")) this.name = obj.getString("name");
            if (obj.has("rating")) this.rating = obj.getDouble("rating");
            if(obj.has("categories")){
                JSONArray temp = obj.getJSONArray("categories");
                if(temp.length()==0){
                    this.description = "";
                }else if(temp.length()==1){
                    this.description = temp.getJSONArray(0).getString(0);
                }else{
                    this.description = temp.getJSONArray(0).getString(0) + ", " + temp.getJSONArray(1).getString(0);
                }
            }else{
                this.description = "";
            }
            if (obj.has("snippet_text")) this.description = this.description.length()==0 ? obj.getString("snippet text") : this.description + " - " + obj.getString("snippet_text");
            if (obj.has("location"))
                this.latitude = obj.getJSONObject("location").getJSONObject("coordinate").getDouble("latitude");
            if (obj.has("location"))
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
