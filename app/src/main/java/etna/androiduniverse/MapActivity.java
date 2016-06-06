package etna.androiduniverse;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import etna.androiduniverse.components.ScrollableMapFragment;
import etna.androiduniverse.entities.PlaceInfo;
import etna.androiduniverse.presenters.MapPresenter;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private static final String TAG = "MapActivity";
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyB5syZcBt8NvDspWWm-0KsQ9hkRBxT_KYo&maxwidth=400&photoreference=";

    private ScrollView container;
    private MapPresenter mapPresenter;
    private LatLng currentPosition;
    private LatLng placePosition;
    private PlaceInfo placeInfo;
    private GoogleMap map;
    private String placeURL;
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FacebookSdk.sdkInitialize(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        placeInfo = (PlaceInfo) getIntent().getSerializableExtra("placeInfo");

        placePosition = new LatLng(Double.parseDouble(placeInfo.getLatitude()), Double.parseDouble(placeInfo.getLongitude()));

        getSupportActionBar().setTitle(placeInfo.getName());

        String currentLocation = getIntent().getExtras().getString("currentLocation");

        if (currentLocation != null) {
            String[] coord = currentLocation.split(",");
            currentPosition = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
        }

        container = (ScrollView) findViewById(R.id.sv_container);

        mapPresenter = new MapPresenter(this, placeInfo.getPlaceId());

        ScrollableMapFragment mapFragment = (ScrollableMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mapFragment.setListener(new ScrollableMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                container.requestDisallowInterceptTouchEvent(true);
            }
        });

        shareDialog = new ShareDialog(this);

        getPresenter().getPlaceDetail();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        CameraPosition camera = new CameraPosition.Builder()
                .target(placePosition)
                .zoom(17)
                .build();

        map.addMarker(new MarkerOptions().position(currentPosition).title("Ma position"));
        map.addMarker(new MarkerOptions().position(placePosition).title(placeInfo.getName()));
        map.moveCamera(CameraUpdateFactory.newCameraPosition(camera));

        Routing routing = new Routing.Builder()
                .key(getString(R.string.api_key))
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(currentPosition, placePosition)
                .build();

        routing.execute();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int i) {
        Route route = routes.get(0);

        TextView distance = (TextView) findViewById(R.id.place_distance);
        distance.setText(route.getDistanceText());

        PolylineOptions polyline = route.getPolyOptions();
        polyline.color(Color.parseColor("#c71547"));

        map.addPolyline(polyline);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        Log.d(TAG, "Routing start");
    }

    @Override
    public void onRoutingCancelled() {
    }

    private String ucfirst(String chaine) {
        return chaine.substring(0, 1).toUpperCase() + chaine.substring(1).toLowerCase();
    }

    public void setDetailPlace(String response) throws JSONException {
        JSONObject detail_object = new JSONObject(response);
        JSONObject detail = detail_object.getJSONObject("result");

        TextView name = (TextView) findViewById(R.id.detail_place_name);
        TextView address = (TextView) findViewById(R.id.detail_place_address);
        ImageView img = (ImageView) findViewById(R.id.detail_place_picture);
        TextView phone = (TextView) findViewById(R.id.detail_place_phone);
        TextView rating = (TextView) findViewById(R.id.detail_place_rating);

        placeURL = detail.getString("url");
        name.setText(detail.getString("name"));
        address.setText(detail.getString("vicinity"));
        phone.setText(detail.getString("formatted_phone_number"));
        rating.setText(detail.getString("rating") + " / 5");

        Picasso.with(this)
                .load(PHOTO_BASE_URL + placeInfo.getPhotoReference())
                .error(R.drawable.image_non_disponible)
                .into(img);
    }

    public MapPresenter getPresenter() {
        return mapPresenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.share) {
            showShareDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showShareDialog() {
        final String apps[] = {"Facebook", "Twitter", "Gmail"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Partager sur ");
        builder.setItems(apps, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {

                String app = apps[position].toLowerCase();

                if (app.equals("facebook")) {
                    shareOnFacebook();
                }
                else if (app.equals("twitter")) {
                    shareOnTwitter();
                }
                else {
                    shareOnGmail();
                }


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void shareOnFacebook() {
        ShareLinkContent shareLink =
                new ShareLinkContent.Builder()
                        .setContentTitle(placeInfo.getName())
                        .setContentUrl(Uri.parse(placeURL))
                        .build();

        shareDialog.show(shareLink);
    }


    public void shareOnTwitter() {
        String application = "com.twitter.android";

        Intent intent = getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(application);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, placeInfo.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "est allé à #" + placeInfo.getName().replace(" ", "") + " - Partagé via AndroidUniverse");

            startActivity(shareIntent);
        }
        else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + application)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + application)));
            }
        }
    }

    public void shareOnGmail() {
        String application = "com.google.android.gm";

        Intent intent = getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(application);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Partage AndroidUniverse");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Je te recommande ce lieu pour passer un bon moment ! :) " +
                    placeInfo.getName() + " " + placeURL);

            startActivity(shareIntent);
        }
        else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + application)));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + application)));
            }
        }
    }
}
