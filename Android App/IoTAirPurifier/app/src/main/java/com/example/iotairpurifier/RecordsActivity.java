package com.example.iotairpurifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    ArrayList<String> recordID, postDate, preAirQuality, preGasSensor, postAirQuality, postGasSensor;
    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    MyDatabaseHelper myDB;
    FloatingActionButton homeFab;
    String ipAddress;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");

        recyclerView = findViewById(R.id.recyclerView);
        homeFab = findViewById(R.id.homeButton);
        homeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        myDB = new MyDatabaseHelper(RecordsActivity.this);
        myDB.deleteRowsBeyond7Days();
        recordID = new ArrayList<>();
        postDate = new ArrayList<>();
        preAirQuality = new ArrayList<>();
        preGasSensor = new ArrayList<>();
        postAirQuality = new ArrayList<>();
        postGasSensor = new ArrayList<>();
        storeDataInArrays();

        customAdapter = new CustomAdapter(RecordsActivity.this, recordID,
                postDate, preAirQuality, preGasSensor, postAirQuality, postGasSensor);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RecordsActivity.this));
    }

    private void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No available records", Toast.LENGTH_LONG).show();
        }else{
            while (cursor.moveToNext()){
                recordID.add(cursor.getString(0));
                postDate.add(cursor.getString(4));
                preAirQuality.add(cursor.getString(2));
                preGasSensor.add(cursor.getString(3));
                postAirQuality.add(cursor.getString(5));
                postGasSensor.add(cursor.getString(6));
            }
        }
    }

    private void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }
}