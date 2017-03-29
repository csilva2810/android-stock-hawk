package com.udacity.stockhawk.ui;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.helper.SimpleItemTouchHelperCallback;
import com.udacity.stockhawk.sync.QuoteSyncAdapter;
import com.udacity.stockhawk.sync.SyncUtils;
import com.udacity.stockhawk.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener,
        StockAdapter.StockAdapterOnClickHandler {

    public static final String STOCK_DIALOG_FRAGMENT = "StockDialogFragment";
    public static final String EXTRA_SYMBOL = "EXTRA_SYMBOL";
    public static final String ACTION_ADD_STOCK = "ACTION_ADD_STOCK";

    private static final int STOCK_LOADER = 0;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_view) RecyclerView mStockRecyclerView;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.error) TextView mErrorTextView;

    private ItemTouchHelper mItemTouchHelper;
    private StockAdapter mAdapter;
    private BroadcastReceiver mStockNotFoundBr;

    @Override
    public void onClick(String symbol) {

        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(EXTRA_SYMBOL, symbol);
        startActivity(detailIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(mToolbar);

        mAdapter = new StockAdapter(this);
        mStockRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mStockRecyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mStockRecyclerView);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setRefreshing(true);

        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        SyncUtils.CreateSyncAccount(MainActivity.this);

        String intentAction = getIntent().getAction();
        if (intentAction != null) {
            if (intentAction.equals(ACTION_ADD_STOCK)) {
                onClickAddStock(null);
            }
        }

    }

    protected void registerStockNotFoundReceiver() {
        mStockNotFoundBr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = getString(R.string.stock_not_found,
                        intent.getStringExtra(EXTRA_SYMBOL));

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        };

        IntentFilter stockNotFoundFilter = new IntentFilter(QuoteSyncAdapter.ACTION_STOCK_NOT_FOUND);
        stockNotFoundFilter.addAction(QuoteSyncAdapter.ACTION_STOCK_NOT_FOUND);
        this.registerReceiver(mStockNotFoundBr, stockNotFoundFilter);
    }

    protected void unregisterStockNotFoundReceiver() {
        this.unregisterReceiver(mStockNotFoundBr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterStockNotFoundReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerStockNotFoundReceiver();
    }

    @Override
    public void onRefresh() {

        if (!Utility.isNetworkAvailable(this) && mAdapter.getItemCount() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
            mErrorTextView.setText(getString(R.string.error_no_network));
            mErrorTextView.setVisibility(View.VISIBLE);
        } else if (!Utility.isNetworkAvailable(this)) {
            mSwipeRefreshLayout.setRefreshing(false);
            mErrorTextView.setText(R.string.toast_no_connectivity);
            mErrorTextView.setVisibility(View.VISIBLE);
        } else if (mAdapter.getItemCount() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
            mErrorTextView.setText(getString(R.string.error_no_stocks));
            mErrorTextView.setVisibility(View.VISIBLE);
        } else {
            mErrorTextView.setVisibility(View.GONE);
            SyncUtils.TriggerRefresh(MainActivity.this);
        }

    }

    public void onClickAddStock(@SuppressWarnings("UnusedParameters") View view) {
        new AddStockDialog().show(getFragmentManager(), STOCK_DIALOG_FRAGMENT);
    }

    void addStock(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {

            if (Utility.isNetworkAvailable(this)) {

                mSwipeRefreshLayout.setRefreshing(true);

                ContentValues cv = new ContentValues();
                cv.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                cv.put(Contract.Quote.COLUMN_PRICE, "");
                cv.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, "");
                cv.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, "");
                cv.put(Contract.Quote.COLUMN_HISTORY, "");
                cv.put(Contract.Quote.COLUMN_NAME, "");
                getContentResolver().insert(Contract.Quote.URI, cv);

                SyncUtils.TriggerRefresh(MainActivity.this);

            } else {
                String message = getString(R.string.error_no_network_try_again);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (data.getCount() != 0) {
            mErrorTextView.setVisibility(View.GONE);
        }

        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setCursor(null);
    }

    private void setDisplayModeMenuItemIcon(MenuItem item) {
        if (PrefUtils.getDisplayMode(this)
                .equals(getString(R.string.pref_display_mode_absolute_key))) {
            item.setIcon(R.drawable.ic_percentage);
            item.setTitle(R.string.a11y_display_mode_abs);
        } else {
            item.setIcon(R.drawable.ic_dollar);
            item.setTitle(R.string.a11y_display_mode_percent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_settings, menu);
        MenuItem item = menu.findItem(R.id.action_change_units);
        setDisplayModeMenuItemIcon(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_units) {
            PrefUtils.toggleDisplayMode(this);
            setDisplayModeMenuItemIcon(item);
            mAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
