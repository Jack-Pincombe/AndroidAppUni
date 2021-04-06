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

import java.util.HashMap;
import java.util.Map;

public class FindFriends extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private Button addButton;
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

        addButton = (Button) findViewById(R.id.addButton);
        FloatingActionButton fab = findViewById(R.id.fab);
        user = FirebaseAuth.getInstance().getCurrentUser();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mFunctions = FirebaseFunctions.getInstance();
        mFunctions.useEmulator("10.0.2.2", 5001);

        getFriendData(user.getEmail());
//        mFriendData = Data.getResult();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = searchName.getText().toString().trim();
                Task<String> exists = userExists(email);
            }
        });
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