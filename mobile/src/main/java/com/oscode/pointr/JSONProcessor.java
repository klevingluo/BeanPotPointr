package com.oscode.pointr;

import org.json.JSONArray;

import java.util.ArrayList;

public class JSONProcessor {
    public static ArrayList<Locals> locations;
    public static YelpAPI yelpAPI;
    public static JSONArray data;

    public static ArrayList<String> exportLocations() {
        ArrayList<String> ret = new ArrayList<String>();
        for (Locals l : locations) {
            ret.add(l.toString());
        }
        return ret;
    }

    public static void update(String latitude, String longitude) {//TODO: note, have YelpHandler check message code and pass in only JSONARRAY
        if (yelpAPI == null) {
            yelpAPI = new YelpAPI(latitude, longitude);
        }
        if (locations == null) {
//            JSONArray data = null;
            yelpAPI.execute();
//            while(yelpAPI.getData() == null){
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//                data = yelpAPI.getData().getJSONArray("businesses");
            //            locations = new ArrayList<Locals>();
//            for (int i = 0; i < data.length(); i++) {
//                try {
//                    JSONObject temp = data.getJSONObject(i);
//                    if (!temp.getBoolean("is_closed")) {
//                        Locals l = new Locals(temp);
//                        locations.add(l);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        } else {
            //TODO: for loop with each locals updating its distance
        }
    }
}
