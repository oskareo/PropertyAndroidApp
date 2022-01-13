package com.example.madpropertypal.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class LocationModel implements Serializable, Parcelable {
    private double lat;
    private double lng;
    private String address;
    private String city;
    private String country;

    public LocationModel() {
    }

    public LocationModel(double lat, double lng, String address, String city, String country) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    protected LocationModel(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        city = in.readString();
        country = in.readString();
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(address);
        parcel.writeString(city);
        parcel.writeString(country);
    }
}
