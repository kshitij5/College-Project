package com.example.gasleakage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    Button button;
    EditText text;
    public int number;
    public static int number2;
//    private static final boolean isTrue = true;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button = findViewById(R.id.button);
        text = (EditText) findViewById(R.id.editTextNumber);
        String value = text.getText().toString();
        number = Integer.parseInt(value);
        number2 = number;
    }

    public void ifChannelIdValid() {
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else if (responseCode == 404) {
                // Channel ID is invalid
                Toast.makeText(LoginActivity.this, "Invalid channel ID", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // Error connecting to the API
            Toast.makeText(LoginActivity.this, "Error connecting to ThingSpeak API", Toast.LENGTH_SHORT).show();
        }
    }

    public static int values(){
        return number2;
    }

}