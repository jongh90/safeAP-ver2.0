package com.jjtech.newap;

import android.graphics.Color;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2015-02-03.
 */
public class myGraphView {
    private GraphView graph;
    private LineGraphSeries<DataPoint> yesterday, today;
    private DataPoint[] y_data, t_data;

    public myGraphView(GraphView graph){
        this.graph = graph;
    }

    public void init(){
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#ff060606"));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.parseColor("#ff060606"));
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(23);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    if((int)value == 0) return "";
                    return super.formatLabel((int)value + 1, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel((int)value, isValueX);
                }
            }
        });
    }

    public void setData(int max, DataPoint[] y_data, DataPoint[] t_data){
        this.y_data = y_data;
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(max);
        yesterday = new LineGraphSeries<DataPoint>(this.y_data);
        yesterday.setTitle("Yesterday");
        yesterday.setColor(Color.argb(255, 122, 192, 236));

        SimpleDateFormat now = new SimpleDateFormat("HH");
        String time = now.format(new Date(System.currentTimeMillis()));
        int n = Integer.parseInt(time);
        if(n >0) n = n-1;

        this.t_data  = new DataPoint[n];
        for(int i=0; i<n; i++)
            this.t_data [i] = t_data[i];

        today = new LineGraphSeries<DataPoint>(this.t_data);
        today.setTitle("Today");
        today.setColor(Color.argb(255, 177, 137, 243));

        graph.addSeries(yesterday);
        graph.addSeries(today);
    }

    public void clear(){
        graph.removeAllSeries();
        graph.onDataChanged(true, true);
    }
}
