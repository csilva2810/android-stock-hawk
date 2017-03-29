package com.udacity.stockhawk.ui;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.helper.ItemTouchHelperAdapter;
import com.udacity.stockhawk.utils.NumberUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder>
    implements ItemTouchHelperAdapter {

    private final Context mContext;

    private Cursor mCursor;
    private final StockAdapterOnClickHandler clickHandler;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

        String symbol = getSymbolAtPosition(position);

        Uri stockUri = Contract.Quote.makeUriForStock(symbol);
        mContext.getContentResolver().delete(stockUri, null, null);

    }

    StockAdapter(Context context) {
        this.mContext = context;
        this.clickHandler = (StockAdapterOnClickHandler) mContext;
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(mContext).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        mCursor.moveToPosition(position);

        String stockName = mCursor.getString(Contract.Quote.POSITION_NAME);
        String symbol = mCursor.getString(Contract.Quote.POSITION_SYMBOL);
        String price = NumberUtils.formatMoney(mCursor.getFloat(Contract.Quote.POSITION_PRICE));

        holder.symbol.setText(symbol);
        holder.price.setText(price);
        holder.tvStockName.setText(stockName);

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = NumberUtils.formatMoneyWithPlus(rawAbsoluteChange);
        String percentage = NumberUtils.formatPercent(percentageChange / 100);
        String changeDesc;

        if (PrefUtils.getDisplayMode(mContext).equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            holder.change.setText(change);
            changeDesc = change;
        } else {
            holder.change.setText(percentage);
            changeDesc = percentage;
        }

        // setting more useful content descriptions
        holder.symbol.setContentDescription(mContext.getString(R.string.a11y_symbol, symbol));
        holder.price.setContentDescription(mContext.getString(R.string.a11y_stock_price, price));
        holder.change.setContentDescription(mContext.getString(R.string.a11y_stock_change, changeDesc));

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }

    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.symbol) TextView symbol;
        @BindView(R.id.price) TextView price;
        @BindView(R.id.change) TextView change;
        @BindView(R.id.stock_name) TextView tvStockName;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            clickHandler.onClick(mCursor.getString(symbolColumn));
        }


    }
}
