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
import android.os.Handler;
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

import java.util.ArrayList;
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
    HashMap<String, HashMap> friendsLocationList = new HashMap<String, HashMap>();


    private HashMap mFriendData;
    private HashMap friendLocation;

    private Handler m_handler;
    Runnable m_GetFriendLocationsHandler;

    Runnable m_plotFriendLocation;

    /**
     * process of getting the locatoins of the users friends and then plotting them to the map
     *
     * 1 get a list of the friends
     * 2 iterate through the list and request that users locations
     * 3 then iterate over list and then plot the coords on teh map
     */

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

        if (m_user.getEmail().contains("test") || m_user.getEmail().contains("b")){
            mFunctions.useEmulator("10.0.2.2", 5001);
        } else {
            mFunctions.useEmulator("192.168.0.24", 5001);
        }

        getFriendData();
        // TODO get the current location of the rider
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lat = intent.getStringExtra("lat");
                longtitude = intent.getStringExtra("longtitude");
            }
        }, new IntentFilter("maptest"));

        m_handler = new Handler();

        m_plotFriendLocation = new Runnable() {
            @Override
            public void run() {
                if (!(friendsLocationList == null)){

                    if (!(mMap == null)) {
                        mMap.clear();
                    }
                    // attempt to plot the data onto the map
                    for (String x : friendsLocationList.keySet()){

                        try {
                            System.out.println(x);
                            List latlist = (List) friendsLocationList.get(x).get("lat");
                            List longlist = (List) friendsLocationList.get(x).get("longtitude");

                            double lat = (double) latlist.get(0);
                            double longtitude = (double) longlist.get(0);
                            if (lat != 0 && longtitude != 0) {
                                LatLng userLocation = new LatLng(lat, longtitude);
                                mMap.addMarker(new MarkerOptions().position(userLocation).title(x));
                            }
                        } catch (Exception e){
                            System.out.println("passing on the user: " + x);
                            continue;
                        }
}
                }
                else {
                    m_handler.removeCallbacks(m_plotFriendLocation);
                }
                m_handler.postDelayed(m_plotFriendLocation, 5000);
            }
        };

        m_GetFriendLocationsHandler = new Runnable() {
            @Override
            public void run() {
                if (!(mFriendData == null)) {
                    System.out.println("gotten the friend data");
                    getFriendsLocations();
                }
                else {
                    m_handler.removeCallbacks(m_GetFriendLocationsHandler);
                }
                m_handler.postDelayed(m_GetFriendLocationsHandler, 1500);
            }
        };

        m_plotFriendLocation.run();
        m_GetFriendLocationsHandler.run();
        // TODO create the map that is going to contain the locations of the users friends
    }

    private void getFriendsLocations(){
        HashMap<String, String> friendsLocation = new HashMap<>();
        List<String> friends = (List<String>) mFriendData.get("friends");

        for (String friend : friends){
            System.out.println("looking for friend: " + friend);

            Map<String, String> data = new HashMap<>();
            data.put("email", friend);

            mFunctions
                    .getHttpsCallable("getFriendLocation")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                        @Override
                        public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            HashMap map = (HashMap) task.getResult().getData();
                            friendLocation = map;
                            friendsLocationList.put(friend, friendLocation);
                            return map;
                        }
                    });
        }
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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(54.6, -5.9);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        HashMap map = (HashMap) getFriendsMap();



        // TODO create markers with the coords of the locations of the friends
    }


    private void getFriendData(){
        Map<String, Object> data = new HashMap<>();

        data.put("text", m_user.getEmail());
        data.put("push", true);

        mFunctions
                .getHttpsCallable("getFriends")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap>() {
                    @Override
                    public HashMap then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap map = (HashMap) task.getResult().getData();
                        mFriendData = map;
                        return map;
                    }
                });
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


}