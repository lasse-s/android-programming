package com.example.lasse.bmi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Double height = 0.0;
    private Double weight = 0.0;
    private Double result = 0.0;

    private TextView heightEdit;
    private TextView weightEdit;
    private TextView resultView;

    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heightEdit = findViewById(R.id.heightEdit);
        weightEdit = findViewById(R.id.weightEdit);
        resultView = findViewById(R.id.result);

        heightEdit.addTextChangedListener(heightEditWatcher);
        weightEdit.addTextChangedListener(weightEditWatcher);
        locale = getResources().getConfiguration().getLocales().get(0);

    }

    private final TextWatcher heightEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)) {
                height = Double.parseDouble(charSequence.toString());
            } else {
                height = 0.0;
            }
            calculateActivity();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher weightEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)) {
                weight = Double.parseDouble(charSequence.toString());
            } else {
                weight = 0.0;
            }
            calculateActivity();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void calculateActivity() {
        // calculate BMI
        if (height != 0.0 && weight != 0.0) {
            result = weight / (Math.pow(height / 100, 2));
            resultView.setText(String.format(locale, "%.2f", result));
        }
    }

}
