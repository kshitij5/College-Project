package com.example.gasleakage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpashActivity extends AppCompatActivity {

    public static boolean isTrue = true;
    public int number = LoginActivity.values();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if (isChannelIdValid()) {
                    i = new Intent(SpashActivity.this, MainActivity.class);
                } else {
                    i = new Intent(SpashActivity.this, LoginActivity.class);
                }
                startActivity(i);
                finish();
            }
        }, 1000);
    }

    public boolean isChannelIdValid() {
        try {
            // Construct the URL for the ThingSpeak API endpoint

            URL url = new URL("https://api.thingspeak.com/channels/" + number + "/feeds.json");

            // Create an HTTP connection to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Retrieve the HTTP response code
            int responseCode = connection.getResponseCode();

            // Check the response code to see if the channel ID is valid
            if (responseCode == 200) {
                // Channel ID is valid
                return isTrue;
            } else if (responseCode == 404) {
                // Channel ID is invalid
                return false;
            }
        } catch (IOException e) {
            // Error connecting to the API
            return false;
        }
        return false;
    }
}