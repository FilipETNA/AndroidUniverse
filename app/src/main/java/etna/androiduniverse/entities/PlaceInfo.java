package etna.androiduniverse.entities;

import java.io.Serializable;

public class PlaceInfo implements Serializable {

    private String name;
    private String phone;
    private String address;
    private String place_id;
    private String latitude;
    private String longitude;
    private String rating;
    private String photoReference;


    public PlaceInfo(String name,
                     String phone,
                     String address,
                     String place_id,
                     String latitude,
                     String longitude,
                     String rating,
                     String photo)
    {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.place_id = place_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.photoReference = photo;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return place_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRating() {
        return rating;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setPhoto(String photoReference) {
        this.photoReference = photoReference;
    }
}
