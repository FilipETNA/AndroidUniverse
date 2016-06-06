package etna.androiduniverse.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import etna.androiduniverse.R;
import etna.androiduniverse.entities.CommerceType;
import etna.androiduniverse.interfaces.OnCardClickListener;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder>{

    private List<CommerceType> commerceTypeList;
    private OnCardClickListener clickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public ImageView icon;
        public TextView label;

        public ViewHolder(View v) {
            super(v);

            card = (CardView) v.findViewById(R.id.card);
            icon = (ImageView) v.findViewById(R.id.card_icon);
            label = (TextView) v.findViewById(R.id.label);
        }

        public void bind(final CommerceType commerceType, final OnCardClickListener listener) {

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCardClick(v, commerceType);
                }
            });
        }

    }

    public void addAll(ArrayList<CommerceType> list) {
        commerceTypeList = list;
        notifyDataSetChanged();
    }

    public TypeAdapter(List<CommerceType> typeList, OnCardClickListener listener) {
        commerceTypeList = new ArrayList<>();
        commerceTypeList = typeList;
        clickListener = listener;
    }

    @Override
    public TypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CommerceType commerceType = commerceTypeList.get(position);

        ViewGroup.LayoutParams cardParams = holder.card.getLayoutParams();

        holder.label.setText(commerceType.getLabel().toUpperCase());
        holder.icon.setImageResource(commerceType.getIcon());
        holder.bind(commerceType, clickListener);

    }

    @Override
    public int getItemCount() {
        return commerceTypeList.size();
    }

}
