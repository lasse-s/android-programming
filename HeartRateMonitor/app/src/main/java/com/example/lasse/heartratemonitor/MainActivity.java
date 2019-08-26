package com.example.lasse.heartratemonitor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.errors.PolarInvalidArgument;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;

/**
 * Application's main activity.
 */
public class MainActivity extends AppCompatActivity {

    String DEVICE_ID = ""; // or bt address like F5:A7:B8:EF:7A:D1 // TODO replace with your device id
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    PolarBleApi api;
    private static final String TAG = "MainActivity";
    Button continueButton;

    /**
     * When activity is created.
     * @param savedInstanceState saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Polar BLE API
        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        EditText deviceIdEdit = findViewById(R.id.deviceIdEdit);

        deviceIdEdit.addTextChangedListener(editDeviceIdWatcher);
        final Button connect = findViewById(R.id.buttonConnect);
        final TextView connected = findViewById(R.id.connectedStatus);

        continueButton = this.findViewById(R.id.buttonContinue);
        continueButton.setEnabled(false);

        api.setApiLogger(new PolarBleApi.PolarBleApiLogger() {
            @Override
            public void message(String s) {
                Log.d(TAG,s);
            }
        });

        checkBT();

        // Set callbacks for Polar BLE API
        api.setApiCallback(new PolarBleApiCallback() {

            /**
             * When ble power state is changed.
             * @param powered if power is on or off
             */
            @Override
            public void blePowerStateChanged(boolean powered) {
                Log.d(TAG,"BLE power: " + powered);
            }

            /**
             * When device is connected.
             * @param polarDeviceInfo info about connected device
             */
            @Override
            public void deviceConnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"CONNECTED: " + polarDeviceInfo.deviceId);
                connected.setText(R.string.connected_text);
                connected.setTextColor(Color.GREEN);
                DEVICE_ID = polarDeviceInfo.deviceId;
                continueButton.setEnabled(true);
            }

            /**
             * When device is still connecting.
             * @param polarDeviceInfo info about connected device
             */
            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"CONNECTING: " + polarDeviceInfo.deviceId);
                DEVICE_ID = polarDeviceInfo.deviceId;
            }

            /**
             * When device is disconnected.
             * @param polarDeviceInfo info about connected device
             */
            @Override
            public void deviceDisconnected(PolarDeviceInfo polarDeviceInfo) {
                Log.d(TAG,"DISCONNECTED: " + polarDeviceInfo.deviceId);
                connected.setText(R.string.disconnected_text);
                connected.setTextColor(Color.RED);
            }

            /**
             * When hr feature is ready.
             * @param identifier id string
             */
            @Override
            public void hrFeatureReady(String identifier) {
                Log.d(TAG,"HR READY: " + identifier);
                // hr notifications are about to start
            }

            /**
             * If disinformation is received.
             * @param identifier info id
             * @param uuid info uuid
             * @param value value for disinformation
             */
            @Override
            public void disInformationReceived(String identifier, UUID uuid, String value) {
                Log.d(TAG,"uuid: " + uuid + " value: " + value);

            }

            /**
             * When battery level is received.
             * @param identifier id
             * @param level battery level as int
             */
            @Override
            public void batteryLevelReceived(String identifier, int level) {
                Log.d(TAG,"BATTERY LEVEL: " + level);

            }

            /**
             * When HR notification is received.
             * @param identifier id
             * @param data data
             */
            @Override
            public void hrNotificationReceived(String identifier, PolarHrData data) {

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    api.connectToDevice(DEVICE_ID);

                } catch (PolarInvalidArgument polarInvalidArgument) {
                    polarInvalidArgument.printStackTrace();
                }
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && savedInstanceState == null) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    /**
     * When continue button is clicked.
     * @param view this view
     */
    public void onClickContinue(View view) {
        Intent intent = new Intent(this, StreamActivity.class);
        startActivity(intent);
    }

    /**
     * For checking if bluetooth adapter is found and enabled.
     * If is, request for BT permissions
     */
    public void checkBT() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        this.requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    /**
     * Text watcher for device id text edit.
     */
    private final TextWatcher editDeviceIdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DEVICE_ID = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


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
     * When options menu is created.
     * @param menu created menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * When options menu is selected.
     * @param item selected item
     * @return call to super class
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.storage:
                Intent intent = new Intent(this, ListViewActivity.class);
                startActivity(intent);
            case R.id.live_monitor:
                Intent intent2 = new Intent(this, GraphViewActivity.class);
                //startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
