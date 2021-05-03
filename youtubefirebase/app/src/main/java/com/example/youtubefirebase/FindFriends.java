package com.example.youtubefirebase;

import android.os.Bundle;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
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
import java.util.List;
import java.util.Map;

public class FindFriends extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private Button addButton, showFriendsButton, showPending;
    private EditText searchName;
    private HashMap mFriendData;
    private FirebaseUser user;
    private Task<GetTokenResult> mToken;

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

        mFunctions = FirebaseFunctions.getInstance();

        if (user.getEmail().contains("test")){
            mFunctions.useEmulator("10.0.2.2", 5001);
        } else {
            mFunctions.useEmulator("192.168.0.24", 5001);
        }

        getFriendData(user.getEmail());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = searchName.getText().toString().trim();
                sendRequest(email);
            }
        });

        showFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateFriendsTables();
            }
        });

        showPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePendingTables();
            }
        });
    }

    private void sendRequest(String friendsEmail){


        String token = mToken.getResult().getToken();
        Map<String, Object> data = new HashMap<>();
        data.put("friend", friendsEmail);
//        data.put("id", token);
        data.put("email", user.getEmail());
        data.put("push", true);

        mFunctions.getHttpsCallable("sendFriendRequest")
                .call(data);
    }
    /**
     * Method that is going to populate the friends and pending tables off of the data from
     * cloud
     */
    private void populateFriendsTables(){
        TableLayout tl = (TableLayout) findViewById(R.id.friendTable);
        tl.removeAllViews();
        TableRow row, titleRow;
        TextView view, titleViewEmail, tileViewButton;

        List<String> friends = (List<String>) mFriendData.get("friends");

        titleRow = new TableRow(getApplicationContext());
        titleViewEmail = new TextView(getApplicationContext());
        titleViewEmail.setText("Friend Email");
        titleViewEmail.setPadding(20, 20, 20, 20);

        tileViewButton = new TextView(getApplicationContext());
        tileViewButton.setText("Remove");
        tileViewButton.setPadding(20, 20, 20, 20);

        titleRow.addView(titleViewEmail);
        titleRow.addView(tileViewButton);

        tl.addView(titleRow);
        for (String email : friends) {
            row = new TableRow(getApplicationContext());
            view = new TextView(getApplicationContext());
            view.setText(email);
            view.setPadding(20, 20, 20, 20);

            TextView button = new Button(getApplicationContext());
            button.setText("Remove Friend");
            button.setPadding(20, 20, 20, 20);

            row.addView(view);
            row.addView(button);
            tl.addView(row);
        }
    }

    private Button createRemoveButton(String email){
        return null;
    }

    private Button createAcceptButton(String email){
        Map<String, Object> data = new HashMap<>();
        data.put("pendingEmail", email);
        data.put("userEmail", user.getEmail());
        data.put("push", true);

        Button acceptButton = new Button(getApplicationContext());
        acceptButton.setText("Accept");
        acceptButton.setPadding(20, 20, 20, 20);


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunctions.getHttpsCallable("acceptFriendRequest")
                        .call(data);
            }
        });

        return acceptButton;
    }

    private Button createRejectButton(String email){
        Map<String, Object> data = new HashMap<>();
        data.put("pendingEmail", email);
        data.put("userEmail", user.getEmail());
        data.put("push", true);

        Button rejectButton = new Button(getApplicationContext());
        rejectButton.setText("Reject");
        rejectButton.setPadding(20, 20, 20, 20);


        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFunctions.getHttpsCallable("rejectFriendRequest")
                        .call(data);
            }
        });

        return rejectButton;
    }

    private void populatePendingTables(){
        TableLayout tl = (TableLayout) findViewById(R.id.friendTable);
        tl.removeAllViews();
        TableRow row, titleRow;
        TextView view, titleViewEmail, tileViewAcceptButton, titleViewRejectButton;

        List<String> friends = (List<String>) mFriendData.get("pending");

        titleRow = new TableRow(getApplicationContext());
        titleViewEmail = new TextView(getApplicationContext());
        titleViewEmail.setText("Email");
        titleViewEmail.setPadding(20, 20, 20, 20);

        tileViewAcceptButton = new TextView(getApplicationContext());
        tileViewAcceptButton.setText("Accept");
        tileViewAcceptButton.setPadding(20, 20, 20, 20);

        titleViewRejectButton = new TextView(getApplicationContext());
        titleViewRejectButton.setText("Reject");
        titleViewRejectButton.setPadding(20, 20, 20, 20);

        titleRow.addView(titleViewEmail);
        titleRow.addView(tileViewAcceptButton);
        titleRow.addView(titleViewRejectButton);

        tl.addView(titleRow);
        for (String email : friends) {
            row = new TableRow(getApplicationContext());
            view = new TextView(getApplicationContext());
            view.setText(email);
            view.setPadding(20, 20, 20, 20);

            Button acceptButton = createAcceptButton(email);
            Button rejectButton = createRejectButton(email);

            row.addView(view);
            row.addView(acceptButton);
            row.addView(rejectButton);
            tl.addView(row);
        }
    }

    /**
     * Method that will get the current users friends and pending friend requests
     *
     * @param userEmail email of the current user
     */
    private void getFriendData(String userEmail){
        Map<String, Object> data = new HashMap<>();

        data.put("text", user.getEmail());
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

//    private void userExists(String text){
//        Map<String, Object> data = new HashMap<>();
//
//        data.put("text", text);
//        data.put("push", true);
//
//        mFunctions.getHttpsCallable("friendExists").call(data)
//                .continueWith(new Continuation<HttpsCallableResult, Void>() {
//                    @Override
//                    public Void then(@NonNull Task<HttpsCallableResult> task) throws Exception {
//                        String result = (String) task.getResult().getData();
//                    }
//                });
//    }
}