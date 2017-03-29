package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.udacity.stockhawk.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public final class PrefUtils {

    public static SharedPreferences getAppSharedPreferences(Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.app_shared_preferences_key),
                Context.MODE_PRIVATE
        );
    }

    public static Set<String> initializeStocks(Context context) {

        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);

        String[] defaultStocksList = context
                .getResources().getStringArray(R.array.default_stocks);

        Set<String> stocks = new HashSet<>(Arrays.asList(defaultStocksList));

        SharedPreferences prefs = getAppSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(initializedKey, true);
        editor.putStringSet(stocksKey, stocks);
        editor.apply();

        return stocks;

    }

    public static Set<String> getStocks(Context context) {

        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);

        SharedPreferences prefs = getAppSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            return initializeStocks(context);
        } else {
            return prefs.getStringSet(stocksKey, new HashSet<String>());
        }

    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        Timber.d("editStockPref: " + symbol + " " + add);

        String stocksKey = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);
        Set<String> stocksCopy = new HashSet<>(stocks);

        if (add) {
            stocksCopy.add(symbol);
        } else {
            stocksCopy.remove(symbol);
        }

        SharedPreferences prefs = getAppSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(stocksKey, stocksCopy   );
        editor.apply();

    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = getAppSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = getAppSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

}
