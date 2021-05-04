package com.example.iotairpurifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
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
import java.util.Calendar;

import javax.net.ssl.SSLContext;

public class ProgressActivity extends AppCompatActivity {
    int AUTO_MODE = 1;
    int MAN_MODE = 2;
    boolean isConnected, isFinnish = false;



    WebSocketClient mWebSocketClient;
    CircularProgressBar progressBar;
    TextView progressText;
    String ipAddress, preDate, preAirQuality, preGasSensor, postDate;
    int duration, durationMinutes = 0;
    int postairQualityData, postgasSensorData;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        progressBar = (CircularProgressBar)findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        duration = getIntent().getIntExtra("DURATION_EXTRA", 0);
        ipAddress = getIntent().getStringExtra("IPADDRESS_EXTRA");
        preDate = getIntent().getStringExtra("PREDATE_EXTRA");
        preAirQuality = getIntent().getStringExtra("PREAIRQUALITY_EXTRA");
        preGasSensor = getIntent().getStringExtra("PREGASSENSOR_EXTRA");

        duration = 60 * (1000 * duration);
        progressBar.setProgressMax(duration);
        progressBar.setProgress(duration);

        if(isFinnish)this.finish();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("CompleteNotification", "IoTAirHumidifierNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        connectWebSocket();

        new CountDownTimer(duration, 50) {
            @Override
            public void onFinish() {
                mWebSocketClient.send("AP_OFF");
                isFinnish = true;
                progressText.setText("COMPLETE");
                progressText.setTextColor(getResources().getColor(R.color.light_green));
                progressBar.setProgressBarColor(getResources().getColor(R.color.light_green));

                NotificationCompat.Builder builder = new NotificationCompat.Builder(ProgressActivity.this, "CompleteNotification");
                builder.setContentTitle("IoT Air Purifier");
                durationMinutes = duration / 60000;
                if(durationMinutes > 1) {
                    builder.setContentText("Air purification for " + durationMinutes + " minutes is complete");
                }else{
                    builder.setContentText("Air purification for " + durationMinutes + " minute is complete");
                }

                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                builder.setSmallIcon(R.drawable.ic_done);
                builder.setLargeIcon(BitmapFactory.decodeResource(ProgressActivity.this.getResources(), R.mipmap.ic_launcher));
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                builder.setAutoCancel(true);

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), FinalActivity.class)
                                .putExtra("IPADDRESS_EXTRA", ipAddress)
                                .putExtra("DURATION_EXTRA", duration)
                                .putExtra("PREAIRQUALITY_EXTRA", preAirQuality)
                                .putExtra("PREGASSENSOR_EXTRA", preGasSensor)
                                .putExtra("PREDATE_EXTRA", preDate)
                                .putExtra("POSTAIRQUALITY_EXTRA", String.valueOf(postairQualityData))
                                .putExtra("POSTGASSENSOR_EXTRA", String.valueOf(postgasSensorData))
                                .putExtra("POSTDATE_EXTRA", postDate), PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentIntent(contentIntent);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
                mWebSocketClient.close();

                new CountDownTimer(1500, 500){

                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        postDate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        finalActivity();
                    }
                }.start();
            }

            @Override
            public void onTick(long l) {
                progressBar.setProgress(progressBar.getProgressMax() - l);
            }


        }.start();
    }

    private void connectWebSocket(){
        URI uri;
        final String url = "ws://" + ipAddress + ":81";
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_6455()) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                isConnected = true;
                Log.i("WebSocket", "Opened");
            }

            @Override
            public void onMessage(String message) {
                isConnected = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(message);
                            postairQualityData = jsonObj.getInt("airquality");
                            postgasSensorData = jsonObj.getInt("co2");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                isConnected = false;
                Log.i("WebSocket", "Disconnected");
            }

            @Override
            public void onError(Exception ex) {
                isConnected = false;
                Log.i("WebSocket", "Error: " + ex.getMessage());
            }
        };

        mWebSocketClient.connect();
    }

    private void finalActivity(){
        Intent intent = new Intent(this, FinalActivity.class);
        intent.putExtra("IPADDRESS_EXTRA", ipAddress);
        intent.putExtra("DURATION_EXTRA", duration);

        intent.putExtra("PREAIRQUALITY_EXTRA", preAirQuality);
        intent.putExtra("PREGASSENSOR_EXTRA", preGasSensor);
        intent.putExtra("PREDATE_EXTRA", preDate);

        intent.putExtra("POSTAIRQUALITY_EXTRA", String.valueOf(postairQualityData));
        intent.putExtra("POSTGASSENSOR_EXTRA", String.valueOf(postgasSensorData));
        intent.putExtra("POSTDATE_EXTRA", postDate);
        startActivity(intent);
    }
}