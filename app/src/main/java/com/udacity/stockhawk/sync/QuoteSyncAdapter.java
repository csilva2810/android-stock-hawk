package com.udacity.stockhawk.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

/**
 * Created by carlinhos on 3/26/17.
 */

public class QuoteSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    public static final String ACTION_STOCK_NOT_FOUND = "" +
            "com.udacity.stockhawk.ACTION_STOCK_NOT_FOUND";

    private static final int YEARS_OF_HISTORY = 1;

    private ContentResolver mContentResolver;
    private Context mContext;

    public QuoteSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {

        Timber.d("Running SyncAdapter");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {

            Set<String> stockPref = new HashSet<String>();
            boolean isInitialized = PrefUtils.isInitialized(mContext);

            if (!isInitialized) {
                stockPref = PrefUtils.initializeStocks(mContext);
            } else {
                final int SYMBOL_INDEX = 0;
                Cursor c = mContentResolver.query(
                        Contract.Quote.URI,
                        new String[]{ Contract.Quote.COLUMN_SYMBOL },
                        null, null, Contract.Quote.COLUMN_SYMBOL);
                if (c != null) {
                    while (c.moveToNext()) {
                        stockPref.add(c.getString(SYMBOL_INDEX));
                    }
                    c.close();
                }
            }

            if (stockPref.size() == 0) {
                Timber.d("No Stocks to Fetch");
                return;
            }

            Timber.d("Stock pref: " + stockPref.toString());

            Map<String, Stock> quotes = YahooFinance.get(
                    stockPref.toArray(new String[stockPref.size()]));

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            for (String symbol: stockPref) {

                Stock stock = quotes.get(symbol);
                StockQuote quote = stock.getQuote();

                try {

                    float price = quote.getPrice().floatValue();
                    float change = quote.getChange().floatValue();
                    float percentChange = quote.getChangeInPercent().floatValue();

                    // WARNING! Don't request historical data for a stock that doesn't exist!
                    // The request will hang forever X_x
                    List<HistoricalQuote> history = stock.getHistory(from, to, Interval.WEEKLY);
                    String stockName = stock.getName();

                    StringBuilder historyBuilder = new StringBuilder();

                    for (HistoricalQuote it : history) {
                        historyBuilder.append(it.getDate().getTimeInMillis());
                        historyBuilder.append(", ");
                        historyBuilder.append(it.getClose());
                        historyBuilder.append(", ");
                        historyBuilder.append(it.getLow());
                        historyBuilder.append(", ");
                        historyBuilder.append(it.getHigh());
                        historyBuilder.append("\n");
                        // Timber.d("History Quote: " + historyBuilder.toString());
                    }

                    ContentValues quoteCV = new ContentValues();
                    quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                    quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                    quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                    quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);

                    quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());
                    quoteCV.put(Contract.Quote.COLUMN_NAME, stockName);

                    quoteCVs.add(quoteCV);

                } catch (NullPointerException e) {
                    Timber.e("Error:" + e);
                    stockPref.remove(symbol);
                    Intent stockNotFound = new Intent(ACTION_STOCK_NOT_FOUND);
                    stockNotFound.putExtra(MainActivity.EXTRA_SYMBOL, symbol);
                    mContext.sendBroadcast(stockNotFound);
                }

            }

            mContentResolver
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()])
                    );

            Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
            Timber.d("sendBroadcast: " + dataUpdatedIntent.getAction());
            mContext.sendBroadcast(dataUpdatedIntent);

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }

    }

}
