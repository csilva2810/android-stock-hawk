package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.utils.NumberUtils;

public class WidgetStockService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {

                if (data != null) {
                    data.close();
                }

                // clearing identity to have access to the content provider
                final long idToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null, null, Contract.Quote.COLUMN_SYMBOL
                );

                Binder.restoreCallingIdentity(idToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if (position == AdapterView.INVALID_POSITION ||
                    data == null ||
                    !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);

                String quoteSymbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                float quotePrice = data.getFloat(Contract.Quote.POSITION_PRICE);

                views.setTextViewText(R.id.widget_item_symbol, quoteSymbol);
                views.setTextViewText(R.id.widget_item_price, NumberUtils.formatMoney(quotePrice));

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MainActivity.EXTRA_SYMBOL, quoteSymbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stock_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(Contract.Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
