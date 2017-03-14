package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.model.History;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailListFragment extends Fragment {

    @BindView(R.id.history_recyclerview) RecyclerView rvHistory;

    public static final String ARG_HISTORY_LIST = "param_history_list";

    private ArrayList<History> historyList;

    public DetailListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            historyList = getArguments().getParcelableArrayList(ARG_HISTORY_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_list, container, false);
        ButterKnife.bind(this, view);

        rvHistory.setNestedScrollingEnabled(false);
        rvHistory.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        rvHistory.setAdapter(new HistoryAdapter(view.getContext(), historyList));

        return view;

    }

}
