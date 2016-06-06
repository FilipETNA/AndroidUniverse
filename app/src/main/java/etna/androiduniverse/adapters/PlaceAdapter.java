package etna.androiduniverse.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import etna.androiduniverse.R;
import etna.androiduniverse.entities.PlaceInfo;
import etna.androiduniverse.interfaces.OnPlaceClickListener;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<PlaceInfo> placesList;
    private OnPlaceClickListener listener;
    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?key=AIzaSyB5syZcBt8NvDspWWm-0KsQ9hkRBxT_KYo&maxwidth=400&photoreference=";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView card;
        public ImageView img;
        public TextView name;
        public TextView address;
        public TextView rating;

        public Context context;

        public ViewHolder(View v) {
            super(v);

            card = (CardView) v.findViewById(R.id.list_place_card);
            img = (ImageView) v.findViewById(R.id.card_img);
            name = (TextView) v.findViewById(R.id.place_name);
            address = (TextView) v.findViewById(R.id.address);
            rating = (TextView) v.findViewById(R.id.rating);

            context = v.getContext();
        }

        public void bind(final PlaceInfo placeInfo, final OnPlaceClickListener listener) {

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPlaceClick(v, placeInfo);
                }
            });
        }

    }

    public void addAll(List<PlaceInfo> placesList) {
        this.placesList = placesList;
        notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter(List<PlaceInfo> placesList, OnPlaceClickListener listener) {
        this.placesList = new ArrayList<>();
        this.placesList = placesList;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        PlaceInfo placeInfo = placesList.get(position);
        holder.bind(placeInfo, listener);


        if (placeInfo.getPhotoReference() == null) {
            holder.img.setImageResource(R.drawable.image_non_disponible);
        }
        else {
            Picasso.with(holder.context).load(PHOTO_BASE_URL + placeInfo.getPhotoReference()).into(holder.img);
        }

        holder.name.setText(placeInfo.getName());
        holder.address.setText(placeInfo.getAddress());
        holder.rating.setText(placeInfo.getRating());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placesList.size();
    }

}
