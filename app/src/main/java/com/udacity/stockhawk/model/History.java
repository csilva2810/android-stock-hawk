package com.udacity.stockhawk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class History implements Parcelable {

    private String mDate;
    private float mPrice;
    private float mLow;
    private float mHigh;

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public float getmPrice() {
        return mPrice;
    }

    public void setmPrice(float mPrice) {
        this.mPrice = mPrice;
    }

    public float getmLow() {
        return mLow;
    }

    public void setmLow(float mLow) {
        this.mLow = mLow;
    }

    public float getmHigh() {
        return mHigh;
    }

    public void setmHigh(float mHigh) {
        this.mHigh = mHigh;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDate);
        dest.writeFloat(this.mPrice);
        dest.writeFloat(this.mLow);
        dest.writeFloat(this.mHigh);
    }

    public History(String mDate, float mPrice, float mLow, float mHigh) {
        this.mDate = mDate;
        this.mPrice = mPrice;
        this.mLow = mLow;
        this.mHigh = mHigh;
    }

    protected History(Parcel in) {
        this.mDate = in.readString();
        this.mPrice = in.readFloat();
        this.mLow = in.readFloat();
        this.mHigh = in.readFloat();
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
