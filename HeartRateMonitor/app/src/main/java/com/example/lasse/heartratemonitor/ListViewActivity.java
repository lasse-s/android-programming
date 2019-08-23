package com.example.lasse.heartratemonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lasse.heartratemonitor.database.Measurement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class for showing fetching and displaying list of saved {@link Measurement}s.
 */
public class ListViewActivity extends AppCompatActivity {

    final DateFormat dateformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
    List<String> measurementList = new ArrayList<>();

    /**
     * When activity is created.
     * @param savedInstanceState saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_view);

        final ListView measurementListView = findViewById(R.id.measurement_list);

        final ListViewActivity self = this;

        measurementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String o = (String) measurementListView.getItemAtPosition(position);

                Long measurementID = parseIdFromString(o);

                Log.d("TAG", "Measurement ID: " + measurementID.toString());

                Intent intent = new Intent(self, GraphViewActivity.class);

                Bundle b = new Bundle();
                b.putLong("measurementID", measurementID); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });

        getAllMeasurements();

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, measurementList);

        measurementListView.setAdapter(adapter);

    }

    /**
     * When options menu is created.
     * @param menu options menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * When menu item is selected.
     * @param item selected item
     * @return call to super class
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.storage:
                intent = new Intent(this, ListViewActivity.class);
                startActivity(intent);
            case R.id.live_monitor:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * For parsing measurement ID from string.
     * @param input source string
     * @return parsed ID
     */
    private Long parseIdFromString(final String input) {
        final String[] parts = input.split("\\|");
        for (String part : parts) {
            Log.d("TAG", part);
        }

        Long id = Long.valueOf(parts[1].replaceAll("\\s+", ""));
        return id;
    }

    /**
     * Fetch all {@link Measurement}s from database.
     */
    private void getAllMeasurements() {

        Log.d("TAG", "getting measurements");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("MEASUREMENT", "loading measurements");
                List<Measurement> measurements = ((TestApp) getApplication()).getDaoSession()
                        .getMeasurementDao().loadAll();

                Log.d("MEASUREMENT", "LENGTH " + measurements.size());
                for (Measurement measurement : measurements) {
                    String timeStamp = dateformat.format(new Date(measurement.getTimestamp()));
                    Log.d("MEASUREMENT", "TIME: " + timeStamp);
                    measurementList.add("| " + measurement.getId() + " |  " + timeStamp);
                }

                Log.d("DATABASE", "Measurements fetched");
            }
        };

        AsyncTask.execute(runnable);

    }

}
