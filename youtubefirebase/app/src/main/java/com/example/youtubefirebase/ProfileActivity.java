package com.example.youtubefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userId;

    private Button addFriend;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        addFriend = (Button) findViewById(R.id.addFriend);
        logout = (Button) findViewById(R.id.signOut);

        addFriend.setOnClickListener(this);
        logout.setOnClickListener(this);
        //
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//                    case R.id.signOut:
//                        FirebaseAuth.getInstance().signOut();
//                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
//                        break;
//                    case R.id.addFriend:
//                        startActivity(new Intent(ProfileActivity.this, FindFriends.class));
//                }
//
//            }
//        });
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        userId = user.getUid();

        final TextView fullnametextview = (TextView) findViewById(R.id.FullName);
        final TextView emailTextView = (TextView) findViewById(R.id.Email);
        final TextView ageTextView = (TextView) findViewById(R.id.Age);



        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userprofile = snapshot.getValue(User.class);
                if (userprofile != null) {
                    String fullname = userprofile.name;
                    String age = userprofile.age;
                    String email = userprofile.email;

                    fullnametextview.setText("Name: " + fullname);
                    emailTextView.setText("email: " + email);
                    ageTextView.setText("age: " + age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened,", Toast.LENGTH_LONG ).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        break;
                    case R.id.addFriend:
                        startActivity(new Intent(ProfileActivity.this, FindFriends.class));
                }
    }
}