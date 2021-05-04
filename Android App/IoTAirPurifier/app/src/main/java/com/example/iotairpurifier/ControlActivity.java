package com.example.iotairpurifier;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


public class ControlActivity extends AppCompatActivity {
    int AUTO_MODE = 1;
    int MAN_MODE = 2;

    int AUTO_DURATION = 60;

    WebSocketClient mWebSocketClient;
    FloatingActionButton homeFab;
    EditText durationInput;
    Button automaticButton, manualButton;
    String ipAddress, preDate, preAirQuality, preGasSensor;
    int processMode, duration;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        connectWebSocket();
//    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        homeFab = findViewById(R.id.homeButton);
        automaticButton = findViewById(R.id.automaticButton);
        manualButton = findViewById(R.id.manualButton);

        preDate = getIntent().getStringExtra("PREDATE_EXTRA");
        preAirQuality = getIntent().getStringExtra("PREAIRQUALITY_EXTRA");
        preGasSensor = getIntent().getStringExtra("PREGASSENSOR_EXTRA");
        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");

        connectWebSocket();

        homeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        automaticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processMode = AUTO_MODE;
                duration = AUTO_DURATION;
                openProgressActivity();
                //mWebSocketClient.close();
            }
        });

        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processMode = MAN_MODE;
                AlertDialog.Builder mydialog = new AlertDialog.Builder(ControlActivity.this);
                mydialog.setTitle("Manual Mode");
                mydialog.setMessage("Enter number of minutes");

                durationInput = new EditText(ControlActivity.this);
                durationInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                durationInput.setHint("maximum of 180 minutes");
                mydialog.setView(durationInput);

                mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        duration = Integer.parseInt(durationInput.getText().toString());
                        if(duration > 180){
                            //nothing
                        }else {
                            openProgressActivity();
                        }
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
        });
    }

    private void openProgressActivity(){
        mWebSocketClient.send("AP_ON");
        mWebSocketClient.close();
        Intent intent = new Intent(this, ProgressActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        intent.putExtra("DURATION_EXTRA", duration);
        intent.putExtra("PREAIRQUALITY_EXTRA", preAirQuality);
        intent.putExtra("PREGASSENSOR_EXTRA", preGasSensor);
        intent.putExtra("PREDATE_EXTRA", preDate);
        startActivity(intent);
    }

    private void connectWebSocket(){
        URI uri;

        try {
            uri = new URI("ws://" + ipAddress + ":81");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }


        mWebSocketClient = new WebSocketClient(uri, new Draft_6455()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                mWebSocketClient.send("AP_OFF");
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            }

            @Override
            public void onError(Exception ex) {

            }
        };
        mWebSocketClient.connect();
        //mWebSocketClient.send("AP_OFF");
    }

    private void goHome(){
        mWebSocketClient.close();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }
}