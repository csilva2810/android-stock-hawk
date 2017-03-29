package com.udacity.stockhawk.ui;

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
import com.udacity.stockhawk.model.Stock;
import com.udacity.stockhawk.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private Stock mStock;

    @BindView(R.id.collapsing_toolbar     ) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.toolbar                ) Toolbar mToolbar;
    @BindView(R.id.toolbar_title          ) TextView mToolbarTitle;
    @BindView(R.id.tab_layout             ) TabLayout mTabLayout;
    @BindView(R.id.symbol_textview        ) TextView mTvSymbol;
    @BindView(R.id.price_textview         ) TextView mTvPrice;
    @BindView(R.id.abs_change_textview    ) TextView mTvAbsChange;
    @BindView(R.id.percent_change_textview) TextView mTvPercentChange;
    @BindView(R.id.detail_viewpager       ) ViewPager mViewPager;
    @BindView(R.id.nested_scrollview      ) NestedScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        String symbol = getIntent().getStringExtra(MainActivity.EXTRA_SYMBOL);
        mStock = Stock.getStockForSymbol(DetailActivity.this, symbol);

        setupToolbar();
        setupViewPager();

        mTabLayout.setupWithViewPager(mViewPager);
        mScrollView.setFillViewport(true);
        mScrollView.setNestedScrollingEnabled(true);
        mScrollView.setSmoothScrollingEnabled(true);

        String price = NumberUtils.formatMoney(mStock.getmPrice());
        String absChange = NumberUtils.formatMoneyWithPlus(mStock.getmAbsoluteChange());
        String percentChange = NumberUtils.formatPercent(mStock.getmPercentageChange());

        mTvSymbol.setText(symbol);
        mTvPrice.setText(price);
        mTvAbsChange.setText(absChange);
        mTvPercentChange.setText(percentChange);

        mTvSymbol.setContentDescription(getString(R.string.a11y_symbol, symbol));
        mTvPrice.setContentDescription(getString(R.string.a11y_stock_price, price));
        mTvAbsChange.setContentDescription(getString(R.string.a11y_stock_change, absChange));
        mTvPercentChange.setContentDescription(getString(R.string.a11y_stock_change, percentChange));

    }

    protected void setupToolbar() {


        mToolbar.setElevation(0);
        mToolbarTitle.setText(mStock.getmName());
        mToolbarTitle.setContentDescription(getString(R.string.a11y_detail_title, mStock.getmName()));
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    protected void setupViewPager() {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(DetailListFragment.ARG_HISTORY_LIST, mStock.getmHistory());

        Fragment detailListFragment = new DetailListFragment();
        detailListFragment.setArguments(bundle);

        Fragment detailChartFragment = new DetailChartFragment();
        detailChartFragment.setArguments(bundle);

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(detailChartFragment, getString(R.string.chart_tab_title));
        adapter.addFragment(detailListFragment, getString(R.string.list_tab_title));

        mViewPager.setAdapter(adapter);

    }

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

}
