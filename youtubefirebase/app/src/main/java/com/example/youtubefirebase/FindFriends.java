package com.example.youtubefirebase;

import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class FindFriends extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private Button addButton, showFriendsButton, showPending;
    private EditText searchName;
    private HashMap mFriendData;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchName = (EditText) findViewById(R.id.friendSearch);
        showFriendsButton = (Button) findViewById(R.id.showFriends);
        showPending = (Button) findViewById(R.id.pendingButton);
        addButton = (Button) findViewById(R.id.addButton);
        user = FirebaseAuth.getInstance().getCurrentUser();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mFunctions = FirebaseFunctions.getInstance();
        mFunctions.useEmulator("10.0.2.2", 5001);

        getFriendData(user.getEmail());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = searchName.getText().toString().trim();
                Task<String> exists = userExists(email);
            }
        });

        showFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateTables();
            }
        });

        showPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePendingTables();
            }
        });
    }

    /**
     * Method that is going to populate the friends and pending tables off of the data from
     * cloud
     */
    private void populateTables(){
        TableLayout tl = (TableLayout) findViewById(R.id.friendTable);
        tl.removeAllViews();
        TableRow row;
        TextView view;

        for (int i = 0; i < 4; i++) {
            row = new TableRow(getApplicationContext());
            for (int j = 0; j < 3; j++) {
                view = new TextView(getApplicationContext());
                view.setText("friend");
                view.setPadding(20, 20, 20, 20);
                row.addView(view);
            }
            tl.addView(row);
        }
//        setContentView(tl);
    }

    private void populatePendingTables(){
        TableLayout tl = (TableLayout) findViewById(R.id.friendTable);
        tl.removeAllViews();
        TableRow row;
        TextView view;

        for (int i = 0; i < 4; i++) {
            row = new TableRow(getApplicationContext());
            for (int j = 0; j < 3; j++) {
                view = new TextView(getApplicationContext());
                view.setText("pending");
                view.setPadding(20, 20, 20, 20);
                row.addView(view);
            }
            tl.addView(row);
        }
//        setContentView(tl);
    }

    /**
     * Method that will get the current users friends and pending friend requests
     *
     * @param userEmail email of the current user
     */
    private void getFriendData(String userEmail){
        Map<String, Object> data = new HashMap<>();

        data.put("text", "jackpincombe@hotmail.co.uk");
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

    private Task<String> userExists(String text){
        Map<String, Object> data = new HashMap<>();

        data.put("text", text);
        data.put("push", true);

        return mFunctions.getHttpsCallable("friendExiststest").call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}