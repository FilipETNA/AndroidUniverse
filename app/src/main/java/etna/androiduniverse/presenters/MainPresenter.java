package etna.androiduniverse.presenters;


import java.util.ArrayList;

import etna.androiduniverse.MainActivity;
import etna.androiduniverse.R;
import etna.androiduniverse.entities.CommerceType;

public class MainPresenter {

    private MainActivity activity;
    private ArrayList<CommerceType> commerceTypes;


    public MainPresenter(MainActivity a) {
        activity = a;
        commerceTypes = new ArrayList<>();

        getCommerceTypes();
    }

    private void getCommerceTypes() {
        commerceTypes.add(new CommerceType("Restaurants", R.drawable.ic_restaurant));
        commerceTypes.add(new CommerceType("Boulangeries", R.drawable.ic_bread));
        commerceTypes.add(new CommerceType("Cinémas", R.drawable.img_cinema));
        commerceTypes.add(new CommerceType("Bars", R.drawable.ic_bar));
        commerceTypes.add(new CommerceType("Parcs", R.drawable.ic_park));
        commerceTypes.add(new CommerceType("Hôtels", R.drawable.ic_hotel));
    }


    public void setCommerceTypes() {
        activity.updateCommerceList(commerceTypes);
    }

}
