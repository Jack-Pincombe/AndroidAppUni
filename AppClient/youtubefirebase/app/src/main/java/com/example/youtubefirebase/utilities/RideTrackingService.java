package com.example.youtubefirebase.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 *Class that is going to start collecting the gps location and then pushing that data to the cloud
 * will run as a background service
 */
public class RideTrackingService extends Service implements LocationListener {
    protected LocationManager locationManager;
    private double longtitude, lat;
    private FirebaseFunctions mFunctions;
    private FirebaseUser user;
    public final IBinder localBinder = new Binder1();

    public class Binder1 extends Binder{

        public RideTrackingService getService(){
            return RideTrackingService.this;
        }
    }


    public RideTrackingService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public void onCreate(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFunctions = FirebaseFunctions.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.getEmail().contains("test") || user.getEmail().contains("fake") || user.getEmail().contains("a")){
            mFunctions.useEmulator("10.0.2.2", 5001);

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart(Intent intent, int startId){
        // do something
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, (LocationListener) this);
    };

    public Map<String, Double> getLocation(){
        Map<String, Double> map = new HashMap();

        map.put("lat", lat);
        map.put("long", longtitude);

        return map;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

       lat = location.getLatitude();
       longtitude = location.getLongitude();

        System.out.println(lat);
        System.out.println(longtitude);

        Map<String, Object> mapToUpload = new HashMap<>();
        mapToUpload.put("lat", lat);
        mapToUpload.put("longtitude", longtitude);
        mapToUpload.put("user", user.getEmail());

        Intent intent = new Intent("maptest");
        intent.putExtra("lat", String.valueOf(lat));
        intent.putExtra("long", String.valueOf(longtitude));

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        uploadToCloud(mapToUpload);
    }

    private void uploadToCloud(Map<String, Object> mapToUpload) {
        mFunctions.getHttpsCallable("updateUserLocation").call(mapToUpload);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


}
