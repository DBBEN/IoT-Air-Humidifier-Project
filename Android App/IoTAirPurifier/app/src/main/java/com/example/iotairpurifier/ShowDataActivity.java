package com.example.iotairpurifier;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;

public class ShowDataActivity extends AppCompatActivity {

    TextView txtAirQuality, txtGas, txtDateTime;
    int airQualityData, gasSensorData;
    String ipAddress, nowDateTime;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        nowDateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        txtAirQuality = findViewById(R.id.txtAirQuality);
        txtGas = findViewById(R.id.txtGas);
        txtDateTime = findViewById(R.id.txtDateTime);

        airQualityData = getIntent().getIntExtra("AIRQUALITY_EXTRA", 0);
        gasSensorData = getIntent().getIntExtra("GASSENSOR_EXTRA", 0);
        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");

        txtDateTime.setText(nowDateTime);
        txtAirQuality.setText(String.valueOf(airQualityData) + " µg/m3");
        txtGas.setText(String.valueOf(gasSensorData) + " PPM");

//        MyDatabaseHelper myDB = new MyDatabaseHelper(ShowDataActivity.this);
//            myDB.addRecord(nowDateTime, String.valueOf(airQualityData), String.valueOf(gasSensorData));

        new CountDownTimer(2000, 1000) {
            public void onFinish() {
                if(airQualityData > 50 || gasSensorData > 100) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(ShowDataActivity.this);
                    mydialog.setTitle("Alert");
                    mydialog.setMessage("Gathered sensor data is above threshold level. Air purifier will be activated");


                    mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openControlActivity();
                        }
                    })

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
                }
            }

            public void onTick(long millisUntilFinished) {

            }
        }.start();

    }

    private void openControlActivity(){
        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra("PREDATE_EXTRA", nowDateTime);
        intent.putExtra("PREAIRQUALITY_EXTRA", String.valueOf(airQualityData));
        intent.putExtra("PREGASSENSOR_EXTRA", String.valueOf(gasSensorData));
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }
}