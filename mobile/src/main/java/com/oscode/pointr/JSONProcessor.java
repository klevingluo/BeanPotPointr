package com.oscode.pointr;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONProcessor {
    private static ArrayList<Locals> locations;
    private static YelpAPI yelpAPI;

    public static ArrayList<Locals> getLocations() {
        update(false);
        return locations;
    }

    public static void update(boolean redownload) {//TODO: note, have YelpHandler check message code and pass in only JSONARRAY
        if (yelpAPI == null) {
            yelpAPI = new YelpAPI();
        }
        if (redownload || locations == null) {
            JSONArray data = null;
            try {
                yelpAPI.execute();
                while(yelpAPI.getData() == null){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                data = yelpAPI.getData().getJSONArray("businesses");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("IMPORTANT_TAG", e.getStackTrace().toString());
            }
            locations = new ArrayList<Locals>();
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject temp = data.getJSONObject(i);
                    if (!temp.getBoolean("is_closed")) {
                        Locals l = new Locals(temp);
                        locations.add(l);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //TODO: for loop with each locals updating its distance
        }
    }
}
