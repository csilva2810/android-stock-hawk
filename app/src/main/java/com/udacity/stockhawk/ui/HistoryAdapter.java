package com.udacity.stockhawk.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.model.History;
import com.udacity.stockhawk.utils.DateUtils;
import com.udacity.stockhawk.utils.NumberUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private ArrayList<History> historyList;

    public HistoryAdapter(Context context, ArrayList<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.date_textview) TextView tvDate;
        @BindView(R.id.price_textview) TextView tvPrice;

        HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false);
        return new HistoryViewHolder(item);

    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        History history = historyList.get(position);

        String date = DateUtils.getDisplayDate(Long.valueOf(history.getmDate()));
        String price = NumberUtils.formatMoney(history.getmPrice());

        holder.tvDate.setText(date);
        holder.tvPrice.setText(price);

        holder.tvDate.setContentDescription(context.getString(R.string.a11y_stock_date, date));
        holder.tvPrice.setContentDescription(context.getString(R.string.a11y_stock_price, price));

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

}
