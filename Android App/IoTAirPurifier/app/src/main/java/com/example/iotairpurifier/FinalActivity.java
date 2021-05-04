package com.example.iotairpurifier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FinalActivity extends AppCompatActivity {

    MyDatabaseHelper myDB;
    FloatingActionButton homeFab, recordsFab;
    TextView txtDateTime, txtAirQuality, txtGasSensor;
    String ipAddress, preDate, preAirQuality, preGasSensor, postDate, postAirQuality, postGasSensor;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");

        preDate = getIntent().getStringExtra("PREDATE_EXTRA");
        preAirQuality = getIntent().getStringExtra("PREAIRQUALITY_EXTRA");
        preGasSensor = getIntent().getStringExtra("PREGASSENSOR_EXTRA");

        postDate = getIntent().getStringExtra("POSTDATE_EXTRA");
        postAirQuality = getIntent().getStringExtra("POSTAIRQUALITY_EXTRA");
        postGasSensor = getIntent().getStringExtra("POSTGASSENSOR_EXTRA");

        txtDateTime = findViewById(R.id.txtDateTimeFinal);
        txtAirQuality = findViewById(R.id.txtAirQualityFinal);
        txtGasSensor = findViewById(R.id.txtGasFinal);

        txtDateTime.setText(postDate);
        txtAirQuality.setText(postAirQuality + " µg/m3");
        txtGasSensor.setText(postGasSensor + " PPM");

        homeFab = findViewById(R.id.homeButton);
        recordsFab = findViewById(R.id.recordsButton);

        myDB = new MyDatabaseHelper(FinalActivity.this);
        myDB.addRecord(preDate, preAirQuality, preGasSensor,
                postDate, postAirQuality, postGasSensor);

        homeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        recordsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRecordsActivity();
            }
        });

    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }

    private void goRecordsActivity(){
        Intent intent = new Intent(this, RecordsActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }
}