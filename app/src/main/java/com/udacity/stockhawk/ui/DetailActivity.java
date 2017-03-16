package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.model.History;
import com.udacity.stockhawk.model.Stock;
import com.udacity.stockhawk.utils.NumberUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private Stock stock;

    @BindView(R.id.collapsing_toolbar     ) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar                ) Toolbar toolbar;
    @BindView(R.id.toolbar_title          ) TextView toolbarTitle;
    @BindView(R.id.tab_layout             ) TabLayout tabLayout;
    @BindView(R.id.symbol_textview        ) TextView tvSymbol;
    @BindView(R.id.price_textview         ) TextView tvPrice;
    @BindView(R.id.abs_change_textview    ) TextView tvAbsChange;
    @BindView(R.id.percent_change_textview) TextView tvPercentChange;
    @BindView(R.id.detail_viewpager       ) ViewPager viewPager;
    @BindView(R.id.nested_scrollview      ) NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String symbol = getIntent().getStringExtra(MainActivity.EXTRA_SYMBOL);
        stock = getStockForSymbol(symbol);

        setupToolbar();
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager);
        scrollView.setFillViewport(true);
        scrollView.setNestedScrollingEnabled(true);
        scrollView.setSmoothScrollingEnabled(true);

        tvSymbol.setText(symbol);
        tvPrice.setText(NumberUtils.formatMoney(stock.getPrice()));
        tvAbsChange.setText( NumberUtils.formatMoneyWithPlus(stock.getAbsoluteChange()) );
        tvPercentChange.setText( NumberUtils.formatPercent(stock.getPercentageChange()) );

    }

    protected void setupToolbar() {

        toolbar.setElevation(0);
        collapsingToolbar.setTitle(stock.getName());
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
        adapter.addFragment(detailChartFragment, getString(R.string.chart_tab_title));
        adapter.addFragment(detailListFragment, getString(R.string.list_tab_title));

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
            float low = Float.valueOf(histDetail[2]);
            float high = Float.valueOf(histDetail[3]);

            historyList.add(new History(date, datePrice, low, high));

        }

        return new Stock(price, absoluteChange, percentageChange, historyList, name);

    }

}
