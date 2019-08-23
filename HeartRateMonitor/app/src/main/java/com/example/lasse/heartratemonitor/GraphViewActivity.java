package com.example.lasse.heartratemonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.lasse.heartratemonitor.database.MeasurementPoint;
import com.example.lasse.heartratemonitor.database.MeasurementPointDao;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing saved Measurements.
 */
public class GraphViewActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;
    GraphView graph;
    ArrayList<DataPoint> dataPoints = new ArrayList<>();

    /**
     * When activity is created.
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);

        Intent intent = getIntent();

        long value = 0; // or other values
        if(intent.getExtras() != null) {
            value = intent.getExtras().getLong("measurementID");
        }

        getAllMeasurementPoints(value);

    }

    /**
     * Update graph with new data.
     */
    private void updateGraph() {
        graph = findViewById(R.id.graph);

        DataPoint[] points = dataPoints.toArray(new DataPoint[dataPoints.size()]);

        double yMax = 0;
        double yMin = 300;
        for (int i = 0; i < dataPoints.size(); i++) {
            if (dataPoints.get(i).getY() > yMax) {
                yMax = dataPoints.get(i).getY();
            }
            if (dataPoints.get(i).getY() < yMin) {
                yMin = dataPoints.get(i).getY();
            }
        }

        series = new LineGraphSeries<>(points);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setMinY(yMin - 10);
        graph.getViewport().setMaxY(yMax + 10);
        graph.getViewport().setScrollable(true);
        graph.addSeries(series);
    }

    /**
     * For getting list of {@link MeasurementPoint} linked to given measurement id.
     * @param measurementID
     */
    private void getAllMeasurementPoints(final Long measurementID) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("MEASUREMENT", "loading measurementPoints");
                List<MeasurementPoint> measurementPoints = ((TestApp) getApplication())
                        .getDaoSession().getMeasurementPointDao().queryBuilder()
                        .where(MeasurementPointDao.Properties.MeasurementID.eq(measurementID))
                        .list();

                Log.d("MEASUREMENT", "LENGTH " + measurementPoints.size());
                int counter = 0;
                for (MeasurementPoint measurement : measurementPoints) {
                    Log.d("MEASUREMENT", "TIME: " + measurement.getTimestamp()
                            + " HR: " + measurement.getHr());
                    final DataPoint dataPoint =
                            new DataPoint(counter, measurement.getHr());
                    dataPoints.add(dataPoint);
                    counter++;
                }

                Log.d("DATABASE", "Measurement entity saved.");
                updateGraph();

            }
        };

        AsyncTask.execute(runnable);

    }

}
