package com.udacity.stockhawk.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;

/**
 * Created by carlinhos on 3/9/17.
 */

public class Stock implements Parcelable {

    private float mPrice;
    private float mAbsoluteChange;
    private float mPercentageChange;
    private ArrayList<History> mHistory;
    private String mName;

    public Stock(float mPrice, float mAbsoluteChange, float mPercentageChange, ArrayList<History> mHistory, String mName) {
        this.mPrice = mPrice;
        this.mAbsoluteChange = mAbsoluteChange;
        this.mPercentageChange = mPercentageChange;
        this.mHistory = mHistory;
        this.mName = mName;
    }

    public float getmPrice() {
        return mPrice;
    }

    public void setmPrice(float mPrice) {
        this.mPrice = mPrice;
    }

    public float getmAbsoluteChange() {
        return mAbsoluteChange;
    }

    public void setmAbsoluteChange(float mAbsoluteChange) {
        this.mAbsoluteChange = mAbsoluteChange;
    }

    public float getmPercentageChange() {
        return mPercentageChange;
    }

    public void setmPercentageChange(float mPercentageChange) {
        this.mPercentageChange = mPercentageChange;
    }

    public ArrayList<History> getmHistory() {
        return mHistory;
    }

    public void setmHistory(ArrayList<History> mHistory) {
        this.mHistory = mHistory;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mPrice);
        dest.writeFloat(this.mAbsoluteChange);
        dest.writeFloat(this.mPercentageChange);
        dest.writeTypedList(this.mHistory);
        dest.writeString(this.mName);
    }

    protected Stock(Parcel in) {
        this.mPrice = in.readFloat();
        this.mAbsoluteChange = in.readFloat();
        this.mPercentageChange = in.readFloat();
        this.mHistory = in.createTypedArrayList(History.CREATOR);
        this.mName = in.readString();
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

    public static Stock getStockForSymbol(Context context, String symbol) {

        String[] columns = Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{});

        Cursor c = context.getContentResolver().query(
                Contract.Quote.makeUriForStock(symbol),
                columns,
                null, null, null
        );

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        ArrayList<History> historyList = new ArrayList<>();

        float price = Float.parseFloat(c.getString(Contract.Quote.POSITION_PRICE));
        float absoluteChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
        float percentageChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE));
        String history = c.getString(Contract.Quote.POSITION_HISTORY);
        String name = c.getString(Contract.Quote.POSITION_NAME);

        String[] historyArray = history.split("\n");

        for (String h : historyArray) {

            String[] histDetail = h.split(",");

            String date = histDetail[0];
            float datePrice = Float.valueOf(histDetail[1]);
            float low = Float.valueOf(histDetail[2]);
            float high = Float.valueOf(histDetail[3]);

            historyList.add(new History(date, datePrice, low, high));

        }

        c.close();

        return new Stock(price, absoluteChange, percentageChange, historyList, name);

    }

}
