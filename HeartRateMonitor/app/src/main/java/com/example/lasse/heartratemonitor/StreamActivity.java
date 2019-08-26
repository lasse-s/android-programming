package com.example.lasse.heartratemonitor;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lasse.heartratemonitor.database.Measurement;
import com.example.lasse.heartratemonitor.database.MeasurementPoint;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.UUID;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarHrBroadcastData;
import polar.com.sdk.api.model.PolarHrData;

/**
 * Activity for streaming and recording HR data from BLE-device.
 */
public class StreamActivity extends AppCompatActivity {

    private final static String TAG = StreamActivity.class.getSimpleName();
    PolarBleApi api;
    Disposable broadcastDisposable;
    Boolean recording = false;
    Long lastSampleTime;
    int pointCounter = 0;
    LineGraphSeries<DataPoint> series;
    GraphView graph;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Long measurementID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        final Button startRec = findViewById(R.id.buttonStartRec);
        final Button stopRec = findViewById(R.id.buttonStopRec);

        final TextView recordingText = findViewById(R.id.recordingText);
        recordingText.setVisibility(View.INVISIBLE);

        sharedPreferences = getSharedPreferences("polar_hr", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        graph = findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 50)
        });
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.addSeries(series);

        api.setApiLogger(new PolarBleApi.PolarBleApiLogger() {
            @Override
            public void message(String s) {
                Log.d(TAG,s);
            }
        });

        Log.d(TAG,"version: " + PolarBleApiDefaultImpl.versionInfo());

        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recording = true;
                Measurement measurement = new Measurement();
                measurement.setTimestamp(System.currentTimeMillis());
                recordingText.setVisibility(View.VISIBLE);
                toggleBroadcast("START");
                saveMeasurement(measurement);
            }
        });

        lastSampleTime = System.currentTimeMillis();

        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recording) {
                    recording = false;
                    recordingText.setVisibility(View.INVISIBLE);
                    toggleBroadcast("STOP");
                }
            }
        });

        toggleBroadcast("START");

        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean powered) {
                Log.d(TAG,"BLE power: " + powered);
            }

            @Override
            public void hrFeatureReady(String identifier) {
                Log.d(TAG,"HR READY: " + identifier);
                // hr notifications are about to start
            }

            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
                Log.d(TAG,"uuid: " + uuid + " value: " + value);

            }

            @Override
            public void batteryLevelReceived(String identifier, int level) {
                Log.d(TAG,"BATTERY LEVEL: " + level);

            }

            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {
                Log.d(TAG, "hr notification received");
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && savedInstanceState == null) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }

    /**
     * When permission request result is received
     * @param requestCode permission request code
     * @param permissions list of permissions to ask
     * @param grantResults given permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == 1) {
            Log.d(TAG,"bt ready");
        }
    }

    /**
     * For persisting {@link Measurement} entity.
     * @param measurement {@link Measurement} object to be saved
     */
    public void saveMeasurement(final Measurement measurement) {

        ((TestApp)getApplication()).getDaoSession().getMeasurementDao().save(measurement);
        measurementID = measurement.getId();

    }

    /**
     * For toggling HR broadcast.
     * @param method determines if broadcast should be started or stopped.
     */
    private void toggleBroadcast(final String method) {
        if (method.equals("START")) {
            broadcastDisposable = api.startListenForPolarHrBroadcasts(null).subscribe(
                    new Consumer<PolarHrBroadcastData>() {
                        @Override
                        public void accept(PolarHrBroadcastData polarBroadcastData) throws Exception {
                            Log.d(TAG,"HR BROADCAST " +
                                    polarBroadcastData.polarDeviceInfo.deviceId + " HR: " +
                                    polarBroadcastData.hr + " batt: " +
                                    polarBroadcastData.batteryStatus +
                                    " " + System.currentTimeMillis()
                            );
                            if (recording) {
                                persistValues(polarBroadcastData);
                            }
                            series.appendData(
                                    new DataPoint(pointCounter, polarBroadcastData.hr), true, 100, false);
                            editor.putString(String.valueOf(System.currentTimeMillis()), String.valueOf(polarBroadcastData.hr));
                            editor.apply();

                            graph.addSeries(series);
                            pointCounter = pointCounter + 1;
                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG,""+throwable.getLocalizedMessage());
                        }
                    },
                    new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.d(TAG,"complete");
                        }
                    }
            );
        } else{
            broadcastDisposable.dispose();
            broadcastDisposable = null;
        }
    }

    /**
     * For persisting {@link MeasurementPoint}s.
     * @param data source {@link PolarHrBroadcastData}
     */
    private void persistValues(PolarHrBroadcastData data) {

        Log.d(TAG, "starting saveing");
        MeasurementPoint measurementPoint = new MeasurementPoint();
        measurementPoint.setHr(data.hr);
        measurementPoint.setMeasurementID(measurementID);
        measurementPoint.setTimestamp(System.currentTimeMillis());
        ((TestApp)getApplication()).getDaoSession().getMeasurementPointDao().save(measurementPoint);

        Log.d(TAG, "Data saved to database.");

    }

    @Override
    public void onPause() {
        super.onPause();
        api.backgroundEntered();
    }

    @Override
    public void onResume() {
        super.onResume();
        api.foregroundEntered();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        api.shutDown();
    }

    /**
     * Helper method for setting activity to run fullscreen / immersive mode.
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
