package com.udacity.stockhawk.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.model.History;
import com.udacity.stockhawk.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class DetailChartFragment extends Fragment {

    @BindView(R.id.chart) LineChartView chartView;

    public static final String ARG_HISTORY_LIST = "param_history_list";

    private ArrayList<History> historyList;

    public DetailChartFragment() {
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

        View view = inflater.inflate(R.layout.fragment_detail_chart, container, false);
        ButterKnife.bind(this, view);

        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisXValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();

        for (History h : historyList.subList(0, 5)) {

            values.add(new PointValue(Float.valueOf(h.getDate()), h.getPrice()));

            AxisValue xValue = new AxisValue(Float.parseFloat(h.getDate()));
            xValue.setLabel( DateUtils.getChartLabelDate(Long.valueOf(h.getDate())) );
            axisXValues.add(xValue);

            axisYValues.add(new AxisValue(h.getPrice()));

        }

        // In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values);
        line.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(axisXValues);
        axisX.setTextColor(Color.BLACK);
        axisX.setName("Date");
        data.setAxisXBottom(axisX);

        Axis axisY = new Axis(axisYValues);
        axisY.setTextColor(Color.BLACK);
        axisY.setName("Price");
        data.setAxisYLeft(axisY);

        chartView.setInteractive(true);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chartView.setLineChartData(data);
        chartView.setNestedScrollingEnabled(true);

        return view;

    }
}
