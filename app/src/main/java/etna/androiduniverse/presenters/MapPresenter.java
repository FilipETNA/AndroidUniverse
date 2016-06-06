package etna.androiduniverse.presenters;

import android.util.Log;

import org.json.JSONException;

import etna.androiduniverse.MapActivity;
import etna.androiduniverse.services.PlaceService;

public class MapPresenter  {

    private static final String TAG = "MapPresenter";

    private MapActivity activity;
    private String placeId;

    public MapPresenter(MapActivity a, String query) {
        activity = a;
        placeId = query;
    }

    public void getPlaceDetail() {

        PlaceService placeService = new PlaceService();

        String response = placeService.getPlaceDetail(placeId);
        try {
            activity.setDetailPlace(response);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}