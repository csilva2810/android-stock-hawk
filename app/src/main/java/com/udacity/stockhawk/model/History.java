package com.udacity.stockhawk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable {

    private String date;
    private float price;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeFloat(this.price);
    }

    public History(String date, float price) {
        this.date = date;
        this.price = price;
    }

    protected History(Parcel in) {
        this.date = in.readString();
        this.price = in.readFloat();
    }

    public static final Parcelable.Creator<History> CREATOR = new Parcelable.Creator<History>() {
        @Override
        public History createFromParcel(Parcel source) {
            return new History(source);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

}
