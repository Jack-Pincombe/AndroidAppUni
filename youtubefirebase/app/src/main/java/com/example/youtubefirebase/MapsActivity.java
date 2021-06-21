package com.example.youtubefirebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.example.youtubefirebase.utilities.RideTrackingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RideTrackingService service;
    private String lat;
    private String longtitude;
    private FirebaseUser m_user;
    private FirebaseFunctions mFunctions;
    private List m_friends;
    private HashMap m_locations;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            service = (IBinder) ((RideTrackingService.Binder1)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        m_user = FirebaseAuth.getInstance().getCurrentUser();
        mFunctions = FirebaseFunctions.getInstance();

        if (m_user.getEmail().contains("test")){
            mFunctions.useEmulator("10.0.2.2", 5001);
        } else {
            mFunctions.useEmulator("192.168.0.24", 5001);
        }

        // TODO get the current location of the rider
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lat = intent.getStringExtra("lat");
                longtitude = intent.getStringExtra("longtitude");
            }
        }, new IntentFilter("maptest"));
        // TODO create the map that is going to contain the locations of the users friends
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        /**
         * call to cloud and get the friends location map
         * plot each location on map
         */


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(54.6, -5.9);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        HashMap map = (HashMap) getFriendsMap();



        // TODO create markers with the coords of the locations of the friends
    }

    private HashMap getFriendsLocations(){
        Map<Object, Object> data = new HashMap<>();

        data.put("text", m_user.getEmail());
        data.put("push", true);

        mFunctions.getHttpsCallable("getLocations")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap map = (HashMap) task.getResult().getData();
//                        m_friends = map;
                        return map;
                    }
                });
        return null;
    }

    /**
     * method that will return a map of the users friends and their locations, will be called every
     * 15 seconds
     * @return map containing the lcoations of the users friends
     */
    public Map<Object, Object> getFriendsMap(){
        Map<Object, Object> data = new HashMap<>();

        data.put("text", m_user.getEmail());
        data.put("push", true);

        mFunctions.getHttpsCallable("getFriends")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap map = (HashMap) task.getResult().getData();
                        m_friends = (List) map.get("friends");
                        return map;
                    }
                });

        return null;
    }

    /**
     * method that is going to plot the friends locations on the map
     */
    private void plotFriendsLocations(Map<Object,Object> friendsLocations){

    }
}