package com.example.youtubefirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.youtubefirebase.utilities.RideTrackingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class StartRide extends AppCompatActivity {
    private FirebaseFunctions mFunctions;
    private FirebaseUser mUser;
    private Button startRideButton, stopRideButton;
    private Intent trackingIntent;
    private TextView status;

    /**
     * method that is going to be stopping the tracking of the user
     */
    public void stopTracking() {
        Map<String, String> data = new HashMap<>();
        data.put("email", mUser.getEmail());
        mFunctions.getHttpsCallable("stopTrackingRider").call(data);

        if (isCurrentlyTracking()) {
            stopService(trackingIntent);
        }
        status.setText("Stopped tracking");
    }

    /**
     * method that is going to start the service that will track the user
     */
    public void startTracking() {
        if (!isCurrentlyTracking()) {
            trackingIntent = new Intent(this, RideTrackingService.class);
            startService(trackingIntent);
            status.setText("Currently tracking");
        }
    }

    private boolean isCurrentlyTracking() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.youtubefirebase.utilities.RideTrackingService".equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        status = findViewById(R.id.status_view);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFunctions = FirebaseFunctions.getInstance();
        startRideButton = findViewById(R.id.startridebutton);
        stopRideButton = findViewById(R.id.stopridebutton);
        RideTrackingService service;
        if (mUser.getEmail().contains("test") || mUser.getEmail().contains("fake") || mUser.getEmail().contains("a")) {
            mFunctions.useEmulator("10.0.2.2", 5001);
        } else {
              mFunctions.useEmulator("192.168.0.24", 5001);
        }

        if (isCurrentlyTracking()) {
            status.setText("Currently Tracking");
        } else {
            status.setText("Currently not tracking");
        }

        startRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        stopRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });
    }
}

