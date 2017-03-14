package com.udacity.stockhawk.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by carlinhos on 3/9/17.
 */

public class Stock implements Parcelable {

    private float price;
    private float absoluteChange;
    private float percentageChange;
    private ArrayList<History> history;
    private String name;

    public Stock(float price, float absoluteChange, float percentageChange, ArrayList<History> history, String name) {
        this.price = price;
        this.absoluteChange = absoluteChange;
        this.percentageChange = percentageChange;
        this.history = history;
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getAbsoluteChange() {
        return absoluteChange;
    }

    public void setAbsoluteChange(float absoluteChange) {
        this.absoluteChange = absoluteChange;
    }

    public float getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(float percentageChange) {
        this.percentageChange = percentageChange;
    }

    public ArrayList<History> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<History> history) {
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.price);
        dest.writeFloat(this.absoluteChange);
        dest.writeFloat(this.percentageChange);
        dest.writeTypedList(this.history);
        dest.writeString(this.name);
    }

    protected Stock(Parcel in) {
        this.price = in.readFloat();
        this.absoluteChange = in.readFloat();
        this.percentageChange = in.readFloat();
        this.history = in.createTypedArrayList(History.CREATOR);
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Stock> CREATOR = new Parcelable.Creator<Stock>() {
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }

        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }
    };
}
