package com.udacity.stockhawk.model;

import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

/**
 * Created by carlinhos on 3/9/17.
 */

public class MyStock {

    private float price;
    private float absoluteChange;
    private float percentageChange;
    private HashMap<String, Float> history;
    private String name;

    public MyStock(float price, float absoluteChange, float percentageChange, HashMap history, String name) {
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

    public HashMap<String, Float> getHistory() {
        return history;
    }

    public void setHistory(HashMap history) {
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
