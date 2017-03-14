package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.History;
import com.udacity.stockhawk.model.Stock;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private Stock stock;
    private DecimalFormat moneyFormat, percentFormat, moneyFormatWithPlus;

    @BindView(R.id.toolbar                ) Toolbar toolbar;
    @BindView(R.id.toolbar_title          ) TextView toolbarTitle;
    @BindView(R.id.tab_layout             ) TabLayout tabLayout;
    @BindView(R.id.symbol_textview        ) TextView tvSymbol;
    @BindView(R.id.price_textview         ) TextView tvPrice;
    @BindView(R.id.abs_change_textview    ) TextView tvAbsChange;
    @BindView(R.id.percent_change_textview) TextView tvPercentChange;
    @BindView(R.id.detail_viewpager       ) ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String symbol = getIntent().getStringExtra(MainActivity.EXTRA_SYMBOL);
        stock = getStockForSymbol(symbol);

        setupToolbar();
        initFormatters();
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager);
        // initTabs();

        tvSymbol.setText(symbol);
        tvPrice.setText( moneyFormat.format(stock.getPrice()) );
        tvAbsChange.setText( moneyFormatWithPlus.format(stock.getAbsoluteChange()) );
        tvPercentChange.setText( percentFormat.format(stock.getPercentageChange()) );

    }

    protected void setupToolbar() {

        toolbar.setElevation(0);
        toolbarTitle.setText(stock.getName());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void setupViewPager() {



        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DetailListFragment.ARG_HISTORY_LIST, stock.getHistory());

        Fragment detailListFragment = new DetailListFragment();
        detailListFragment.setArguments(bundle);

        Fragment detailChartFragment = new DetailChartFragment();
        detailChartFragment.setArguments(bundle);

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(detailChartFragment, "Chart");
        adapter.addFragment(detailListFragment, "List");

        viewPager.setAdapter(adapter);

    }

//    protected void initTabs() {
//
//        TabLayout.Tab tabList = tabLayout.newTab();
//        TabLayout.Tab tabChart = tabLayout.newTab();
//
//        tabList.setIcon(R.drawable.tab_view_list_white).setContentDescription(R.string.tab_list_content_description);
//        tabChart.setIcon(R.drawable.tab_timeline_white).setContentDescription(R.string.tab_chart_content_description);
//
//        tabLayout.addTab(tabList);
//        tabLayout.addTab(tabChart);
//
//    }

    static class Adapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

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

    protected Stock getStockForSymbol(String symbol) {

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

        ArrayList<History> historyList = new ArrayList<>();
        HashMap<String, Float> historyMap = new HashMap<>();

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

            historyList.add(new History(date, datePrice));

        }

        return new Stock(price, absoluteChange, percentageChange, historyList, name);

    }

}
