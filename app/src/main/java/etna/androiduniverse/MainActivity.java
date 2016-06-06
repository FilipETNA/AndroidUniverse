package etna.androiduniverse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import etna.androiduniverse.adapters.TypeAdapter;
import etna.androiduniverse.decorations.MarginDecoration;
import etna.androiduniverse.entities.CommerceType;
import etna.androiduniverse.interfaces.OnCardClickListener;
import etna.androiduniverse.presenters.MainPresenter;

public class MainActivity extends AppCompatActivity {

    private MainPresenter mainPresenter;

    private RecyclerView mRecyclerView;
    private TypeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainPresenter = new MainPresenter(this);

        //Setting data
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new MarginDecoration(this));

        //Adapter
        adapter = new TypeAdapter(new ArrayList<CommerceType>(), new OnCardClickListener() {
            @Override
            public void onCardClick(View v, CommerceType commerceType) {

                Context context = v.getContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("type", commerceType.getLabel());
                editor.apply();

                Intent intent = new Intent(v.getContext(), ResultActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(adapter);

        //Layout Manager
        layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mainPresenter.setCommerceTypes();

    }

    public void updateCommerceList(ArrayList<CommerceType> commerceTypeList) {
        adapter.addAll(commerceTypeList);

    }
}
