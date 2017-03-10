package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.MyStock;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    private MyStock stock;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.symbol_textview)
    TextView tvSymbol;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.price_textview)
    TextView tvPrice;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.abs_change_textview)
    TextView tvAbsChange;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.percent_change_textview)
    TextView tvPercentChange;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.history_recyclerview)
    TextView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent extras = getIntent();
        String symbol = extras.getStringExtra(MainActivity.EXTRA_SYMBOL);

        stock = getStockForSymbol(symbol);

        toolbar.setTitle(stock.getName());
        setSupportActionBar(toolbar);

        tvSymbol.setText("Symbol clicked: " + symbol);
        tvPrice.setText(String.valueOf(stock.getPrice()));
        tvAbsChange.setText(String.valueOf(stock.getAbsoluteChange()));
        tvPercentChange.setText(String.valueOf(stock.getPercentageChange()));

    }

    protected MyStock getStockForSymbol(String symbol) {

        String[] columns = new String[] {
                Contract.Quote._ID,
                Contract.Quote.COLUMN_SYMBOL,
                Contract.Quote.COLUMN_PRICE,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                Contract.Quote.COLUMN_HISTORY,
                Contract.Quote.COLUMN_NAME
        };

        Cursor c = this.getContentResolver().query(
                Contract.Quote.makeUriForStock(symbol),
                columns,
                null, null, null
        );

        if (c == null || !c.moveToFirst()) {
            return null;
        }

        HashMap<String, Float> historyList = new HashMap<>();

        float price = Float.parseFloat(c.getString(Contract.Quote.POSITION_PRICE));
        float absoluteChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
        float percentageChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE));
        String history = c.getString(Contract.Quote.POSITION_HISTORY);
        String name = c.getString(Contract.Quote.POSITION_NAME);

        Timber.d(
                "Quote: " + price + ", " +  absoluteChange + ", " + percentageChange + ", " +
                name
        );

        String[] historyArray = history.split("\n");

        for (String h : historyArray) {

            String[] histDetail = h.split(",");

            String date = histDetail[0];
            float datePrice = Float.parseFloat(histDetail[1]);

            historyList.put(date, datePrice);

//                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(Long.valueOf(date));
//
//                Timber.d(
//                    "Date: " +
//                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
//                    calendar.get(Calendar.MONTH) + "/" +
//                    calendar.get(Calendar.YEAR) +
//                    " Value: " + datePrice
//                );

        }

        return new MyStock(price, absoluteChange, percentageChange, historyList, name);

    }

}
