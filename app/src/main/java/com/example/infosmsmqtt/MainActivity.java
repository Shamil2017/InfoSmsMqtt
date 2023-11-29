package com.example.infosmsmqtt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private  Integer Qos = 0;
    private  String  host, port, topic, clientID, mes;
    private MqttAndroidClient client;
    TextView subText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        host = "broker.mqttdashboard.com";
        port = "1883";
        topic = "Dream/shamil1";
        clientID = MqttClient.generateClientId().toString();


        try {
            Toast.makeText(getApplicationContext(), "connect", Toast.LENGTH_SHORT).show();
            connect();
        }
        catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


    }
  /*  public void published(View v){


        String message = "the payload";
        try {
            client.publish(topic, message.getBytes(),0,false);
            Toast.makeText(this,"Published Message",Toast.LENGTH_SHORT).show();
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    private void setSubscription(){

        try{

            client.subscribe(topic,0);


        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"connected!!",Toast.LENGTH_LONG).show();
                    setSubscription();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connection failed!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
*/




    private void connect() {

        client =
                new MqttAndroidClient(MainActivity.this, "tcp://" + host + ":"+ port,
                        clientID);
        try {
            IMqttToken token = client.connect();
            Log.d(TAG, "client.connect");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                    Toast.makeText(getApplicationContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    // подписываемся на сообщения по данному топику
                    sub();


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {

            Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void sub() {
        try {
            client.subscribe(topic, Qos);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG,"messages: " + new String(message.getPayload()));
                    Log.d(TAG,"topic: " + topic);


                    notification(new String(message.getPayload()));

                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void notification(String s) {
        Intent intent = new Intent(this, DelayedMessageService.class);
        intent.putExtra(DelayedMessageService.EXTRA_MESSAGE,
                s);
        startService(intent);
    }

    public void disconn(View view){

        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Disconnected!!",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}