package etna.androiduniverse.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import etna.androiduniverse.entities.PlaceInfo;
import etna.androiduniverse.utils.HttpClient;

public class PlaceService {


    private static final String TAG = "PlaceService";
    private static final String API_KEY = "AIzaSyB5syZcBt8NvDspWWm-0KsQ9hkRBxT_KYo";
    private static final String URL_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String URL_DETAIL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private HttpClient client_search;
    private HashMap<String, String> types;


    public PlaceService(){
        this.client_search = new HttpClient(URL_SEARCH);

        types = new HashMap<>();
        types.put("Boulangeries", "bakery");
        types.put("Hôtels", "lodging");
        types.put("Parcs", "park");
        types.put("Bars", "bar");
        types.put("Cinémas", "movie_theater");
        types.put("Restaurants", "restaurant");
    }

    public ArrayList<PlaceInfo> getNearbyPlaces(String location, int radius, String type) throws IOException{

        ArrayList<PlaceInfo> places = new ArrayList<>();
        String response = null;
        String urlParams = "key=" + API_KEY;

        urlParams += "&location=" + location;
        urlParams += "&radius=" + radius;
        urlParams += "&types=" + types.get(type);

        try {
            response = client_search.execute("GET", urlParams).get();
            places = this.jsonToPlaceInfoList(response);
            return places;
        } catch (InterruptedException | ExecutionException | JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return places;
    }

    public String getPlaceDetail(String placeId){

        String response = null;
        String urlParams = "key=" + API_KEY;

        urlParams += "&placeid=" + placeId;

        try {
            response = new HttpClient(URL_DETAIL).execute("GET", urlParams).get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }

        return response;
    }

    private ArrayList<PlaceInfo> jsonToPlaceInfoList(String json) throws JSONException {
        ArrayList<PlaceInfo> places = new ArrayList<>();
        JSONObject response = new JSONObject(json);
        JSONArray results = response.getJSONArray("results");

        if(results == null)
        {
            System.out.println("RESPONSE "+response);
            return null;
        }

        for(int i = 0; i < results.length(); i++) {
            JSONObject place = results.getJSONObject(i);

            PlaceInfo placeInfo = new PlaceInfo(
                    place.getString("name"),
                    place.optString("formatted_phone_number", "no phone"),
                    place.getString("vicinity"),
                    place.getString("place_id"),
                    place.getJSONObject("geometry").getJSONObject("location").getString("lat"),
                    place.getJSONObject("geometry").getJSONObject("location").getString("lng"),
                    place.optString("rating", "N/A"),
                    null
            );

            if (place.has("photos")) {
                String photoReference = place.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                placeInfo.setPhoto(photoReference);
            }

            places.add(placeInfo);
        }

        return places;
    }
}
