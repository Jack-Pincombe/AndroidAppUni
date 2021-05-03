package com.example.youtubefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.youtubefirebase.utilities.RideTrackingService;

import java.util.Map;

public  class StartRide extends AppCompatActivity {

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private String lat, longtitue;
    TextView coord;
    private Button startRideButton;
    RideTrackingService trackingService;

    private final static int time = 1000 * 60 * 2;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        startRideButton = findViewById(R.id.startridebutton);

        startService(new Intent(this, RideTrackingService.class));
        }
    }

