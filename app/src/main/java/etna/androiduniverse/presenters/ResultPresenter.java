package etna.androiduniverse.presenters;


import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import etna.androiduniverse.R;
import etna.androiduniverse.ResultActivity;
import etna.androiduniverse.entities.PlaceInfo;
import etna.androiduniverse.services.PlaceService;

public class ResultPresenter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = "ResultPresenter";

    private ResultActivity activity;
    private ArrayList<PlaceInfo> placesList;
    private String type;
    private GoogleApiClient googleApiClient;
    private Location mLastLocation;
    private LocationManager locationManager;

    public ResultPresenter(ResultActivity a, String query) {
        activity = a;
        placesList = new ArrayList<>();
        type = query;

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public void onStart() {
        loadResults();
        googleApiClient.connect();

        if (!isGPSEnabled()) {
            activity.showErrors(true);
        }
    }

    public void onStop() {
        googleApiClient.disconnect();
    }

    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (!isGPSEnabled()) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (mLastLocation != null) {
            if (placesList.size() == 0) {
                getNearbyPlaces();
            }
        }
        else  {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {

                        mLastLocation = location;
                        getNearbyPlaces();
                        try {
                            if (mLastLocation != null) {
                                locationManager.removeUpdates(this);
                            }
                        } catch (SecurityException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
            } catch (SecurityException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public String getLastLocationString() {
        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        return mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void showGPSDisabledAlertToUser() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity,
                                    1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void getNearbyPlaces() {

        PlaceService placeService = new PlaceService();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        try {
            placesList = placeService.getNearbyPlaces(getLastLocationString(), 1000, type);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        activity.updatePlacesList(placesList);
    }

    public void saveResults() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        editor.putInt("results_size", placesList.size());

        for (int i = 0; i < placesList.size(); i++) {
            editor.putString("result_" + i, gson.toJson(placesList.get(i)));
        }

        editor.apply();
    }

    public void loadResults() {

        SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getString(R.string.preference_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        if (sharedPreferences.getInt("results_size", 0) == 0) {
            return;
        }

        for (int i = 0; i < sharedPreferences.getInt("results_size", 0); i++) {
            PlaceInfo placeInfo = gson.fromJson(sharedPreferences.getString("result_" + i, null), PlaceInfo.class);
            placesList.add(placeInfo);
            editor.remove("result_" + i);
        }

        editor.remove("results_size");
        editor.apply();

        activity.updatePlacesList(placesList);
    }

}
