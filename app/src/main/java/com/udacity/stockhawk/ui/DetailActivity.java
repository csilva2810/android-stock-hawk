package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.MyStock;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private MyStock stock;
    private DecimalFormat moneyFormat, percentFormat, moneyFormatWithPlus;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.symbol_textview)
    TextView tvSymbol;

    @BindView(R.id.price_textview)
    TextView tvPrice;

    @BindView(R.id.abs_change_textview)
    TextView tvAbsChange;

    @BindView(R.id.percent_change_textview)
    TextView tvPercentChange;

    //@BindView(R.id.history_recyclerview)
    // RecyclerView rvHistory;

    @BindView(R.id.detail_viewpager)
    ViewPager vpDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initFormatters();
        initTabs();

        Intent extras = getIntent();
        String symbol = extras.getStringExtra(MainActivity.EXTRA_SYMBOL);

        stock = getStockForSymbol(symbol);

        toolbar.setElevation(0);
        toolbarTitle.setText(stock.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvSymbol.setText(symbol);
        tvPrice.setText( moneyFormat.format(stock.getPrice()) );
        tvAbsChange.setText( moneyFormatWithPlus.format(stock.getAbsoluteChange()) );
        tvPercentChange.setText( percentFormat.format(stock.getPercentageChange()) );

        // rvHistory.setLayoutManager(new LinearLayoutManager(this));
        // rvHistory.setNestedScrollingEnabled(false);
        // rvHistory.setAdapter(new HistoryAdapter(this, stock.getHistory()));

    }

    protected void initTabs() {

        TabLayout.Tab tabChart = tabLayout.newTab();
        TabLayout.Tab tabList = tabLayout.newTab();

        tabChart.setIcon(R.drawable.tab_timeline_white).setContentDescription(R.string.tab_chart_content_description);
        tabList.setIcon(R.drawable.tab_view_list_white).setContentDescription(R.string.tab_list_content_description);

        tabLayout.addTab(tabChart);
        tabLayout.addTab(tabList);

    }

    protected void initFormatters() {

        moneyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();

        moneyFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance();
        moneyFormatWithPlus.setMaximumFractionDigits(2);
        moneyFormatWithPlus.setMinimumFractionDigits(2);
        moneyFormatWithPlus.setNegativePrefix("$-");
        moneyFormatWithPlus.setPositivePrefix("$");

        percentFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        percentFormat.setMaximumFractionDigits(2);
        percentFormat.setMinimumFractionDigits(2);
        percentFormat.setNegativePrefix("%-");
        percentFormat.setPositivePrefix("%");

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

        ArrayList<String[]> historyList = new ArrayList<>();
        HashMap<String, Float> historyMap = new HashMap<>();

        float price = Float.parseFloat(c.getString(Contract.Quote.POSITION_PRICE));
        float absoluteChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_ABSOLUTE_CHANGE));
        float percentageChange = Float.parseFloat(c.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE));
        String history = c.getString(Contract.Quote.POSITION_HISTORY);
        String name = c.getString(Contract.Quote.POSITION_NAME);

        String[] historyArray = history.split("\n");

        for (String h : historyArray) {

            String[] histDetail = h.split(",");
            historyList.add(histDetail);


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
