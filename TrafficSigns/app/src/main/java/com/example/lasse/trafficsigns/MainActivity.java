package com.example.lasse.trafficsigns;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // keys for reading data from SharedPreferences
    public static final String CHOICES = "pref_numberOfChoices";

    private boolean phoneDevice = true; // pakotetaan pystyasento
    private boolean preferencesChanged = true; // onko asetuksia muutettu?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // asetetaan oletusarvo sovelluksen SharePreferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // rekisteröidään kuuntelija SharedPreferences muutoksille
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(
                        preferencesChangeListener); // määritellään myöhemmin koodissa

        // määritellään näytön koko
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // jos laite on tabletti, niin määritellään phoneDevice epätodeksi
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        // jos ajetaan puhelimella, niin salli vain pystyasento
        if (phoneDevice)
            setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }); */
    }

    // kutsutaan onCreate:n jälkeen
    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
            // kun perusasetukset on tehty, niin alustetaan MainActivityFragmetn
            // ja käynnistetään visailu
            MainActivityFragment quizFragment = (MainActivityFragment)
                    getSupportFragmentManager().findFragmentById(
                            R.id.quizFragment);
            quizFragment.updateGuessRows(
                    PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    // näytetään menu vain kun sovellus on pystyasennossa
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // luetaan laitteen suunta
        int orientation = getResources().getConfiguration().orientation;

        // näytetään menu vain jos pystyasennossa
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        else
            return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    /*
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
        return true;
    }
    */

        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
        return super.onOptionsItemSelected(item);
    }

    //kuuntelija muutoksille SharedPreferences:ssa
    private OnSharedPreferenceChangeListener preferencesChangeListener =
            new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true; // käyttäjä muutti asetuksia

                    MainActivityFragment quizFragment = (MainActivityFragment)
                            getSupportFragmentManager().findFragmentById(
                                    R.id.quizFragment);

                    if (key.equals(CHOICES)) { // valintojen lukumäärä
                        quizFragment.updateGuessRows(sharedPreferences); // metodi määritellään myöhemmin toisessa luokassa
                        quizFragment.resetQuiz(); // metodi määritellään myöhemmin toisessa luokassa
                    }

                    Toast.makeText(MainActivity.this, // näyttää ilmoituksen muutoksesta
                            R.string.restarting_quiz,
                            Toast.LENGTH_SHORT).show();
                }
            };

}
