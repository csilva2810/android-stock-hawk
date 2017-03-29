package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

        int labelColor = ContextCompat.getColor(getActivity(), R.color.chartLabelColor);
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisXValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();

        for (History h : historyList.subList(0, 5)) {

            values.add(new PointValue(Float.valueOf(h.getmDate()), h.getmPrice()));

            AxisValue xValue = new AxisValue(Float.parseFloat(h.getmDate()));
            xValue.setLabel( DateUtils.getChartLabelDate(Long.valueOf(h.getmDate())) );
            axisXValues.add(xValue);

            axisYValues.add(new AxisValue(h.getmPrice()));

        }

        Line line = new Line(values);
        line.setColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        line.setFilled(true);
        line.setHasLabels(true);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(axisXValues);
        axisX.setTextColor(labelColor);
        axisX.setName(getString(R.string.chart_date_label));

        Axis axisY = new Axis(axisYValues);
        axisY.setTextColor(labelColor);
        axisY.setName(getString(R.string.chart_price_label));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        chartView.setInteractive(true);
        chartView.setNestedScrollingEnabled(true);
        chartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chartView.setLineChartData(data);

        return view;

    }
}
