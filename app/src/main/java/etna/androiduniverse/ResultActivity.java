package etna.androiduniverse;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import etna.androiduniverse.adapters.PlaceAdapter;
import etna.androiduniverse.entities.PlaceInfo;
import etna.androiduniverse.interfaces.OnPlaceClickListener;
import etna.androiduniverse.presenters.ResultPresenter;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    private ResultPresenter resultPresenter;
    private RecyclerView mRecyclerView;
    private PlaceAdapter adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", null);

        resultPresenter = new ResultPresenter(this, type);
        getSupportActionBar().setTitle(type + " aux alentours");

        mRecyclerView = (RecyclerView) findViewById(R.id.places_list);

        adapter = new PlaceAdapter(new ArrayList<PlaceInfo>(), new OnPlaceClickListener() {
            @Override
            public void onPlaceClick(View v, PlaceInfo placeInfo) {
                getPresenter().saveResults();

                Intent intent = new Intent(v.getContext(), MapActivity.class);
                intent.putExtra("placeInfo", placeInfo);
                intent.putExtra("currentLocation", getPresenter().getLastLocationString());
                startActivity(intent);
            }
        });


        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button errorButton = (Button) findViewById(R.id.errorButton);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().showGPSDisabledAlertToUser();
            }
        });

        if (!getPresenter().isGPSEnabled()) {
            getPresenter().showGPSDisabledAlertToUser();
        }

        mRecyclerView.setVisibility(View.INVISIBLE);

        if (getPresenter().isGPSEnabled()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Chargement...");
            progressDialog.setMessage("Recherche des " + type + " à proximité");
            progressDialog.show();
        }


    }

    @Override
    protected void onStart() {
        getPresenter().onStart();
        super.onStart();
    }

    @Override
    protected void onStop() {
        getPresenter().onStop();
        super.onStop();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == -1) {
                showErrors(false);
                getPresenter().onStop();
                getPresenter().onStart();
            }
            else {
                showErrors(true);
            }
        }
    }

    public void showErrors(boolean show) {
        TextView errorText = (TextView) findViewById(R.id.errorText);
        Button errorButton = (Button) findViewById(R.id.errorButton);

        if (show) {
            mRecyclerView.setVisibility(View.GONE);
            errorText.setVisibility(View.VISIBLE);
            errorButton.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
            errorButton.setVisibility(View.GONE);
        }
    }

    public ResultPresenter getPresenter() {
        return resultPresenter;
    }

    public void updatePlacesList(List<PlaceInfo> placeList) {
        progressDialog.dismiss();
        mRecyclerView.setVisibility(View.VISIBLE);
        adapter.addAll(placeList);
    }
}
