package com.example.youtubefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button createuser;
    private TextView banner;
    private EditText nameField, emailField, agefield, passwordField, passwordConfirmField;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.textView2);
        banner.setOnClickListener(this);

        createuser = (Button) findViewById(R.id.createAccountButton);
        createuser.setOnClickListener(this);
        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        agefield = (EditText) findViewById(R.id.agefield);
        passwordField = (EditText) findViewById(R.id.passwordfield);
        passwordConfirmField = (EditText) findViewById(R.id.confirmpasswordfield);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView2:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.createAccountButton:
                registerUser();
                break;
        }
    }

    private void registerUser(){
        String email = emailField.getText().toString().trim();
        String name = nameField.getText().toString().trim();
        String age = agefield.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String passwordConfirm = passwordConfirmField.getText().toString().trim();

        if (name.isEmpty()){
            nameField.setError("Name is required");
            nameField.requestFocus();
            return;
        }
        if (age.isEmpty()){
            agefield.setError("age is required");
            agefield.requestFocus();
            return;
        }
        if (email.isEmpty()){
            emailField.setError("emai is not valid");
            emailField.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm) || password.isEmpty() || password.length() < 6){
            passwordField.setError("Both passwords should match");
            passwordField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailField.setError("Must be a valid email");
            emailField.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(email, name, age);

                            Map<String, Object> userfriends = new HashMap<>();
                            List<String> emptyList = new ArrayList<>();

                            userfriends.put("Friends", emptyList);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Friends").document(email)
                                    .set(userfriends)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference("Friends").child(FirebaseAuth.getInstance().getUid())
                                    .setValue(emptyList);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this, "User has been registered successfuly", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else {
                                        Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "Failed to register try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}