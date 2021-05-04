package com.example.iotairpurifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private WebSocketClient mWebSocketClient;
    //private WebSocketService service;
    private RelativeLayout buttonLayout;
    private EditText ipInput;
    private Button onButton, offButton;
    public boolean isConnected = false;
    String ipAddress;
    int airQualityData, gasSensorData, hardwareButtonStat;
    int previousState, nowState = 0;

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            WebSocketService.WebSocketBinder binder = (WebSocketService.WebSocketBinder) iBinder;
//            service = binder.getService();
//            isBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            isBound = false;
//        }
//    };

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        Intent intent = new Intent(this, WebSocketService.class);
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if(isBound && service != null){
//            unbindService(serviceConnection);
//        }
//    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onButton = findViewById(R.id.onButton);
        offButton = findViewById(R.id.offButton);
        buttonLayout = findViewById(R.id.buttonLayout);
        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");

        connectWebSocket();


        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected) {
                    mWebSocketClient.send("LEDON");
                    openShowDataActivity();
                }else {
                    Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected) {
                    mWebSocketClient.send("LEDOFF");
                    //service.send("LEDOFF");
                }else {
                    Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addConnection) {
            //Toast.makeText(this, "Add connection", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
            mydialog.setTitle("Air Purifier IP Address");

            ipInput = new EditText(MainActivity.this);
            ipInput.setInputType(InputType.TYPE_CLASS_PHONE);
            ipInput.setText(ipAddress);
            mydialog.setView(ipInput);

            mydialog.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ipAddress = ipInput.getText().toString();
                    //service.setIpAddress(ipAddress);
                    //service.startConnection();
                    connectWebSocket();
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
        return super.onOptionsItemSelected(item);
    }

    private void connectWebSocket() {
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
                isConnected = true;
                mWebSocketClient.send("AP_OFF");
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(message);
                            airQualityData = jsonObj.getInt("airquality");
                            gasSensorData = jsonObj.getInt("co2");
                            hardwareButtonStat = jsonObj.getInt("switch");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(isConnected && deviceSwitchIsToggled()){
                            skipControlActivity();
                        }

                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                //Toast.makeText(MainActivity.this, "Connection terminated", Toast.LENGTH_SHORT).show();
                isConnected = false;
            }

            @Override
            public void onError(Exception ex) {
                //Toast.makeText(MainActivity.this, "Connection unsuccessful", Toast.LENGTH_SHORT).show();
                isConnected = false;
            }
        };
        mWebSocketClient.connect();
    }

    private boolean deviceSwitchIsToggled(){
        nowState = hardwareButtonStat;
        if(nowState != previousState){
            previousState = nowState;
            return true;
        }else{
            previousState = nowState;
            return false;
        }

    }

    private void openShowDataActivity(){
        mWebSocketClient.close();
        Intent intent = new Intent(this, ShowDataActivity.class);
        intent.putExtra("AIRQUALITY_EXTRA", airQualityData);
        intent.putExtra("GASSENSOR_EXTRA", gasSensorData);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        startActivity(intent);
    }

    private void skipControlActivity(){
        mWebSocketClient.close();
        Intent intent = new Intent(this, ControlActivity.class);
        intent.putExtra("PREAIRQUALITY_EXTRA", String.valueOf(airQualityData));
        intent.putExtra("PREGASSENSOR_EXTRA", String.valueOf(gasSensorData));
        intent.putExtra("IPADDRESS_EXTRA", String.valueOf(ipAddress));
        startActivity(intent);
    }
}
