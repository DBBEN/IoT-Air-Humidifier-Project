package com.example.iotairpurifier;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketService extends Service {

    private IBinder binder = new WebSocketBinder();
    private WebSocketClient wss;
    private URI uri;
    private int airQualityData, gasSensorData;
    private boolean isConnected = false;
    private String ipAddress;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class WebSocketBinder extends Binder{
        WebSocketService getService(){
            return WebSocketService.this;
        }
    }

    public void startConnection(){
        try {
            uri = new URI("ws://" + ipAddress + ":81");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        wss = new WebSocketClient(uri, new Draft_6455()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                isConnected = true;
            }

            @Override
            public void onMessage(String message) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObj = new JSONObject(message);
                            airQualityData = jsonObj.getInt("airquality");
                            gasSensorData = jsonObj.getInt("co2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                isConnected = false;
            }

            @Override
            public void onError(Exception ex) {
                isConnected = false;
            }
        };
        wss.connect();
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getAirQualityData() {
        return airQualityData;
    }

    public int getGasSensorData() {
        return gasSensorData;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void send(String i){
        wss.send(i);
    }
}
