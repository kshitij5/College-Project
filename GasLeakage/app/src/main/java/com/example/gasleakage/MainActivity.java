package com.example.gasleakage;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    RequestQueue mQueue;
    TextView textView;
    Handler mHandler;
    WebView webView;
    NavigationView navigationView;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://thingspeak.com/channels/1953855/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15&width=" + webView.getWidth() + "&height=" + webView.getHeight());

        textView = findViewById(R.id.textView);
        mQueue = Volley.newRequestQueue(this);
        mHandler = new Handler();

        navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Settings Activity", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.quit) {
//                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTextView();
                mHandler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void updateTextView() {
        String url = "https://api.thingspeak.com/channels/1953855/feeds.json?results=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray feeds = response.getJSONArray("feeds");
                            JSONObject feed = feeds.getJSONObject(0);
                            String field1 = feed.getString("field1");
                            field1 = field1.replaceAll("[^\\d.]", "");
                            float value = Float.parseFloat(field1);

                            String[] datetime = feed.getString("created_at").split("T",0);
                            datetime[1] = datetime[1].replace("Z","");

                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat2 = new SimpleDateFormat("hh:mm:ss aa");
                            String date = null, time = null;
                            try {
                                Date data = dateFormat.parse(datetime[0]);
                                assert data != null;
                                date = dateFormat2.format(data);

                                Date data2 = timeFormat.parse(datetime[1]);
                                assert data2 != null;
                                time = timeFormat2.format(data2);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            textView.setText("Last gas Level: " + value + " ppm\n" + "At\n" +
                                    "Date: "+ date + "\n" + "Time: " + time);
                            if (value > 280) {
                                showNotification(value);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    // Method to display a notification if the value is greater than 280
    private void showNotification(float value) {
        String channelId = "gas_level";
        String channelName = "Gas Level";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Leakage Alert")
                .setContentText("The gas level is greater than 280: " + value)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_SOUND);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
   }
}
